package se.tmconnect.styrelserummet.underlag

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class ArsredovisningTest {

    @Test
    fun testByggnadsar() {
        // Test case 1: "Byggnadsår 1968" ger byggnadsar == 1968
        val text1 = "Some text Byggnadsår 1968 more text"
        val result1 = lasArsredovisning(text1)
        assertEquals(1968, result1.byggnadsar)

        // Test case 2: "BYGGNADSÅR: 1972" ger 1972 (stora bokstäver)
        val text2 = "Some text BYGGNADSÅR: 1972 more text"
        val result2 = lasArsredovisning(text2)
        assertEquals(1972, result2.byggnadsar)

        // Test case 3: "Byggnadsår enligt taxeringen 1985" ger 1985
        val text3 = "Some text Byggnadsår enligt taxeringen 1985 more text"
        val result3 = lasArsredovisning(text3)
        assertEquals(1985, result3.byggnadsar)

        // Test case 4: "Byggnadsår, se not 12 om fastighetens taxering och värde, 1990" ger byggnadsar == null
        val text4 = "Some text Byggnadsår, se not 12 om fastighetens taxering och värde, 1990 more text"
        val result4 = lasArsredovisning(text4)
        assertNull(result4.byggnadsar)

        // Test case 5: "Ingenting här" ger byggnadsar == null
        val text5 = "Ingenting här"
        val result5 = lasArsredovisning(text5)
        assertNull(result5.byggnadsar)
    }

    @Test
    fun testAvskrivningsprincip() {
        // Test case 1: "Avskrivningsprincip\nLinjär avskrivning över 100 år\nÖvrigt" ger avskrivningsprincip == "Linjär avskrivning över 100 år"
        val text1 = "Avskrivningsprincip\nLinjär avskrivning över 100 år\nÖvrigt"
        val result1 = lasArsredovisning(text1)
        assertEquals("Linjär avskrivning över 100 år", result1.avskrivningsprincip)

        // Test case 2: "Avskrivningsprincip för byggnader\n  Komponentavskrivning  \n" ger "Komponentavskrivning" (trimmad)
        val text2 = "Avskrivningsprincip för byggnader\n  Komponentavskrivning  \n"
        val result2 = lasArsredovisning(text2)
        assertEquals("Komponentavskrivning", result2.avskrivningsprincip)

        // Test case 3: "Ingenting här" ger avskrivningsprincip == null
        val text3 = "Ingenting här"
        val result3 = lasArsredovisning(text3)
        assertNull(result3.avskrivningsprincip)
    }
}
