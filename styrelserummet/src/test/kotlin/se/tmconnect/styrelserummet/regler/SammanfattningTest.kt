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

    @Test
    fun osakerOchAvstamning() {
        // Testa med komponenter där några har låg säkerhet
        val komponenter = listOf(
            komponent(1_000_000),  // HOG
            komponent(2_000_000),  // LAG
            komponent(3_000_000),  // UPPSKATTNING
            komponent(4_000_000)   // HOG
        )
        
        assertEquals(
            listOf(
                "Listan innehåller 4 komponenter.",
                "Anskaffningsvärdet är 10\u00A0000\u00A0000 kr sammanlagt.",
                "2 komponenter (50,0 %) har uppskattat värde eller låg säkerhet.",
                "Skillnaden mot byggnadens värde är 1\u00A0000\u00A0000 kr."
            ),
            sammanfatta(komponenter, 11_000_000L, 2025)
        )
        
        // Testa med byggnadens värde saknas
        assertEquals(
            listOf(
                "Listan innehåller 2 komponenter.",
                "Anskaffningsvärdet är 3\u00A0000\u00A0000 kr sammanlagt.",
                "1 komponenter (50,0 %) har uppskattat värde eller låg säkerhet.",
                "Byggnadens värde saknas, så avstämning görs inte."
            ),
            sammanfatta(listOf(komponent(1_000_000), komponent(2_000_000)), null, 2025)
        )
        
        // Testa med stämmande värden
        assertEquals(
            listOf(
                "Listan innehåller 2 komponenter.",
                "Anskaffningsvärdet är 3\u00A0000\u00A0000 kr sammanlagt.",
                "0 komponenter (0,0 %) har uppskattat värde eller låg säkerhet.",
                "Komponenternas anskaffningsvärde stämmer med byggnadens värde."
            ),
            sammanfatta(listOf(komponent(1_000_000), komponent(2_000_000)), 3_000_000L, 2025)
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
