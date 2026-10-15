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

    // Texten är återskapad efter tre riktiga kvitton som fotats på snedden (BESLUTSLOGG 2026-10-12),
    // där ML Kit ger etiketter och belopp på skilda rader och i annan ordning än på papperet.

    @Test
    fun biltemaEtikettOchBeloppPaSkildaRader() {
        val text = """
            BIL TEMA SWEDEN AB/LANDSKRONA 160
            Totalt (SEK):
            Tillbaka:
            2026-09-01 10:44
            Datum: 01.09.2026
            2 * 39,90
            79,80
            79,80
            0,00
            Öppet köp i 30 dagar mot uppvisande av kvitto
        """.trimIndent()
        val f = tolkaKvitto(text, idag)
        assertEquals("BIL TEMA SWEDEN AB", f.butik)
        assertEquals(LocalDate.of(2026, 9, 1), f.datum)
        assertEquals(7_980L, f.prisOre)
    }

    @Test
    fun fakturaDarTotaltAntalTjansterInteArPriset() {
        val text = """
            Faktura 127258
            Borstahusen Campingresort
            2026-09-03 10:13
            Eftersäsong, 2026-08-29 - 2026-08-31
            Totalt 12 tjänster
            Totalt 2420,00 SEK
            Varav moms 252,54 SEK
            Mottaget Surfboard Payments 2420,00 SEK
        """.trimIndent()
        val f = tolkaKvitto(text, idag)
        assertEquals("Borstahusen Campingresort", f.butik)
        assertEquals(LocalDate.of(2026, 9, 3), f.datum)
        assertEquals(242_000L, f.prisOre)
    }

    @Test
    fun beloppSomUpprepasGesSomPrisNarNyckelordSaknasPaRaden() {
        val text = """
            Banh mi Viet Viet
            01/09/2026 12:16:06
            TOTAL
            TERMINAL
            218,00
            Moms 12 % 23,36
            Brutto 218,00
            BELOP: 218,00 kr
        """.trimIndent()
        val f = tolkaKvitto(text, idag)
        assertEquals("Banh mi Viet Viet", f.butik)
        assertEquals(LocalDate.of(2026, 9, 1), f.datum)
        assertEquals(21_800L, f.prisOre)
    }

    @Test
    fun upprepatBeloppUtanNyckelord() {
        assertEquals(7_980L, hittaPris(listOf("Kabel", "79,80", "Kort 79,80", "Moms 15,96")))
    }

    @Test
    fun heltalUtanValutaArInteEttPris() {
        assertNull(hittaPris(listOf("Totalt 12 tjänster")))
        assertEquals(129_900L, hittaPris(listOf("Totalt 1299 kr")))
    }

    @Test
    fun periodensSlutEllerStartArInteKopdatum() {
        assertNull(hittaDatum("2026-08-29 - 2026-08-31", idag))
    }

    @Test
    fun datumMedKlockslagVinnerOverDatumUtanOrdningsMarkering() {
        assertEquals(LocalDate.of(2026, 9, 3), hittaDatum("2026-08-01\n2026-09-03 10:13", idag))
    }

    @Test
    fun butikUtanAvdelningOchGatunummer() {
        assertEquals("Biltema AB", hittaButik(listOf("Biltema AB/Landskrona 160")))
        assertNull(hittaButik(listOf("Faktura 127258", "Fak tura 127258")))
    }

    @Test
    fun kontaktFyllsBaraDarDetSaknas() {
        val f = KvittoForslag(null, null, null, kontakt = ButikKontakt("a@b.se", "08-123 45", "556000-0000"))
        val tom = fyllTommaFalt(f, "", "", idag, idag)
        assertEquals(ButikKontakt("a@b.se", "08-123 45", "556000-0000"), tom.kontakt)
        assertTrue(tom.kontaktFranKvitto)
        val delvis = fyllTommaFalt(f, "", "", idag, idag, kontakt = ButikKontakt(epost = "min@epost.se"))
        assertEquals(ButikKontakt("min@epost.se", "08-123 45", "556000-0000"), delvis.kontakt)
        val full = fyllTommaFalt(f, "", "", idag, idag, kontakt = ButikKontakt("x@y.se", "1", "2"))
        assertEquals(ButikKontakt("x@y.se", "1", "2"), full.kontakt)
        assertTrue(!full.kontaktFranKvitto)
    }

    @Test
    fun garantiManaderFranTreArsGarantiPaKvitto() {
        val f = tolkaKvitto(
            "Elgiganten\n2026-08-01\nSumma att betala 1 299,00\n3 års garanti",
            LocalDate.of(2026, 10, 14),
        )
        assertEquals(36, f.garantiManader)
    }

    @Test
    fun garantiManaderSaknasNarIngenGarantiITexten() {
        val f = tolkaKvitto(
            "Kjell & Company\n01.08.2026\nTotal 349.90 SEK",
            LocalDate.of(2026, 10, 14),
        )
        assertNull(f.garantiManader)
    }

    @Test
    fun garantiManaderGorAttForslagInteArTomt() {
        assertTrue(
            !tolkaKvitto("Garanti 24 mån", LocalDate.of(2026, 10, 14)).arTomt,
        )
    }

    @Test
    fun tomtKvittoArTomtAvenMedGarantifalt() {
        assertTrue(tolkaKvitto("", LocalDate.of(2026, 10, 14)).arTomt)
    }
}
