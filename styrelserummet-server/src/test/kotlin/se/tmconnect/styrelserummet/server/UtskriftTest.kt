package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class UtskriftTest {
    @Test
    fun utskriftsRader() {
        val belysning = Komponent(
            namn = "Belysning",
            grupp = Komponentgrupp.EL,
            anskaffningsvarde = 500_000,
            anskaffningsar = 2015,
            nyttjandeperiodAr = 50,
            restvarde = 0,
            kalla = "test",
            sakerhet = Sakerhet.HOG,
        )
        val enKomponent = utskriftsRader(listOf(belysning), arsslut = 2025)
        assertEquals(2, enKomponent.size)
        assertEquals(
            UtskriftsRad(
                grupp = "El",
                namn = "Belysning",
                anskaffningsvarde = "500\u00A0000 kr",
                nyttjandeperiod = "50 år",
                avskrivningAret = "10\u00A0000 kr",
                bokfortVarde = "390\u00A0000 kr",
            ),
            enKomponent[0],
        )
        assertEquals(
            UtskriftsRad(
                grupp = "",
                namn = "Summa",
                anskaffningsvarde = "500\u00A0000 kr",
                nyttjandeperiod = "",
                avskrivningAret = "10\u00A0000 kr",
                bokfortVarde = "390\u00A0000 kr",
                arSumma = true,
            ),
            enKomponent[1],
        )

        val varme = Komponent(
            namn = "Panna",
            grupp = Komponentgrupp.VARME,
            anskaffningsvarde = 100_000,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 20,
            kalla = "test",
            sakerhet = Sakerhet.MEDEL,
        )
        val elOchVarme = utskriftsRader(listOf(varme, belysning), arsslut = 2025)
        assertEquals(listOf("El", "Värme"), elOchVarme.take(2).map { it.grupp })
        assertEquals(listOf("Belysning", "Panna"), elOchVarme.take(2).map { it.namn })
    }
}
