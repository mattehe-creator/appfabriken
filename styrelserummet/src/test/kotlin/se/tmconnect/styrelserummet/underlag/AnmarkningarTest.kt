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
    fun anmarkningAnskaffningsarEfterArsslut() {
        val tabell = lasNyttjandeperioderFranResurs()
        
        // Testa att en komponent med anskaffningsår 2026 ger anmärkning när arsslut är 2025
        val efter = anmarkningarFor(utkastMedAnskaffningsar(2026), tabell, arsslut = 2025)
        assertTrue(
            "Borde innehålla anmärkning för anskaffningsår efter räkenskapsåret",
            efter.any {
                it.contains("Stomme") &&
                    it.contains("2026") &&
                    it.contains("2025") &&
                    it.contains("efter räkenskapsårets slut")
            },
        )

        // Testa att en komponent med anskaffningsår 2025 inte ger anmärkning när arsslut är 2025
        val sammaAr = anmarkningarFor(utkastMedAnskaffningsar(2025), tabell, arsslut = 2025)
        assertFalse(
            "Borde INTE innehålla anmärkning för anskaffningsår lika med räkenskapsåret",
            sammaAr.any {
                it.contains("Stomme") &&
                    it.contains("efter räkenskapsårets slut")
            },
        )
    }
}
