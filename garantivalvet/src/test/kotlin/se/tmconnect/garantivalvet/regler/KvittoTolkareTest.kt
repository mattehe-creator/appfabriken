package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KvittoTolkareTest {
    private val idag = LocalDate.of(2026, 10, 12)

    @Test
    fun svensktKvittoMedSummaAttBetala() {
        val f = tolkaKvitto("ELGIGANTEN\nOrg.nr 556000-0000\n2026-08-01 14:32\nSumma att betala 1 299,00", idag)
        assertEquals("ELGIGANTEN", f.butik)
        assertEquals(LocalDate.of(2026, 8, 1), f.datum)
        assertEquals(129_900L, f.prisOre)
    }

    @Test
    fun punktDatumOchTotalMedSek() {
        val f = tolkaKvitto("Kjell & Company\n01.08.2026\nTotal 349.90 SEK", idag)
        assertEquals("Kjell & Company", f.butik)
        assertEquals(LocalDate.of(2026, 8, 1), f.datum)
        assertEquals(34_990L, f.prisOre)
    }

    @Test
    fun framtidaDatumOchSaknatPrisGerInget() {
        val f = tolkaKvitto("Kvitto\nMoms 25 % 52,00\nDatum 3/9/27", idag)
        assertNull(f.butik)
        assertNull(f.datum)
        assertNull(f.prisOre)
        assertTrue(f.arTomt)
    }

    @Test
    fun tvasiffrigtArOchSnedstreck() {
        assertEquals(LocalDate.of(2026, 8, 1), hittaDatum("Köpt 1/8/26", idag))
    }

    @Test
    fun forGammaltDatumIgnoreras() {
        assertNull(hittaDatum("2010-01-01", idag))
    }

    @Test
    fun ogiltigtDatumHoppasOver() {
        assertEquals(LocalDate.of(2026, 8, 1), hittaDatum("31.02.2026 2026-08-01", idag))
    }

    @Test
    fun attBetalaVinnerOverDelsumma() {
        val rader = listOf("Summa 1 000,00", "Rabatt 100,00", "Att betala 900,00")
        assertEquals(90_000L, hittaPris(rader))
    }

    @Test
    fun momsOchSubtotalRaderRaknasInte() {
        assertNull(hittaPris(listOf("Moms 25 % total 52,00", "Subtotal 208,00")))
    }

    @Test
    fun beloppPaNastaRad() {
        assertEquals(49_900L, hittaPris(listOf("Totalt", "499,00")))
    }

    @Test
    fun ingetNyckelordGerIngetPris() {
        assertNull(hittaPris(listOf("Hörlurar 499,00", "Kabel 99,00")))
    }

    @Test
    fun beloppsformat() {
        assertEquals(129_900L, tolkaBeloppTillOre("1 299,00"))
        assertEquals(129_900L, tolkaBeloppTillOre("1.299,00"))
        assertEquals(34_990L, tolkaBeloppTillOre("349.90"))
        assertEquals(129_900L, tolkaBeloppTillOre("1299"))
        assertEquals(129_900L, tolkaBeloppTillOre("1,299"))
        assertEquals(34_950L, tolkaBeloppTillOre("349,5"))
        assertNull(tolkaBeloppTillOre("0,00"))
    }

    @Test
    fun prisfaltText() {
        assertEquals("1299", prisOreTillFaltText(129_900L))
        assertEquals("349,90", prisOreTillFaltText(34_990L))
        assertEquals("0,05", prisOreTillFaltText(5L))
    }

    @Test
    fun butikHoppaOverKvittoNyckelordOchDatum() {
        assertEquals("Biltema", hittaButik(listOf("KVITTO", "2026-08-01", "Tel 08-123 45", "Biltema")))
    }

    @Test
    fun tomTextGerTomtForslag() {
        assertTrue(tolkaKvitto("", idag).arTomt)
    }

    private val forslag = KvittoForslag("Biltema", LocalDate.of(2026, 8, 1), 34_990L)

    @Test
    fun tommaFaltFyllsFranKvittot() {
        val ny = fyllTommaFalt(forslag, "", "", idag, idag)
        assertEquals("Biltema", ny.varKopt)
        assertEquals("349,90", ny.prisText)
        assertEquals(LocalDate.of(2026, 8, 1), ny.kopdatum)
        assertTrue(ny.butikFranKvitto && ny.prisFranKvitto && ny.datumFranKvitto)
    }

    @Test
    fun ifylldaFaltSkrivsAldrigOver() {
        val valt = LocalDate.of(2026, 9, 1)
        val ny = fyllTommaFalt(forslag, "Clas Ohlson", "500", valt, idag)
        assertEquals("Clas Ohlson", ny.varKopt)
        assertEquals("500", ny.prisText)
        assertEquals(valt, ny.kopdatum)
        assertTrue(!ny.butikFranKvitto && !ny.prisFranKvitto && !ny.datumFranKvitto)
    }

    @Test
    fun tomtForslagAndrarInget() {
        val ny = fyllTommaFalt(KvittoForslag(null, null, null), "", "", idag, idag)
        assertEquals("", ny.varKopt)
        assertEquals("", ny.prisText)
        assertEquals(idag, ny.kopdatum)
        assertTrue(!ny.butikFranKvitto && !ny.prisFranKvitto && !ny.datumFranKvitto)
    }
}
