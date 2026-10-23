package se.tmconnect.styrelserummet.underlag.exempel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.pdf.PdfLasare
import se.tmconnect.styrelserummet.underlag.byggUtkast

class ExempelFlodeTest {

    @Test
    fun exempelHandingarGerAvstamdUtkastMedVarningForBalkong() {
        val planBytes = skrivPdf(underhallsplanSidor())
        val arBytes = skrivPdf(arsredovisningSidor())

        val planText = PdfLasare().lasSomText(planBytes).getOrThrow()
        val arText = PdfLasare().lasSomText(arBytes).getOrThrow()

        val utkast = byggUtkast(
            planText,
            arText,
            exempelForening(),
            inspeladTolkare(),
            nuAr = RAKENSKAPSAR,
        )

        assertTrue(utkast.avstamning!!.stammer)
        assertEquals(BYGGNADENS_VARDE, utkast.byggnadensVarde)
        assertTrue(utkast.komponenter.size >= 13)

        val balkong = utkast.komponenter.single { it.namn == "Balkonger" }
        assertEquals(Sakerhet.UPPSKATTNING, balkong.sakerhet)

        assertTrue(
            utkast.anmarkningar.any {
                (it.contains("kostnad", ignoreCase = true) && it.contains("balkong", ignoreCase = true)) ||
                    (
                        it.contains("uppskattat anskaffningsvärde", ignoreCase = true) &&
                            it.contains("Balkonger")
                        )
            },
        )
    }
}
