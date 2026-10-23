package se.tmconnect.flextid.regler

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.FlextidRepository

class NotisAtgardReglerTest {
    @Test
    fun stamplaInSparatTarBortNotis() {
        assertEquals(
            NotisAtgardUtfall.TaBort,
            notisAtgardEfterStamplaIn(FlextidRepository.GenvagStamplaInResult.Sparat),
        )
    }

    @Test
    fun stamplaInRedanInstampladVisarTextMedTid() {
        val utfall =
            notisAtgardEfterStamplaIn(
                FlextidRepository.GenvagStamplaInResult.RedanInstamplad(LocalTime.of(8, 15)),
            )
        assertTrue(utfall is NotisAtgardUtfall.VisaText)
        val visa = utfall as NotisAtgardUtfall.VisaText
        assertEquals(NotisAtgardTextNyckel.GENVAG_REDAN_INSTAMPLAD, visa.nyckel)
        assertEquals(LocalTime.of(8, 15), visa.tidForFormat)
    }

    @Test
    fun stamplaInGransVisarProMeddelande() {
        val utfall = notisAtgardEfterStamplaIn(FlextidRepository.GenvagStamplaInResult.GransUtanPro)
        assertEquals(
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.PRO_GRANS_NADD),
            utfall,
        )
    }

    @Test
    fun stamplaUtSparatTarBortNotis() {
        assertEquals(
            NotisAtgardUtfall.TaBort,
            notisAtgardEfterStamplaUt(FlextidRepository.GenvagStamplaUtResult.Sparat),
        )
    }

    @Test
    fun stamplaUtIngenInstemplingVisarText() {
        assertEquals(
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.GENVAG_INGEN_INSTEMPLING),
            notisAtgardEfterStamplaUt(FlextidRepository.GenvagStamplaUtResult.IngenInstempling),
        )
    }

    @Test
    fun stamplaUtSlutForeStartVisarText() {
        assertEquals(
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.FEL_SLUT_FORE_START),
            notisAtgardEfterStamplaUt(FlextidRepository.GenvagStamplaUtResult.SlutForeStart),
        )
    }

    @Test
    fun stamplaUtGransVisarProMeddelande() {
        assertEquals(
            NotisAtgardUtfall.VisaText(NotisAtgardTextNyckel.PRO_GRANS_NADD),
            notisAtgardEfterStamplaUt(FlextidRepository.GenvagStamplaUtResult.GransUtanPro),
        )
    }
}
