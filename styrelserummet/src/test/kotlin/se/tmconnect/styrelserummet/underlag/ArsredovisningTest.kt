package se.tmconnect.styrelserummet.underlag

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class ArsredovisningTest {

    @Test
    fun testByggnadsar() {
        val text1 = "Byggnadsår 1968"
        val result1 = lasArsredovisning(text1)
        assertEquals(1968, result1.byggnadsar)

        val text2 = "BYGGNADSÅR: 1972"
        val result2 = lasArsredovisning(text2)
        assertEquals(1972, result2.byggnadsar)

        val text3 = "Byggnadsår enligt taxeringen 1985"
        val result3 = lasArsredovisning(text3)
        assertEquals(1985, result3.byggnadsar)

        val text4 = "Byggnadsår, se not 12 om fastighetens taxering och värde, 1990"
        val result4 = lasArsredovisning(text4)
        assertNull(result4.byggnadsar)

        val text5 = "Ingenting här"
        val result5 = lasArsredovisning(text5)
        assertNull(result5.byggnadsar)
    }

    @Test
    fun testAvskrivningsprincip() {
        val text1 = "Avskrivningsprincip\nLinjär avskrivning över 100 år\nÖvrigt"
        val result1 = lasArsredovisning(text1)
        assertEquals("Linjär avskrivning över 100 år", result1.avskrivningsprincip)

        val text2 = "Avskrivningsprincip för byggnader\n  Komponentavskrivning  \n"
        val result2 = lasArsredovisning(text2)
        assertEquals("Komponentavskrivning", result2.avskrivningsprincip)

        val text3 = "Ingenting här"
        val result3 = lasArsredovisning(text3)
        assertNull(result3.avskrivningsprincip)
    }
}
