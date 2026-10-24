package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.MonthDay
import se.tmconnect.flextid.data.Installning

/**
 * Kortare (eller längre) normaltid under en period varje år, till exempel sommartid
 * 1 juni–31 augusti. Perioden får gå över årsskiftet (1 december–28 februari).
 */
data class SommarNormaltid(
    val fran: MonthDay,
    val till: MonthDay,
    val minPerDag: Int,
) {
    fun omfattar(datum: LocalDate): Boolean {
        val md = MonthDay.from(datum)
        return if (!fran.isAfter(till)) {
            !md.isBefore(fran) && !md.isAfter(till)
        } else {
            !md.isBefore(fran) || !md.isAfter(till)
        }
    }
}

/**
 * Allt som avgör normaltiden en viss dag.
 *
 * Ordning: röda dagar och lediga aftnar har ingen normaltid; sedan gäller sommartiden
 * om dagen ligger i perioden; sedan veckodagens egen normaltid; annars standard.
 */
data class Arbetstid(
    val normaltidMinPerDag: Int,
    val arbetsdagarMask: Int,
    val aftnarLediga: Boolean = true,
    val perVeckodag: Map<DayOfWeek, Int> = emptyMap(),
    val sommar: SommarNormaltid? = null,
) {
    fun arArbetsdag(datum: LocalDate): Boolean = arArbetsdagMedHelger(datum, arbetsdagarMask, aftnarLediga)

    /** Normaltiden om dagen är en arbetsdag, utan hänsyn till veckodagar och röda dagar. */
    fun normaltidOmArbete(datum: LocalDate): Int =
        (sommar?.takeIf { it.omfattar(datum) }?.minPerDag
            ?: perVeckodag[datum.dayOfWeek]
            ?: normaltidMinPerDag).coerceAtLeast(0)

    /** Normaltiden för dagen: 0 för lediga dagar. */
    fun normaltidFor(datum: LocalDate): Int = if (arArbetsdag(datum)) normaltidOmArbete(datum) else 0

    fun normaltidForIntervall(fran: LocalDate, till: LocalDate): Int {
        var summa = 0
        var datum = fran
        while (!datum.isAfter(till)) {
            summa += normaltidFor(datum)
            datum = datum.plusDays(1)
        }
        return summa
    }

    /** Sant när normaltiden varierar mellan dagar (veckodagar eller sommartid). */
    val harVariation: Boolean get() = perVeckodag.isNotEmpty() || sommar != null
}

/** Extra regler för normaltid som ligger i inställningarna (inte i databasen). */
data class NormaltidUndantag(
    val perVeckodag: Map<DayOfWeek, Int> = emptyMap(),
    val sommar: SommarNormaltid? = null,
)

fun Installning.arbetstid(
    aftnarLediga: Boolean = true,
    undantag: NormaltidUndantag = NormaltidUndantag(),
): Arbetstid =
    Arbetstid(
        normaltidMinPerDag = normaltidMinPerDag,
        arbetsdagarMask = arbetsdagarMask,
        aftnarLediga = aftnarLediga,
        perVeckodag = undantag.perVeckodag,
        sommar = undantag.sommar,
    )

const val MAX_NORMALTID_MIN = 24 * 60

/**
 * Längd som text: "7:30", "7.30", "7" (hela timmar) eller "7,5" och "7.5" (decimaltimmar).
 * Tom text eller ogiltig längd ger null. Högst 24 timmar.
 */
fun tolkaLangdText(text: String): Int? {
    val t = text.trim()
    if (t.isEmpty()) return null
    val min =
        when {
            ':' in t -> {
                val (h, m) = t.split(':').let { if (it.size == 2) it[0] to it[1] else return null }
                val timmar = h.toIntOrNull() ?: return null
                val minuter = m.toIntOrNull() ?: return null
                if (minuter !in 0..59 || m.length != 2) return null
                timmar * 60 + minuter
            }
            ',' in t || '.' in t -> {
                val d = t.replace(',', '.').toBigDecimalOrNull() ?: return null
                d.multiply(java.math.BigDecimal(60)).setScale(0, java.math.RoundingMode.HALF_UP).toInt()
            }
            else -> (t.toIntOrNull() ?: return null) * 60
        }
    return min.takeIf { it in 0..MAX_NORMALTID_MIN }
}

/** "MONDAY=480;FRIDAY=420" ↔ karta. Okända eller trasiga delar hoppas över. */
fun formateraPerVeckodag(karta: Map<DayOfWeek, Int>): String =
    karta.entries.sortedBy { it.key }.joinToString(";") { "${it.key.name}=${it.value}" }

fun tolkaPerVeckodag(text: String?): Map<DayOfWeek, Int> {
    if (text.isNullOrBlank()) return emptyMap()
    return text.split(';').mapNotNull { del ->
        val (namn, varde) = del.split('=').takeIf { it.size == 2 }?.let { it[0] to it[1] } ?: return@mapNotNull null
        val dag = DayOfWeek.entries.firstOrNull { it.name == namn.trim() } ?: return@mapNotNull null
        val min = varde.trim().toIntOrNull()?.takeIf { it in 0..MAX_NORMALTID_MIN } ?: return@mapNotNull null
        dag to min
    }.toMap()
}

/** "--06-01" (ISO MonthDay) eller null. */
fun tolkaManadDag(text: String?): MonthDay? =
    text?.let { runCatching { MonthDay.parse(it.trim()) }.getOrNull() }

/** Datum som "1/6" eller "01-06" (dag först) till MonthDay. */
fun tolkaDagManadText(text: String): MonthDay? {
    val delar = text.trim().split('/', '-', '.').filter { it.isNotBlank() }
    if (delar.size != 2) return null
    val dag = delar[0].toIntOrNull() ?: return null
    val manad = delar[1].toIntOrNull() ?: return null
    return runCatching { MonthDay.of(manad, dag) }.getOrNull()
}

fun formateraDagManad(md: MonthDay): String = "${md.dayOfMonth}/${md.monthValue}"
