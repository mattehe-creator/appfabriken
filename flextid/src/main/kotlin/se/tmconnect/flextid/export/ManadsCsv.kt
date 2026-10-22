package se.tmconnect.flextid.export

import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.Helgdag
import se.tmconnect.flextid.regler.arbetadeMinuter
import se.tmconnect.flextid.regler.dagensUtfall
import se.tmconnect.flextid.regler.formateraMinuterSomTid
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.flextid.regler.helgdagPa
import se.tmconnect.karna.export.csvDokument
import se.tmconnect.karna.export.csvRad

data class ManadsCsvTexter(
    val rubriker: List<String>,
    val typEtiketter: Map<DagTyp, String> = emptyMap(),
    val helgdagNamn: (Helgdag) -> String = { it.namn },
)

fun manadsCsvFilnamn(arManad: YearMonth): String =
    "flextid-${arManad.year}-${arManad.monthValue.toString().padStart(2, '0')}.csv"

fun byggManadsCsv(
    dagar: List<Dag>,
    installning: Installning,
    arManad: YearMonth,
    texter: ManadsCsvTexter,
    locale: Locale = Locale.getDefault(),
): String {
    val iManaden =
        dagar.filter { dag ->
            val ym = YearMonth.from(dag.datum)
            ym == arManad
        }.sortedBy { it.datum }
    val rader = buildList {
        add(csvRad(texter.rubriker))
        iManaden.forEach { dag ->
            val arbetat = arbetadeMinuter(dag.start, dag.slut, dag.rastMin)
            val utfall =
                dagensUtfall(dag, installning.normaltidMinPerDag, installning.arbetsdagarMask)
            add(
                csvRad(
                    listOf(
                        dag.datum.toString(),
                        formateraTid(dag.start, locale),
                        formateraTid(dag.slut, locale),
                        dag.rastMin.toString(),
                        formateraMinuterSomTid(arbetat, locale),
                        formateraSaldoCell(utfall, locale),
                        texter.typEtiketter[dag.typ] ?: dag.typ.name,
                        helgdagPa(dag.datum, medAftnar = true)?.let(texter.helgdagNamn) ?: "",
                    ),
                ),
            )
        }
    }
    return csvDokument(rader)
}

private fun formateraSaldoCell(minuter: Int, locale: Locale): String {
    val tecken = when {
        minuter > 0 -> "+"
        minuter < 0 -> "-"
        else -> ""
    }
    return tecken + formateraMinuterSomTid(kotlin.math.abs(minuter), locale)
}
