package se.tmconnect.garantivalvet.export

import java.io.OutputStream
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/**
 * Automatisk säkerhetskopia till en mapp som användaren valt via systemets filväljare (SAF). Appen skickar inget
 * själv och begär ingen behörighet; den skriver bara dit användaren pekat. Mappen kan ligga hos användarens egen
 * molntjänst. Av som standard.
 */
const val AUTO_BEHALL_ANTAL = 5
const val AUTO_FILPREFIX = "garantivalvet-auto-"
const val AUTO_INTERVALL_DAGLIGEN = 1
const val AUTO_INTERVALL_VECKOVIS = 7
const val AUTO_INTERVALL_MANADSVIS = 30

data class AutoBackupTillstand(
    val pa: Boolean = false,
    val mappNamn: String? = null,
    val intervallDagar: Int = AUTO_INTERVALL_VECKOVIS,
    /** Tid för senaste lyckade kopia (epok, millisekunder), eller `null`. */
    val senaste: Long? = null,
    /** Sant om senaste försöket misslyckades, till exempel när mappen inte längre går att skriva i. */
    val fel: Boolean = false,
)

data class MappPost(
    val id: String,
    val namn: String,
    val andrad: Long,
)

/** Det som behövs av en mapp. Implementeras med SAF på telefonen och som minneslager i tester. */
interface MappLagring {
    fun lista(): List<MappPost>

    /** Skapar en fil och returnerar en ström att skriva i, eller `null` om det inte gick. */
    fun skapa(namn: String): OutputStream?

    fun radera(post: MappPost): Boolean
}

fun autoFilnamn(datum: LocalDate): String = "$AUTO_FILPREFIX$datum.zip"

/** Dags för en ny kopia när ingen gjorts än, eller när det gått minst [intervallDagar] sedan senaste. */
fun arSakerhetskopiaDags(senaste: Long?, nu: Long, intervallDagar: Int): Boolean {
    if (senaste == null) return true
    return nu - senaste >= TimeUnit.DAYS.toMillis(intervallDagar.toLong().coerceAtLeast(1))
}

/** Äldsta automatiska kopior utöver de [behall] senaste. Filer som användaren själv lagt i mappen rörs aldrig. */
fun postAttRadera(poster: List<MappPost>, behall: Int = AUTO_BEHALL_ANTAL): List<MappPost> =
    poster
        .filter { it.namn.startsWith(AUTO_FILPREFIX) && it.namn.endsWith(".zip") }
        .sortedByDescending { it.andrad }
        .drop(behall.coerceAtLeast(1))

/**
 * Skriver en ny kopia i [lagring] och gallrar gamla. En fil med samma namn från samma dag ersätts, annars lägger
 * mappens leverantör till "(1)". Misslyckas skrivningen tas den halvfärdiga filen bort. Returnerar `true` om det gick.
 */
fun kopiera(lagring: MappLagring, skrivZip: (OutputStream) -> Unit, idag: LocalDate): Boolean {
    val namn = autoFilnamn(idag)
    lagring.lista().filter { it.namn == namn }.forEach { lagring.radera(it) }
    val ut = lagring.skapa(namn) ?: return false
    try {
        ut.use(skrivZip)
    } catch (_: Exception) {
        lagring.lista().filter { it.namn == namn }.forEach { lagring.radera(it) }
        return false
    }
    postAttRadera(lagring.lista()).forEach { lagring.radera(it) }
    return true
}
