package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.YearMonth
import java.util.Locale
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

data class ManadsRapportTexter(
    val manadArRubrik: String,
    val ingaRegistreradeDagar: String,
    val summaArbetadEtikett: String,
    val summaUtfallEtikett: String,
    val saldoManadsslutEtikett: String,
    val kolumnRubriker: List<String>,
    val typEtiketter: Map<DagTyp, String>,
    val veckodagEtiketter: Map<DayOfWeek, String>,
    val saknasTecken: String,
    val normaltidManadEtikett: String = "",
)

data class ManadsRapportRad(
    val datum: String,
    val veckodag: String,
    val start: String,
    val slut: String,
    val rast: String,
    val arbetad: String,
    val utfall: String,
    val typ: String,
)

data class ManadsRapportData(
    val rubrik: String,
    val rader: List<ManadsRapportRad>,
    val summaArbetad: String?,
    val summaUtfall: String?,
    val saldoManadsslut: String,
    val ingaDagar: Boolean,
    val arbetsdagarManad: Int = 0,
    val normaltidManad: String = "",
)

fun arKomplettDag(dag: Dag): Boolean = dag.start != null && dag.slut != null

fun saldoVidOchMed(
    dagar: List<Dag>,
    installning: Installning,
    tillOchMed: java.time.LocalDate,
    aftnarLediga: Boolean = true,
    arbetstid: Arbetstid = installning.arbetstid(aftnarLediga),
): Int {
    val summaUtfall =
        dagar
            .filter { it.datum <= tillOchMed }
            .sumOf { dagensUtfall(it, arbetstid) }
    return installning.ingangssaldoMin + summaUtfall
}

fun byggManadsRapport(
    dagar: List<Dag>,
    installning: Installning,
    arManad: YearMonth,
    texter: ManadsRapportTexter,
    locale: Locale = Locale.getDefault(),
    aftnarLediga: Boolean = true,
    arbetstid: Arbetstid = installning.arbetstid(aftnarLediga),
): ManadsRapportData {
    val arbetsdagar =
        arbetsdagarIManad(
            arManad.year,
            arManad.monthValue,
            installning.arbetsdagarMask,
            uteslutRodaDagar = true,
            uteslutAftnar = aftnarLediga,
        )
    val sistaDagIManaden = arManad.atEndOfMonth()
    val saldo = saldoVidOchMed(dagar, installning, sistaDagIManaden, aftnarLediga, arbetstid)
    val iManaden =
        dagar
            .filter { YearMonth.from(it.datum) == arManad }
            .sortedBy { it.datum }

    var summaArbetadMin = 0
    var summaUtfallMin = 0

    val rader =
        iManaden.map { dag ->
            val komplett = arKomplettDag(dag)
            val arbetadMin =
                if (komplett) {
                    arbetadeMinuter(dag.start, dag.slut, dag.rastMin)
                } else {
                    null
                }
            val utfallMin =
                when {
                    dag.typ != DagTyp.ARBETE -> 0
                    komplett ->
                        dagensUtfall(dag, arbetstid)
                    else -> null
                }

            when {
                dag.typ != DagTyp.ARBETE -> {
                    summaUtfallMin += 0
                }
                komplett -> {
                    summaUtfallMin += utfallMin!!
                    summaArbetadMin += arbetadMin!!
                }
            }

            ManadsRapportRad(
                datum = dag.datum.toString(),
                veckodag = texter.veckodagEtiketter[dag.datum.dayOfWeek] ?: dag.datum.dayOfWeek.name,
                start = tidEllerSaknas(dag.start, texter.saknasTecken, locale),
                slut =
                    when {
                        dag.start != null && dag.slut == null -> texter.saknasTecken
                        else -> tidEllerSaknas(dag.slut, texter.saknasTecken, locale)
                    },
                rast = dag.rastMin.toString(),
                arbetad =
                    if (dag.typ == DagTyp.ARBETE && komplett) {
                        formateraMinuterSomTid(arbetadMin!!, locale)
                    } else if (dag.typ == DagTyp.ARBETE) {
                        texter.saknasTecken
                    } else {
                        texter.saknasTecken
                    },
                utfall =
                    when (utfallMin) {
                        null -> texter.saknasTecken
                        else -> formateraSaldoMinuter(utfallMin, locale)
                    },
                typ = texter.typEtiketter[dag.typ] ?: dag.typ.name,
            )
        }

    return ManadsRapportData(
        rubrik = texter.manadArRubrik,
        rader = rader,
        summaArbetad =
            if (rader.isEmpty()) {
                null
            } else {
                formateraMinuterSomTid(summaArbetadMin, locale)
            },
        summaUtfall =
            if (rader.isEmpty()) {
                null
            } else {
                formateraSaldoMinuter(summaUtfallMin, locale)
            },
        saldoManadsslut = formateraSaldoMinuter(saldo, locale),
        ingaDagar = rader.isEmpty(),
        arbetsdagarManad = arbetsdagar,
        normaltidManad =
            formateraMinuterSomTid(arbetstid.normaltidForIntervall(arManad.atDay(1), sistaDagIManaden), locale),
    )
}

private fun tidEllerSaknas(
    tid: java.time.LocalTime?,
    saknas: String,
    locale: Locale,
): String {
    val formaterad = formateraTid(tid, locale)
    return formaterad.ifEmpty { saknas }
}
