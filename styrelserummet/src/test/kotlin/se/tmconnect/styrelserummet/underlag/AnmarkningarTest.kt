package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class AnmarkningarTest {
    private fun utkastMedAnskaffningsar(anskaffningsar: Int): Utkast =
        Utkast(
            forening = Forening(namn = "Test", byggnadsar = 1975),
            komponenter = listOf(
                Komponent(
                    namn = "Stomme",
                    grupp = Komponentgrupp.STOMME,
                    anskaffningsvarde = 1_000_000L,
                    anskaffningsar = anskaffningsar,
                    nyttjandeperiodAr = 50,
                    kalla = "test.pdf, sida 1",
                    sakerhet = Sakerhet.HOG,
                ),
            ),
            byggnadensVarde = null,
            arsredovisning = ArsredovisningUtdrag(null, null, null, null),
            avstamning = null,
            anmarkningar = emptyList(),
        )

    @Test
    fun anmarkningAnskaffningsarForeByggnadsar() {
        val tidigt = anmarkningarFor(utkastMedAnskaffningsar(1970), emptyList())
        assertTrue(
            tidigt.any {
                it.contains("Stomme") &&
                    it.contains("1970") &&
                    it.contains("1975") &&
                    it.contains("före")
            },
        )

        val sammaAr = anmarkningarFor(utkastMedAnskaffningsar(1975), emptyList())
        assertFalse(
            sammaAr.any {
                it.contains("Stomme") &&
                    it.contains("före") &&
                    it.contains("byggnadsår")
            },
        )
    }
}
