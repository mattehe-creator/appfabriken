package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class SammanfattningTest {

    @Test
    fun antalOchSumma() {
        assertEquals(
            listOf(
                "Listan innehåller 0 komponenter.",
                "Anskaffningsvärdet är 0 kr sammanlagt.",
            ),
            sammanfatta(emptyList(), null, 2025),
        )
        assertEquals(
            listOf(
                "Listan innehåller 1 komponenter.",
                "Anskaffningsvärdet är 1\u00A0000\u00A0000 kr sammanlagt.",
            ),
            sammanfatta(listOf(komponent(1_000_000)), null, 2025),
        )
    }

    private fun komponent(varde: Long) =
        Komponent(
            namn = "Test",
            grupp = Komponentgrupp.STOMME,
            anskaffningsvarde = varde,
            anskaffningsar = 2020,
            nyttjandeperiodAr = 40,
            restvarde = 0,
            kalla = "test",
            sakerhet = Sakerhet.HOG,
        )
}
