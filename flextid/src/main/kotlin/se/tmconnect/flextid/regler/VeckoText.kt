package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.flextid.data.Dag

data class VeckoTextTexter(
    val rubrik: String,
    val veckodagar: Map<DayOfWeek, String>,
    val typEtiketter: Map<DagTyp, String>,
    val summaEtikett: String,
)

fun veckoText(
    dagar: List<Dag>,
    mandag: LocalDate,
    texter: VeckoTextTexter,
    locale: Locale,
): String {
    val dagMap = dagar.associateBy { it.datum }
    val rader = mutableListOf<String>()
    rader.add(texter.rubrik)

    var summaArbetadeMin = 0

    for (offset in 0L..6L) {
        val datum = mandag.plusDays(offset)
        val dag = dagMap[datum] ?: continue
        val veckodag = texter.veckodagar[datum.dayOfWeek] ?: continue

        when {
            dag.typ == DagTyp.ARBETE && dag.start != null && dag.slut != null -> {
                val arbetat = arbetadeMinuter(dag.start, dag.slut, dag.rastMin)
                summaArbetadeMin += arbetat
                val start = formateraTid(dag.start, locale)
                val slut = formateraTid(dag.slut, locale)
                val arbetatText = formateraMinuterSomTid(arbetat, locale)
                rader.add("$veckodag $start–$slut ($arbetatText)")
            }
            dag.typ == DagTyp.ARBETE && dag.start != null -> {
                val start = formateraTid(dag.start, locale)
                rader.add("$veckodag $start–")
            }
            else -> {
                val etikett = texter.typEtiketter[dag.typ] ?: dag.typ.name
                rader.add("$veckodag $etikett")
            }
        }
    }

    rader.add("${texter.summaEtikett} ${formateraMinuterSomTid(summaArbetadeMin, locale)}")
    return rader.joinToString("\n")
}
