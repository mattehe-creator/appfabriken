package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag

fun avrundaTillHelMinut(tid: LocalTime): LocalTime = tid.withSecond(0).withNano(0)

sealed interface PlaneradStamplaIn {
    data class Spara(val dag: Dag) : PlaneradStamplaIn

    data class Oforandrad(val start: LocalTime) : PlaneradStamplaIn

    data object GransUtanPro : PlaneradStamplaIn
}

sealed interface PlaneradStamplaUt {
    data class Spara(val dag: Dag) : PlaneradStamplaUt

    data object IngenInstampling : PlaneradStamplaUt

    data class Oforandrad(val slut: LocalTime) : PlaneradStamplaUt

    data class OgiltigTid(val fel: TidValideringFel) : PlaneradStamplaUt
}

fun planeraStamplaIn(
    befintlig: Dag?,
    datum: LocalDate,
    nu: LocalTime,
    standardRastMin: Int,
    antalDagar: Int,
    arPro: Boolean,
): PlaneradStamplaIn {
    if (befintlig?.start != null) {
        return PlaneradStamplaIn.Oforandrad(befintlig.start!!)
    }
    if (befintlig == null && !kanLaggaTillDagPost(antalDagar, arPro)) {
        return PlaneradStamplaIn.GransUtanPro
    }
    val start = avrundaTillHelMinut(nu)
    val dag =
        (befintlig ?: Dag(datum = datum, rastMin = standardRastMin)).copy(
            start = start,
            typ = befintlig?.typ ?: DagTyp.ARBETE,
        )
    return PlaneradStamplaIn.Spara(dag)
}

fun planeraStamplaUt(
    befintlig: Dag?,
    nu: LocalTime,
): PlaneradStamplaUt {
    if (befintlig?.start == null) {
        return PlaneradStamplaUt.IngenInstampling
    }
    if (befintlig.slut != null) {
        return PlaneradStamplaUt.Oforandrad(befintlig.slut!!)
    }
    val slut = avrundaTillHelMinut(nu)
    val dag = befintlig.copy(slut = slut)
    val fel = valideraTider(dag.start, dag.slut)
    if (fel != null) {
        return PlaneradStamplaUt.OgiltigTid(fel)
    }
    return PlaneradStamplaUt.Spara(dag)
}
