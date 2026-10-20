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
    private fun komponent(namn: String, anskaffningsar: Int) = Komponent(
        namn = namn,
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = 1_000_000L,
        anskaffningsar = anskaffningsar,
        nyttjandeperiodAr = 100,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )

    private fun utkastMed(vararg komponenter: Komponent) = Utkast(
        forening = Forening("Testföreningen", 1975),
        komponenter = komponenter.toList(),
        byggnadensVarde = null,
        arsredovisning = ArsredovisningUtdrag(null, null, 1975, null),
        avstamning = null,
        anmarkningar = emptyList(),
    )

    @Test
    fun anmarkningAnskaffningsarEfterArsslut() {
        val tabell = lasNyttjandeperioderFranResurs()
        val arsslut = 2025

        val efter = anmarkningarFor(
            utkastMed(komponent("Ny hiss", 2026)),
            tabell,
            arsslut,
        )
        assertTrue(
            efter.any {
                it.contains("Ny hiss") &&
                    it.contains("2026") &&
                    it.contains("räkenskapsårets slut") &&
                    it.contains("2025")
            },
        )

        val sammaAr = anmarkningarFor(
            utkastMed(komponent("Ombyggd fasad", 2025)),
            tabell,
            arsslut,
        )
        assertFalse(
            sammaAr.any {
                it.contains("Ombyggd fasad") && it.contains("räkenskapsårets slut")
            },
        )
    }
}
