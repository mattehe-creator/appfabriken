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

/** Marginal så att ett dygnsjobb som WorkManager startar några minuter för tidigt inte hoppar över en dag. */
private val INTERVALL_MARGINAL_MS = TimeUnit.HOURS.toMillis(2)

/**
 * Dags för en ny kopia när ingen gjorts än, eller när det gått ungefär [intervallDagar] sedan senaste
 * (två timmars marginal, eftersom WorkManager bara garanterar ungefär 24 timmar mellan körningar).
 */
fun arSakerhetskopiaDags(senaste: Long?, nu: Long, intervallDagar: Int): Boolean {
    if (senaste == null) return true
    return nu - senaste >= TimeUnit.DAYS.toMillis(intervallDagar.toLong().coerceAtLeast(1)) - INTERVALL_MARGINAL_MS
}

/** Datumdelen av filnamnet (ISO, sorterar kronologiskt). Leverantörens "(1)" och ändringstid spelar ingen roll här. */
private fun datumNyckel(namn: String): String = namn.removePrefix(AUTO_FILPREFIX).take(10)

/** Äldsta automatiska kopior utöver de [behall] senaste. Filer som användaren själv lagt i mappen rörs aldrig. */
fun postAttRadera(poster: List<MappPost>, behall: Int = AUTO_BEHALL_ANTAL): List<MappPost> =
    poster
        .filter { it.namn.startsWith(AUTO_FILPREFIX) && it.namn.endsWith(".zip") }
        // Filnamnets datum först: vissa leverantörer rapporterar ingen ändringstid, och då får den nya kopian aldrig gallras.
        .sortedWith(compareByDescending<MappPost> { datumNyckel(it.namn) }.thenByDescending { it.andrad })
        .drop(behall.coerceAtLeast(1))

/**
 * Skriver en ny kopia i [lagring] och gallrar gamla. Den nya filen skrivs först; först när den lyckats tas en
 * tidigare kopia från samma dag bort (mappens leverantör kan ha lagt till "(1)" i det nya namnet). Misslyckas
 * skrivningen tas bara den halvfärdiga filen bort, och dagens äldre kopia finns kvar. Returnerar `true` om det gick.
 */
fun kopiera(lagring: MappLagring, skrivZip: (OutputStream) -> Unit, idag: LocalDate): Boolean {
    val namn = autoFilnamn(idag)
    val fore = lagring.lista()
    val foreIdn = fore.map { it.id }.toSet()
    val ut = lagring.skapa(namn) ?: return false
    try {
        ut.use(skrivZip)
    } catch (_: Exception) {
        lagring.lista().filter { it.id !in foreIdn }.forEach { lagring.radera(it) }
        return false
    }
    fore.filter { it.namn == namn }.forEach { lagring.radera(it) }
    postAttRadera(lagring.lista()).forEach { lagring.radera(it) }
    return true
}
