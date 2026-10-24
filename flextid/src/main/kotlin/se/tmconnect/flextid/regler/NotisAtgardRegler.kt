package se.tmconnect.flextid.regler

import java.time.LocalTime
import se.tmconnect.flextid.data.FlextidRepository

enum class NotisAtgardTextNyckel {
    GENVAG_REDAN_INSTAMPLAD,
    GENVAG_INGEN_INSTEMPLING,
    FEL_SLUT_FORE_START,
    PRO_GRANS_NADD,
}

sealed interface NotisAtgardUtfall {
    data object TaBort : NotisAtgardUtfall

    data class VisaText(
        val nyckel: NotisAtgardTextNyckel,
        val tidForFormat: LocalTime? = null,
    ) : NotisAtgardUtfall
}

fun notisAtgardEfterStamplaIn(
    resultat: FlextidRepository.GenvagStamplaInResult,
): NotisAtgardUtfall =
    when (resultat) {
        FlextidRepository.GenvagStamplaInResult.Sparat -> NotisAtgardUtfall.TaBort
        is FlextidRepository.GenvagStamplaInResult.RedanInstamplad ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.GENVAG_REDAN_INSTAMPLAD, resultat.start)
        FlextidRepository.GenvagStamplaInResult.GransUtanPro ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.PRO_GRANS_NADD)
        FlextidRepository.GenvagStamplaInResult.SlutForeStart ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.FEL_SLUT_FORE_START)
    }

fun notisAtgardEfterStamplaUt(
    resultat: FlextidRepository.GenvagStamplaUtResult,
): NotisAtgardUtfall =
    when (resultat) {
        FlextidRepository.GenvagStamplaUtResult.Sparat -> NotisAtgardUtfall.TaBort
        FlextidRepository.GenvagStamplaUtResult.IngenInstempling ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.GENVAG_INGEN_INSTEMPLING)
        FlextidRepository.GenvagStamplaUtResult.SlutForeStart ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.FEL_SLUT_FORE_START)
        FlextidRepository.GenvagStamplaUtResult.GransUtanPro ->
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.PRO_GRANS_NADD)
    }
