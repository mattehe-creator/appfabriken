package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Betalning via Stripe Checkout (engångsbetalning per utkast).
 * Av som standard: aktiv bara när STRIPE_SECRET_KEY och STYRELSERUMMET_BAS_URL finns.
 * Utkastet visas alltid; CSV och utskrift kräver betalning när betalningen är aktiv.
 * Inga kortuppgifter passerar servern; Stripe tar emot dem på sin egen sida.
 */
data class BetalKonfig(
    val stripeNyckel: String?,
    val basUrl: String?,
    val prisOre: Long = STANDARDPRIS_ORE,
    val valuta: String = "sek",
    val produktnamn: String = "K3-komponentlista, utkast",
) {
    val aktiv: Boolean get() = !stripeNyckel.isNullOrBlank() && !basUrl.isNullOrBlank()

    companion object {
        const val STANDARDPRIS_ORE = 99_000L
    }
}

fun betalKonfigFranMiljo(env: Map<String, String> = System.getenv()): BetalKonfig =
    BetalKonfig(
        stripeNyckel = env["STRIPE_SECRET_KEY"]?.trim()?.takeIf { it.isNotEmpty() },
        basUrl = env["STYRELSERUMMET_BAS_URL"]?.trim()?.trimEnd('/')?.takeIf { it.startsWith("https://") },
        prisOre = env["STYRELSERUMMET_PRIS_ORE"]?.trim()?.toLongOrNull()?.takeIf { it in 100L..10_000_000L }
            ?: BetalKonfig.STANDARDPRIS_ORE,
    )

@Serializable
data class BetalStatus(
    val aktiv: Boolean,
    val betald: Boolean,
    val prisKr: Long,
)

fun betalStatus(konfig: BetalKonfig, meta: JobbMeta): BetalStatus =
    BetalStatus(aktiv = konfig.aktiv, betald = meta.betald, prisKr = konfig.prisOre / 100)

/** Får jobbets CSV och utskrift lämnas ut? */
fun farLamnaUt(konfig: BetalKonfig, meta: JobbMeta): Boolean = !konfig.aktiv || meta.betald

data class Kassa(val id: String, val url: String)

sealed interface KassaStatus {
    data object Betald : KassaStatus
    data object EjBetald : KassaStatus
    data object Fel : KassaStatus
}

interface Betalare {
    fun skapaKassa(jobbId: String): Kassa?
    fun kontrollera(kassaId: String, jobbId: String): KassaStatus
}

/** Minsta HTTP-yta mot Stripe, så att testerna kan använda en falsk. */
interface StripeHttp {
    fun post(url: String, nyckel: String, formular: String): HttpSvarEnkelt
    fun get(url: String, nyckel: String): HttpSvarEnkelt
}

data class HttpSvarEnkelt(val statusKod: Int, val kropp: String)

class JavaStripeHttp(
    private val klient: java.net.http.HttpClient = java.net.http.HttpClient.newBuilder()
        .connectTimeout(java.time.Duration.ofSeconds(10))
        .build(),
) : StripeHttp {
    private fun bygg(url: String, nyckel: String) =
        java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .timeout(java.time.Duration.ofSeconds(20))
            .header("Authorization", "Bearer $nyckel")

    override fun post(url: String, nyckel: String, formular: String): HttpSvarEnkelt {
        val req = bygg(url, nyckel)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(formular))
            .build()
        val svar = klient.send(req, java.net.http.HttpResponse.BodyHandlers.ofString())
        return HttpSvarEnkelt(svar.statusCode(), svar.body())
    }

    override fun get(url: String, nyckel: String): HttpSvarEnkelt {
        val req = bygg(url, nyckel).GET().build()
        val svar = klient.send(req, java.net.http.HttpResponse.BodyHandlers.ofString())
        return HttpSvarEnkelt(svar.statusCode(), svar.body())
    }
}

private val KASSA_ID = Regex("^cs_[A-Za-z0-9_]{8,200}$")

fun arGiltigtKassaId(id: String): Boolean = KASSA_ID.matches(id)

