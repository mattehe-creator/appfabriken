package se.tmconnect.karna.sakerhetskopia

import java.io.OutputStream
import java.time.LocalDate
import java.util.concurrent.TimeUnit

const val AUTO_BEHALL_ANTAL = 5
const val AUTO_INTERVALL_DAGLIGEN = 1
const val AUTO_INTERVALL_VECKOVIS = 7
const val AUTO_INTERVALL_MANADSVIS = 30

data class AutoBackupTillstand(
    val pa: Boolean = false,
    val mappNamn: String? = null,
    val intervallDagar: Int = AUTO_INTERVALL_VECKOVIS,
    val senaste: Long? = null,
    val fel: Boolean = false,
)

data class MappPost(
    val id: String,
    val namn: String,
    val andrad: Long,
)

interface MappLagring {
    fun lista(): List<MappPost>

    fun skapa(namn: String): OutputStream?

    fun radera(post: MappPost): Boolean
}

fun autoFilnamn(datum: LocalDate, filPrefix: String, filAndelse: String = ".zip"): String =
    "$filPrefix$datum$filAndelse"

private val INTERVALL_MARGINAL_MS = TimeUnit.HOURS.toMillis(2)

fun arSakerhetskopiaDags(senaste: Long?, nu: Long, intervallDagar: Int): Boolean {
    if (senaste == null) return true
    return nu - senaste >= TimeUnit.DAYS.toMillis(intervallDagar.toLong().coerceAtLeast(1)) - INTERVALL_MARGINAL_MS
}

private fun datumNyckel(namn: String, filPrefix: String): String = namn.removePrefix(filPrefix).take(10)

fun postAttRadera(
    poster: List<MappPost>,
    filPrefix: String,
    behall: Int = AUTO_BEHALL_ANTAL,
    filAndelse: String = ".zip",
): List<MappPost> =
    poster
        .filter { it.namn.startsWith(filPrefix) && it.namn.endsWith(filAndelse) }
        .sortedWith(compareByDescending<MappPost> { datumNyckel(it.namn, filPrefix) }.thenByDescending { it.andrad })
        .drop(behall.coerceAtLeast(1))

fun kopiera(
    lagring: MappLagring,
    filPrefix: String,
    skrivZip: (OutputStream) -> Unit,
    idag: LocalDate,
    filAndelse: String = ".zip",
): Boolean {
    val namn = autoFilnamn(idag, filPrefix, filAndelse)
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
    postAttRadera(lagring.lista(), filPrefix, filAndelse = filAndelse).forEach { lagring.radera(it) }
    return true
}
