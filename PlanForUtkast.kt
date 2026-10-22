package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.regler.PlanAr
import se.tmconnect.styrelserummet.regler.avskrivningsplan
import se.tmconnect.styrelserummet.modell.Komponent

const val PLAN_ANTAL_AR = 10

fun planForUtkast(utkast: UtkastDto, antalAr: Int = PLAN_ANTAL_AR): List<PlanAr> {
    val komponenter = utkast.komponenter.map { franDto(it) }
    return avskrivningsplan(komponenter, utkast.arsslut + 1, antalAr)
}