class StripeBetalare(
    private val konfig: BetalKonfig,
    private val http: StripeHttp = JavaStripeHttp(),
    private val logg: (String) -> Unit = ::println,
) : Betalare {
    private val json = Json { ignoreUnknownKeys = true }

    fun kassaFormular(jobbId: String): String {
        val bas = konfig.basUrl ?: ""
        val falt = listOf(
            "mode" to "payment",
            "locale" to "sv",
            "client_reference_id" to jobbId,
            "metadata[jobb]" to jobbId,
            "line_items[0][quantity]" to "1",
            "line_items[0][price_data][currency]" to konfig.valuta,
            "line_items[0][price_data][unit_amount]" to konfig.prisOre.toString(),
            "line_items[0][price_data][product_data][name]" to konfig.produktnamn,
            "invoice_creation[enabled]" to "true",
            "success_url" to "$bas/j/$jobbId?kassa={CHECKOUT_SESSION_ID}",
            "cancel_url" to "$bas/j/$jobbId",
        )
        return falt.joinToString("&") { (k, v) -> "${koda(k)}=${koda(v).replace("%7BCHECKOUT_SESSION_ID%7D", "{CHECKOUT_SESSION_ID}")}" }
    }

    override fun skapaKassa(jobbId: String): Kassa? {
        val nyckel = konfig.stripeNyckel ?: return null
        return try {
            val svar = http.post("https://api.stripe.com/v1/checkout/sessions", nyckel, kassaFormular(jobbId))
            if (svar.statusKod !in 200..299) {
                logg("Stripe: kunde inte skapa kassa (status ${svar.statusKod})")
                return null
            }
            val obj = json.parseToJsonElement(svar.kropp) as? JsonObject ?: return null
            val id = obj["id"]?.jsonPrimitive?.content ?: return null
            val url = obj["url"]?.jsonPrimitive?.content ?: return null
            if (!arGiltigtKassaId(id) || !url.startsWith("https://")) return null
            Kassa(id, url)
        } catch (e: Exception) {
            logg("Stripe: fel vid skapande av kassa (${e.javaClass.simpleName})")
            null
        }
    }

    override fun kontrollera(kassaId: String, jobbId: String): KassaStatus {
        val nyckel = konfig.stripeNyckel ?: return KassaStatus.Fel
        if (!arGiltigtKassaId(kassaId)) return KassaStatus.Fel
        return try {
            val svar = http.get("https://api.stripe.com/v1/checkout/sessions/$kassaId", nyckel)
            if (svar.statusKod !in 200..299) return KassaStatus.Fel
            val obj = json.parseToJsonElement(svar.kropp) as? JsonObject ?: return KassaStatus.Fel
            val referens = obj["client_reference_id"]?.jsonPrimitive?.content
            if (referens != jobbId) return KassaStatus.Fel
            when (obj["payment_status"]?.jsonPrimitive?.content) {
                "paid", "no_payment_required" -> KassaStatus.Betald
                else -> KassaStatus.EjBetald
            }
        } catch (e: Exception) {
            logg("Stripe: fel vid kontroll av kassa (${e.javaClass.simpleName})")
            KassaStatus.Fel
        }
    }

    private fun koda(s: String): String = URLEncoder.encode(s, StandardCharsets.UTF_8)
}

/** Sida som visas när utskriften öppnas innan jobbet är betalt. [jobbId] är redan kontrollerat. */
fun betalningKravsHtml(jobbId: String): String =
    """
    <!DOCTYPE html>
    <html lang="sv">
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <title>Betalning krävs — Styrelserummet</title>
      <link rel="stylesheet" href="/gemensam.css">
    </head>
    <body>
      <div class="ram">
        <h1>Betalning krävs för utskrift</h1>
        <p>${Felmeddelanden.forklara(Felkoder.BETALNING_KRAVS)}</p>
        <p><a class="knapp" href="/j/$jobbId">Tillbaka till utkastet</a></p>
      </div>
    </body>
    </html>
    """.trimIndent()
