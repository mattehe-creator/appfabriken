package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.lasNyttjandeperioderFranResurs

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
        val tidigt = anmarkningarFor(utkastMedAnskaffningsar(1970), emptyList(), 2025)
        assertTrue(
            tidigt.any {
                it.contains("Stomme") &&
                    it.contains("1970") &&
                    it.contains("1975") &&
                    it.contains("före")
            },
        )

        val sammaAr = anmarkningarFor(utkastMedAnskaffningsar(1975), emptyList(), 2025)
        assertFalse(
            sammaAr.any {
                it.contains("Stomme") &&
                    it.contains("före") &&
                    it.contains("byggnadsår")
            },
        )
    }
    
    @Test
    fun anmarkningAnskaffningsarEfterArsslut() {
        val tabell = lasNyttjandeperioderFranResurs()
        
        // Komponent med anskaffningsår efter arsslut ska ge anmärkning
        val utkastEfter = utkastMedAnskaffningsar(2026)
        val anmarkningarEfter = anmarkningarFor(utkastEfter, tabell, 2025)
        assertTrue(
            "Ska innehålla anmärkning för anskaffningsår efter arsslut",
            anmarkningarEfter.any {
                it.contains("Stomme") && 
                it.contains("2026") && 
                it.contains("2025")
            }
        )
        
        // Komponent med samma anskaffningsår som arsslut ska inte ge anmärkning
        val utkastSamma = utkastMedAnskaffningsar(2025)
        val anmarkningarSamma = anmarkningarFor(utkastSamma, tabell, 2025)
        assertFalse(
            "Ska inte innehålla anmärkning för samma anskaffningsår som arsslut",
            anmarkningarSamma.any {
                it.contains("Stomme") && 
                it.contains("2025") && 
                it.contains("2025")
            }
        )
        
        // Komponent med anskaffningsår före arsslut ska inte ge anmärkning
        val utkastFöre = utkastMedAnskaffningsar(2024)
        val anmarkningarFöre = anmarkningarFor(utkastFöre, tabell, 2025)
        assertFalse(
            "Ska inte innehålla anmärkning för anskaffningsår före arsslut",
            anmarkningarFöre.any {
                it.contains("Stomme") && 
                it.contains("2024") && 
                it.contains("2025")
            }
        )
    }
}
