package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

class HemforsakringTest {
    private fun kop(vad: String, pris: Long?, datum: LocalDate = LocalDate.of(2024, 1, 1), butik: String? = null) =
        Kop(vad = vad, varKopt = butik, kopdatum = datum, garantiManader = 24, prisOre = pris)

    @Test
    fun dyrastForstOchSummanRaknasUtanSaknadePriser() {
        val s = byggHemforsakring(listOf(kop("Mugg", null), kop("TV", 799_900), kop("Dammsugare", 249_950)))
        assertEquals(listOf("TV", "Dammsugare", "Mugg"), s.rader.map { it.vad })
        assertEquals(1_049_850L, s.summaOre)
        assertEquals(1, s.antalUtanPris)
    }

    @Test
    fun prisSomDecimalAnvanderPunktOchTvaDecimaler() {
        assertEquals("1299.50", prisSomDecimal(129_950))
        assertEquals("0.05", prisSomDecimal(5))
        assertEquals("49.00", prisSomDecimal(4_900))
    }

    @Test
    fun csvHarRubrikRaderOchSumma() {
        val s = byggHemforsakring(listOf(kop("TV", 799_900, butik = "Elgiganten"), kop("Mugg", null)))
        val csv = hemforsakringCsv(s, listOf("Vara", "Butik", "Datum", "Pris"), "Summa")
        assertEquals(
            "Vara;Butik;Datum;Pris\r\nTV;Elgiganten;2024-01-01;7999.00\r\nMugg;;2024-01-01;\r\nSumma;;;7999.00\r\n",
            csv,
        )
    }

    @Test
    fun csvCitatOchFormelskydd() {
        val s = byggHemforsakring(listOf(kop("Kaffe; \"stor\"", 100), kop("=SUMMA(A1)", 50)))
        val csv = hemforsakringCsv(s, listOf("a", "b", "c", "d"), "S")
        assertTrue(csv.contains("\"Kaffe; \"\"stor\"\"\";;2024-01-01;1.00"))
        assertTrue(csv.contains("'=SUMMA(A1);;2024-01-01;0.50"))
    }
}
