package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class KvittoVarorTest {
    @Test
    fun raderMedBeloppBlirForslagUtanAntalOchBelopp() {
        val text = """
            BILTEMA
            SKRIVPLATTA A4  79,80
            3 Banh Mi Nem Nuong 99,00
            2 * 39,90
            Kjell kabel 1 299,00 kr
        """.trimIndent()
        assertEquals(listOf("Skrivplatta a4", "Banh Mi Nem Nuong", "Kjell kabel"), hittaVarorader(text))
    }

    @Test
    fun summaMomsOchBetalningarHoppasOver() {
        val text = """
            Kabel 99,00
            Totalt 99,00
            Moms 25 % 19,80
            Kort 99,00
            Mastercard 99,00
            Växel 0,00
        """.trimIndent()
        assertEquals(listOf("Kabel"), hittaVarorader(text))
    }

    @Test
    fun antalEfterNamnetTasBort() {
        assertEquals(listOf("Hörlurar"), hittaVarorader("Hörlurar 2 st 598,00"))
    }

    @Test
    fun dubblettMaxAntalOchTomText() {
        assertEquals(listOf("Kabel"), hittaVarorader("Kabel 99,00\nKabel 99,00"))
        assertEquals(emptyList<String>(), hittaVarorader(""))
        assertEquals(emptyList<String>(), hittaVarorader("Ingen pris här"))
        val manga = (1..10).joinToString("\n") { "Vara$it abc 10,00" }
        assertEquals(6, hittaVarorader(manga).size)
    }

    @Test
    fun forLangaNamnHoppasOver() {
        assertEquals(emptyList<String>(), hittaVarorader("${"x".repeat(50)} 10,00"))
    }
}
