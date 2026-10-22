package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet

class UtbyteTest {

    @Test
    fun utrangering300kUtbyte2026() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(120_000L, utrangeringsbelopp(k, 2026))
    }

    @Test
    fun utrangering1_2MUtbyte2026() {
        val k = komponent(1_200_000, 2010, 40, 0)
        assertEquals(720_000L, utrangeringsbelopp(k, 2026))
    }

    @Test
    fun utrangeringEfterFullAvskrivning() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(0L, utrangeringsbelopp(k, 2035))
    }

    @Test
    fun utrangeringMedRestvarde() {
        val k = komponent(500_000, 2018, 10, 50_000)
        assertEquals(50_000L, utrangeringsbelopp(k, 2030))
    }

    @Test
    fun utrangeringSammaArSomAnskaffning() {
        val k = komponent(300_000, 2020, 10, 0)
        assertEquals(300_000L, utrangeringsbelopp(k, 2020))
    }

    @Test
    fun ersattKomponentStandardNyttjandeperiod() {
        val gammal = takKomponent()
        val resultat = ersattKomponent(gammal, 2026, 450_000, kalla = "Underhållsplan 2026")
        assertEquals(450_000L, resultat.ny.anskaffningsvarde)
        assertEquals(2026, resultat.ny.anskaffningsar)
        assertEquals(10, resultat.ny.nyttjandeperiodAr)
        assertEquals(0L, resultat.ny.restvarde)
        assertEquals(Sakerhet.MEDEL, resultat.ny.sakerhet)
        assertEquals("Tak", resultat.ny.namn)
        assertEquals("Underhållsplan 2026", resultat.ny.kalla)
        assertEquals(120_000L, resultat.utrangering)
        assertEquals(gammal, resultat.gammal)
    }

    @Test
    fun ersattKomponentAnpassadNyttjandeperiod() {
        val gammal = takKomponent()
        val resultat = ersattKomponent(
            gammal,
            2026,
            450_000,
            nyNyttjandeperiodAr = 30,
            kalla = "Underhållsplan 2026",
        )
        assertEquals(30, resultat.ny.nyttjandeperiodAr)
    }

    @Test(expected = IllegalArgumentException::class)
    fun ersattKomponentNegativtAnskaffningsvarde() {
        ersattKomponent(takKomponent(), 2026, -1, kalla = "Underhållsplan 2026")
    }

    @Test(expected = IllegalArgumentException::class)
    fun ersattKomponentNollNyttjandeperiod() {
        ersattKomponent(takKomponent(), 2026, 450_000, nyNyttjandeperiodAr = 0, kalla = "Underhållsplan 2026")
    }

    private fun komponent(varde: Long, anskaffningsar: Int, period: Int, restvarde: Long) = Komponent(
        namn = "Test",
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = varde,
        anskaffningsar = anskaffningsar,
        nyttjandeperiodAr = period,
        restvarde = restvarde,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )

    private fun takKomponent() = Komponent(
        namn = "Tak",
        grupp = Komponentgrupp.STOMME,
        anskaffningsvarde = 300_000,
        anskaffningsar = 2020,
        nyttjandeperiodAr = 10,
        restvarde = 0,
        kalla = "test",
        sakerhet = Sakerhet.HOG,
    )
}
