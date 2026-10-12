package se.tmconnect.garantivalvet.regler

import java.time.DateTimeException
import java.time.LocalDate

/** Förslag som lästs ur kvittotexten. Fält som inte hittades är `null`. */
data class KvittoForslag(
    val butik: String?,
    val datum: LocalDate?,
    val prisOre: Long?,
) {
    val arTomt: Boolean get() = butik == null && datum == null && prisOre == null
}

private val ISO_DATUM = Regex("""(?<!\d)(\d{4})[-./](\d{1,2})[-./](\d{1,2})(?!\d)""")
private val DAG_FORST_DATUM = Regex("""(?<![\d.,/-])(\d{1,2})[-./](\d{1,2})[-./](\d{4}|\d{2})(?![\d.,/-])""")
private val BELOPP = Regex("""(?<![\d.,])(\d{1,3}(?:[  .]\d{3})+(?:[.,]\d{1,2})?|\d+(?:[.,]\d{1,2})?)(?![\d])""")

private val PRIS_PRIORITET = listOf(
    Regex("""att betala|amount due|to pay"""),
    Regex("""\b(summa|totalt|total|belopp|sum)\b"""),
)
private val PRIS_UTESLUT = Regex("""moms|mva|vat|tax|subtotal|delsumma|varav|rabatt|växel|vaxel|change""")

private val BUTIK_UTESLUT = Regex(
    """^(kvitto|kassakvitto|receipt|org|tel|telefon|moms|datum|date|summa|total|totalt|välkommen|tack|www|http|kund|kassa)\b""",
)

private const val MAX_AR_TILLBAKA = 10L
private const val MAX_BUTIK_TECKEN = 40

fun tolkaKvitto(text: String, idag: LocalDate): KvittoForslag {
    val rader = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
    return KvittoForslag(
        butik = hittaButik(rader),
        datum = hittaDatum(text, idag),
        prisOre = hittaPris(rader),
    )
}

internal fun hittaDatum(text: String, idag: LocalDate): LocalDate? {
    val tidigast = idag.minusYears(MAX_AR_TILLBAKA)
    val kandidater = mutableListOf<Pair<Int, LocalDate?>>()
    ISO_DATUM.findAll(text).forEach { m ->
        kandidater += m.range.first to skapaDatum(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt())
    }
    DAG_FORST_DATUM.findAll(text).forEach { m ->
        val ar = m.groupValues[3].let { if (it.length == 2) 2000 + it.toInt() else it.toInt() }
        kandidater += m.range.first to skapaDatum(ar, m.groupValues[2].toInt(), m.groupValues[1].toInt())
    }
    return kandidater
        .sortedBy { it.first }
        .mapNotNull { it.second }
        .firstOrNull { !it.isAfter(idag) && !it.isBefore(tidigast) }
}

private fun skapaDatum(ar: Int, manad: Int, dag: Int): LocalDate? =
    try {
        LocalDate.of(ar, manad, dag)
    } catch (_: DateTimeException) {
        null
    }

internal fun hittaPris(rader: List<String>): Long? {
    for (monster in PRIS_PRIORITET) {
        var traff: Long? = null
        rader.forEachIndexed { i, rad ->
            val lag = rad.lowercase()
            if (monster.containsMatchIn(lag) && !PRIS_UTESLUT.containsMatchIn(lag)) {
                val ore = sistaBelopp(rad) ?: rader.getOrNull(i + 1)?.takeIf { belopRad(it) }?.let { sistaBelopp(it) }
                if (ore != null) traff = ore
            }
        }
        if (traff != null) return traff
    }
    return null
}

private fun belopRad(rad: String): Boolean =
    BELOPP.containsMatchIn(rad) && rad.count { it.isLetter() } <= 4

private fun sistaBelopp(rad: String): Long? =
    BELOPP.findAll(rad).mapNotNull { tolkaBeloppTillOre(it.value) }.lastOrNull()

/** `1 299,00`, `1.299,00`, `349.90`, `1299` och `1,299` ger ören; `null` om värdet inte är ett positivt belopp. */
internal fun tolkaBeloppTillOre(text: String): Long? {
    val rent = text.replace(" ", "").replace(" ", "")
    val sista = rent.lastIndexOfAny(charArrayOf(',', '.'))
    val (heltal, ore) = if (sista == -1) {
        rent to ""
    } else {
        val efter = rent.substring(sista + 1)
        if (efter.length == 3 && rent.count { it == ',' || it == '.' } >= 1 && efter.all { it.isDigit() }) {
            rent.filter { it.isDigit() } to ""
        } else {
            rent.substring(0, sista).filter { it.isDigit() } to efter
        }
    }
    if (heltal.isEmpty() || heltal.length > 9) return null
    val oren = when (ore.length) {
        0 -> 0L
        1 -> ore.toLong() * 10
        2 -> ore.toLong()
        else -> return null
    }
    val total = heltal.toLong() * 100 + oren
    return total.takeIf { it > 0 }
}

internal fun hittaButik(rader: List<String>): String? =
    rader.firstOrNull { rad ->
        val lag = rad.lowercase()
        rad.count { it.isLetter() } >= 3 &&
            !BUTIK_UTESLUT.containsMatchIn(lag) &&
            !ISO_DATUM.containsMatchIn(rad) &&
            !DAG_FORST_DATUM.containsMatchIn(rad) &&
            rad.count { it.isLetter() } > rad.count { it.isDigit() }
    }?.take(MAX_BUTIK_TECKEN)?.trim()

/** Text för prisfältet i formuläret: `1299` eller `349,90` (kronor). */
fun prisOreTillFaltText(ore: Long): String {
    val kr = ore / 100
    val rest = (ore % 100).toInt()
    return if (rest == 0) kr.toString() else "%d,%02d".format(kr, rest)
}

/** Formulärfälten efter att förslag lagts in, och vilka som kom från kvittot. */
data class Ifyllning(
    val varKopt: String,
    val prisText: String,
    val kopdatum: LocalDate,
    val butikFranKvitto: Boolean,
    val prisFranKvitto: Boolean,
    val datumFranKvitto: Boolean,
)

/**
 * Lägger bara in förslag i tomma fält: butik och pris om de är blanka, datum om det fortfarande är
 * standardvärdet (dagens datum). Det användaren redan skrivit eller valt ändras aldrig.
 */
fun fyllTommaFalt(forslag: KvittoForslag, varKopt: String, prisText: String, kopdatum: LocalDate, idag: LocalDate): Ifyllning {
    val butik = forslag.butik?.takeIf { varKopt.isBlank() }
    val pris = forslag.prisOre?.takeIf { prisText.isBlank() }
    val datum = forslag.datum?.takeIf { kopdatum == idag && it != kopdatum }
    return Ifyllning(
        varKopt = butik ?: varKopt,
        prisText = pris?.let { prisOreTillFaltText(it) } ?: prisText,
        kopdatum = datum ?: kopdatum,
        butikFranKvitto = butik != null,
        prisFranKvitto = pris != null,
        datumFranKvitto = datum != null,
    )
}
