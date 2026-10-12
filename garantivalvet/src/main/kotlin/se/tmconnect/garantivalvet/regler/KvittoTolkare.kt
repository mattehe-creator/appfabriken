package se.tmconnect.garantivalvet.regler

import java.time.DateTimeException
import java.time.LocalDate

/** Förslag som lästs ur kvittotexten. Fält som inte hittades är `null`. */
data class KvittoForslag(
    val butik: String?,
    val datum: LocalDate?,
    val prisOre: Long?,
    val oppetKopDagar: Int? = null,
) {
    val arTomt: Boolean get() = butik == null && datum == null && prisOre == null && oppetKopDagar == null
}

private val ISO_DATUM = Regex("""(?<!\d)(\d{4})[-./](\d{1,2})[-./](\d{1,2})(?!\d)""")
private val DAG_FORST_DATUM = Regex("""(?<![\d.,/-])(\d{1,2})[-./](\d{1,2})[-./](\d{4}|\d{2})(?![\d.,/-])""")
private val BELOPP = Regex(
    """(?<![\d.,])(\d{1,3}(?:[ \u00A0.]\d{3})+(?:[.,]\d{1,2})?|\d+(?:[.,]\d{1,2})?)(?!\d)(\s*(?:kr|sek|:-)(?![a-zåäö]))?""",
    RegexOption.IGNORE_CASE,
)
private val HAR_DECIMALER = Regex("""[.,]\d{1,2}$""")
private val TID_EFTER = Regex("""^\s+\d{1,2}:\d{2}""")
private val PERIOD_EFTER = Regex("""^\s+[-–]\s+\d""")
private val PERIOD_FORE = Regex("""\d\s+[-–]\s+$""")
private val DATUM_ORD = Regex("""datum|date|tid\b|faktur""")

private val PRIS_PRIORITET = listOf(
    Regex("""att betala|amount due|to pay"""),
    Regex("""\b(summa|totalt|total|sum)\b|bel[oö]pp?\b"""),
)
private val PRIS_UTESLUT = Regex("""moms|mva|vat|tax|subtotal|delsumma|varav|rabatt|växel|vaxel|change|tillbaka|netto""")

private val BUTIK_UTESLUT = Regex(
    """^(kvitto|kassakvitto|receipt|faktura|fak\s?tura|kopia|order|org|tel|telefon|moms|datum|date|summa|total|totalt|välkommen|tack|www|http|kund|kassa|kassör)\b""",
)

private const val MAX_AR_TILLBAKA = 10L
private const val MAX_BUTIK_TECKEN = 40

fun tolkaKvitto(text: String, idag: LocalDate): KvittoForslag {
    val rader = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
    return KvittoForslag(
        butik = hittaButik(rader),
        datum = hittaDatum(text, idag),
        prisOre = hittaPris(rader),
        oppetKopDagar = tolkaOppetKop(text),
    )
}

internal fun hittaDatum(text: String, idag: LocalDate): LocalDate? {
    val tidigast = idag.minusYears(MAX_AR_TILLBAKA)
    class Kandidat(val plats: Int, val datum: LocalDate?, val traff: MatchResult)
    val kandidater = mutableListOf<Kandidat>()
    ISO_DATUM.findAll(text).forEach { m ->
        kandidater += Kandidat(m.range.first, skapaDatum(m.groupValues[1].toInt(), m.groupValues[2].toInt(), m.groupValues[3].toInt()), m)
    }
    DAG_FORST_DATUM.findAll(text).forEach { m ->
        val ar = m.groupValues[3].let { if (it.length == 2) 2000 + it.toInt() else it.toInt() }
        kandidater += Kandidat(m.range.first, skapaDatum(ar, m.groupValues[2].toInt(), m.groupValues[1].toInt()), m)
    }
    return kandidater
        .filter { k -> k.datum != null && !k.datum.isAfter(idag) && !k.datum.isBefore(tidigast) }
        .filterNot { k ->
            // Början eller slut på en period ("2026-08-29 - 2026-08-31") är inte köpdatumet.
            val fore = text.substring((k.traff.range.first - 6).coerceAtLeast(0), k.traff.range.first)
            val efter = text.substring(k.traff.range.last + 1, (k.traff.range.last + 7).coerceAtMost(text.length))
            PERIOD_FORE.containsMatchIn(fore) || PERIOD_EFTER.containsMatchIn(efter)
        }
        .map { k ->
            val efter = text.substring(k.traff.range.last + 1, (k.traff.range.last + 8).coerceAtMost(text.length))
            val radStart = text.lastIndexOf('\n', k.plats) + 1
            val radSlut = text.indexOf('\n', k.plats).let { if (it == -1) text.length else it }
            val rad = text.substring(radStart, radSlut).lowercase()
            var poang = 0
            if (TID_EFTER.containsMatchIn(efter)) poang += 2
            if (DATUM_ORD.containsMatchIn(rad)) poang += 1
            Triple(poang, k.plats, k.datum!!)
        }
        .sortedWith(compareByDescending<Triple<Int, Int, LocalDate>> { it.first }.thenBy { it.second })
        .firstOrNull()
        ?.third
}

