package se.tmconnect.flextid.regler

import java.time.LocalTime
import se.tmconnect.flextid.data.Dag

fun avrundaTillHelMinut(nu: LocalTime): LocalTime = nu.withSecond(0).withNano(0)

sealed interface GenvagStamplaInBeslut {
    data class SparaStart(val start: LocalTime) : GenvagStamplaInBeslut

    data class RedanInstamplad(val start: LocalTime) : GenvagStamplaInBeslut
}

sealed interface GenvagStamplaUtBeslut {
    data class SparaSlut(val slut: LocalTime) : GenvagStamplaUtBeslut

    data object IngenInstempling : GenvagStamplaUtBeslut
}

fun beslutGenvagStamplaIn(dag: Dag?, nu: LocalTime): GenvagStamplaInBeslut {
    val start = dag?.start
    if (start != null) {
        return GenvagStamplaInBeslut.RedanInstamplad(start)
    }
    return GenvagStamplaInBeslut.SparaStart(avrundaTillHelMinut(nu))
}

fun beslutGenvagStamplaUt(dag: Dag?, nu: LocalTime): GenvagStamplaUtBeslut {
    if (dag?.start == null) {
        return GenvagStamplaUtBeslut.IngenInstempling
    }
    return GenvagStamplaUtBeslut.SparaSlut(avrundaTillHelMinut(nu))
}
