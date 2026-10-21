package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.bokfortVarde
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
        val tidigt = anmarkningarFor(utkastMedAnskaffningsar(1970), emptyList(), arsslut = 2025)
        assertTrue(
            tidigt.any {
                it.contains("Stomme") &&
                    it.contains("1970") &&
                    it.contains("1975") &&
                    it.contains("före")
            },
        )

        val sammaAr = anmarkningarFor(utkastMedAnskaffningsar(1975), emptyList(), arsslut = 2025)
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

    private fun enkelKomponent(
        namn: String,
        anskaffningsar: Int,
        nyttjandeperiodAr: Int,
        anskaffningsvarde: Long = 100_000L,
        restvarde: Long = 0L,
        grupp: Komponentgrupp = Komponentgrupp.TAK,
    ) = Komponent(
        namn = namn,
        grupp = grupp,
        anskaffningsvarde = anskaffningsvarde,
        anskaffningsar = anskaffningsar,
        nyttjandeperiodAr = nyttjandeperiodAr,
        restvarde = restvarde,
        kalla = "test.pdf, sida 1",
        sakerhet = Sakerhet.HOG,
    )

    private fun utkastMed(komponenter: List<Komponent>): Utkast =
        Utkast(
            forening = Forening(namn = "Test", byggnadsar = 1975),
            komponenter = komponenter,
            byggnadensVarde = null,
            arsredovisning = ArsredovisningUtdrag(null, null, null, null),
            avstamning = null,
            anmarkningar = emptyList(),
        )

    @Test
    fun anmarkningDubblaNamnOchNyttjande() {
        val dubblaTak =
            utkastMed(
                listOf(
                    enkelKomponent("Tak", anskaffningsar = 2010, nyttjandeperiodAr = 30),
                    enkelKomponent("Tak", anskaffningsar = 2015, nyttjandeperiodAr = 25, grupp = Komponentgrupp.FASAD),
                ),
            )
        val dubblaAnmarkningar = anmarkningarFor(dubblaTak, emptyList(), arsslut = 2025)
        assertTrue(
            dubblaAnmarkningar.any { it.contains("Tak") && it.contains("samma namn", ignoreCase = true) },
        )

        val utgangen =
            enkelKomponent(
                namn = "Fönster",
                anskaffningsar = 2005,
                nyttjandeperiodAr = 15,
                anskaffningsvarde = 100_000L,
                restvarde = 0L,
                grupp = Komponentgrupp.FONSTER_DORRAR,
            )
        assertTrue(utgangen.anskaffningsar + utgangen.nyttjandeperiodAr <= 2025)
        val bokfortVidArsslut = bokfortVarde(utgangen, 2025)
        if (bokfortVidArsslut > utgangen.restvarde) {
            val nyttjandeAnmarkningar = anmarkningarFor(utkastMed(listOf(utgangen)), emptyList(), arsslut = 2025)
            assertTrue(
                nyttjandeAnmarkningar.any {
                    it.contains("Fönster") &&
                        it.contains("nyttjandeperiod") &&
                        (it.contains("gått ut") || it.contains("bokfört"))
                },
            )
        } else {
            val nyttjandeAnmarkningar = anmarkningarFor(utkastMed(listOf(utgangen)), emptyList(), arsslut = 2025)
            assertFalse(
                nyttjandeAnmarkningar.any {
                    it.contains("Fönster") && it.contains("nyttjandeperioden gått ut")
                },
            )
        }

        val takOchTak =
            utkastMed(
                listOf(
                    enkelKomponent("Tak", anskaffningsar = 2010, nyttjandeperiodAr = 30),
                    enkelKomponent("tak", anskaffningsar = 2011, nyttjandeperiodAr = 29),
                ),
            )
        val caseAnmarkningar = anmarkningarFor(takOchTak, emptyList(), arsslut = 2025)
        assertFalse(caseAnmarkningar.any { it.contains("samma namn") })
    }
}