private fun skapaDatum(ar: Int, manad: Int, dag: Int): LocalDate? =
    try {
        LocalDate.of(ar, manad, dag)
    } catch (_: DateTimeException) {
        null
    }

private data class Belopp(val ore: Long, val harDecimaler: Boolean, val harValuta: Boolean) {
    /** Heltal utan valuta ("12" i "Totalt 12 tjänster") är oftast ett antal, inte ett pris. */
    val tillrackligtSakert: Boolean get() = harDecimaler || harValuta
}

private fun belopp(rad: String): List<Belopp> =
    BELOPP.findAll(rad).mapNotNull { m ->
        val ore = tolkaBeloppTillOre(m.groupValues[1]) ?: return@mapNotNull null
        Belopp(ore, HAR_DECIMALER.containsMatchIn(m.groupValues[1]), m.groupValues[2].isNotEmpty())
    }.toList()

internal fun hittaPris(rader: List<String>): Long? =
    prisFranNyckelord(rader) ?: vanligasteBelopp(rader)

private fun prisFranNyckelord(rader: List<String>): Long? {
    for (monster in PRIS_PRIORITET) {
        var traff: Long? = null
        rader.forEachIndexed { i, rad ->
            val lag = rad.lowercase()
            if (monster.containsMatchIn(lag) && !PRIS_UTESLUT.containsMatchIn(lag)) {
                // Samma rad först; annars de två närmaste raderna (foton på snedden ger etikett och belopp på skilda rader).
                val ore = belopp(rad).lastOrNull { it.tillrackligtSakert }?.ore
                    ?: (1..2).firstNotNullOfOrNull { d ->
                        rader.getOrNull(i + d)?.takeIf { it.count { c -> c.isLetter() } <= 4 }
                            ?.let { belopp(it).lastOrNull { b -> b.tillrackligtSakert }?.ore }
                    }
                if (ore != null) traff = ore
            }
        }
        if (traff != null) return traff
    }
    return null
}

/** Summan står oftast flera gånger (rad, total, belopp, brutto): ett belopp med ören som återkommer är troligen totalen. */
private fun vanligasteBelopp(rader: List<String>): Long? =
    rader
        .filterNot { PRIS_UTESLUT.containsMatchIn(it.lowercase()) }
        .flatMap { belopp(it) }
        .filter { it.harDecimaler }
        .groupingBy { it.ore }
        .eachCount()
        .filter { it.value >= 2 }
        .entries
        .sortedWith(compareByDescending<Map.Entry<Long, Int>> { it.value }.thenByDescending { it.key })
        .firstOrNull()
        ?.key

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
    rader.asSequence()
        .filter { rad ->
            val lag = rad.lowercase()
            rad.count { it.isLetter() } >= 3 &&
                !BUTIK_UTESLUT.containsMatchIn(lag) &&
                !ISO_DATUM.containsMatchIn(rad) &&
                !DAG_FORST_DATUM.containsMatchIn(rad) &&
                rad.count { it.isLetter() } > rad.count { it.isDigit() }
        }
        .map { rensaButik(it) }
        .firstOrNull { rad -> rad.count { it.isLetter() } >= 3 }
        ?.take(MAX_BUTIK_TECKEN)?.trim()

/** "BIL TEMA SWEDEN AB/LANDSKRONA 160" -> "BIL TEMA SWEDEN AB": avdelning och gatunummer hör inte till namnet. */
private fun rensaButik(rad: String): String =
    rad.substringBefore('/').trim().trimEnd { it.isDigit() || it.isWhitespace() || it == ',' }.trim()

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
    val oppetKopDagar: Int? = null,
    val oppetKopFranKvitto: Boolean = false,
)

/**
 * Lägger bara in förslag i tomma fält: butik och pris om de är blanka, datum om det fortfarande är
 * standardvärdet (dagens datum). Det användaren redan skrivit eller valt ändras aldrig.
 */
fun fyllTommaFalt(
    forslag: KvittoForslag,
    varKopt: String,
    prisText: String,
    kopdatum: LocalDate,
    idag: LocalDate,
    oppetKopDagar: Int? = null,
): Ifyllning {
    val butik = forslag.butik?.takeIf { varKopt.isBlank() }
    val pris = forslag.prisOre?.takeIf { prisText.isBlank() }
    val datum = forslag.datum?.takeIf { kopdatum == idag && it != kopdatum }
    val oppetKop = forslag.oppetKopDagar?.takeIf { oppetKopDagar == null }
    return Ifyllning(
        varKopt = butik ?: varKopt,
        prisText = pris?.let { prisOreTillFaltText(it) } ?: prisText,
        kopdatum = datum ?: kopdatum,
        butikFranKvitto = butik != null,
        prisFranKvitto = pris != null,
        datumFranKvitto = datum != null,
        oppetKopDagar = oppetKop ?: oppetKopDagar,
        oppetKopFranKvitto = oppetKop != null,
    )
}
