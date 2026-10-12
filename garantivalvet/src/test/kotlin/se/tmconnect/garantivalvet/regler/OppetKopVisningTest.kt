package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class OppetKopVisningTest {
    private fun kop(vad: String, oppetKopDagar: Int?, kopdatum: LocalDate = LocalDate.of(2026, 9, 1)) =
        Kop(vad = vad, kopdatum = kopdatum, garantiManader = 24, oppetKopDagar = oppetKopDagar)

    @Test
    fun narmasteFristArOppetKopNarDetGallerOchGerFornaSlut() {
        val k = kop("Kabel", 30)
        val f = narmasteFrist(k, LocalDate.of(2026, 9, 10))!!
        assertEquals(NarmasteFristTyp.OPPET_KOP, f.typ)
        assertEquals(LocalDate.of(2026, 10, 1), f.datum)
    }

    @Test
    fun narmasteFristArGarantiNarOppetKopGattUt() {
        val f = narmasteFrist(kop("Kabel", 30), LocalDate.of(2026, 10, 5))!!
        assertEquals(NarmasteFristTyp.GARANTI, f.typ)
        assertEquals(LocalDate.of(2028, 9, 1), f.datum)
    }

    @Test
    fun utanOppetKopGallerGarantinSomForut() {
        assertEquals(NarmasteFristTyp.GARANTI, narmasteFrist(kop("Kabel", null), LocalDate.of(2026, 9, 10))!!.typ)
        assertNull(aktivtOppetKop(kop("Kabel", null), LocalDate.of(2026, 9, 10)))
        assertNull(aktivtOppetKop(kop("Kabel", 0), LocalDate.of(2026, 9, 10)))
    }

    @Test
    fun oppetKopSomSlutarForstSorterasForst() {
        val idag = LocalDate.of(2026, 9, 10)
        val medOppetKop = kop("B", 14) // slut 2026-09-15
        val utanOppetKop = kop("A", null, LocalDate.of(2025, 1, 1)) // garanti till 2027-01-01
        val sorterad = sorteraEfterNarmasteFrist(listOf(utanOppetKop, medOppetKop), idag)
        assertEquals(listOf("B", "A"), sorterad.map { it.vad })
    }

    @Test
    fun paminnelseForOppetKopTreDagarFore() {
        val slut = LocalDate.of(2026, 10, 1)
        val frist = Frist(1, FristTyp.OPPET_KOP, slut)
        assertEquals(listOf(frist), fristerSomSkaAviseras(listOf(frist), LocalDate.of(2026, 9, 28), emptySet()))
        assertEquals(emptyList<Frist>(), fristerSomSkaAviseras(listOf(frist), LocalDate.of(2026, 9, 27), emptySet()))
    }

    @Test
    fun garantiFortfarandeTrettioDagarFore() {
        val frist = Frist(1, FristTyp.GARANTI, LocalDate.of(2026, 10, 1))
        assertEquals(listOf(frist), fristerSomSkaAviseras(listOf(frist), LocalDate.of(2026, 9, 1), emptySet()))
    }

    @Test
    fun nycklarForOppetKopOchGarantiSkiljs() {
        val slut = LocalDate.of(2026, 10, 1)
        val a = Frist(1, FristTyp.OPPET_KOP, slut)
        val b = Frist(1, FristTyp.GARANTI, slut)
        assertEquals(listOf(b), fristerSomSkaAviseras(listOf(a, b), LocalDate.of(2026, 9, 30), setOf(fristNyckel(a))))
    }

    @Test
    fun underlagVisarOppetKopGarantiOchReklamationSkilda() {
        val etiketter = UnderlagEtiketter(
            vad = "Vad", varKopt = "Var", kopdatum = "Köpdatum", pris = "Pris", garantiManader = "Garanti (mån)",
            garantiSlut = "Garanti till", reklamationSlut = "Reklamation till", presumtionSlut = "Presumtion till",
            reklamationStatus = "Status", statusFelAntasFunnits = "Fel", statusGaller = "Gäller",
            statusUtgangen = "Utgången", anteckning = "Anteckning", skapad = "Skapad",
            oppetKop = "Öppet köp dagar", oppetKopSlut = "Öppet köp till", varutyp = "Typ av vara",
            varutyper = mapOf("ELEKTRONIK" to "Elektronik"),
        )
        val k = kop("Kabel", 30).copy(varutyp = "ELEKTRONIK")
        val underlag = byggUnderlag(k, LocalDate.of(2026, 9, 10), etiketter, "Underlag")
        val rader = underlag.rader.associate { it.etikett to it.varde }
        assertEquals("30", rader["Öppet köp dagar"])
        assertEquals("2026-10-01", rader["Öppet köp till"])
        assertEquals("Elektronik", rader["Typ av vara"])
        assertEquals("2028-09-01", rader["Garanti till"])
        assertEquals("2029-09-01", rader["Reklamation till"])
    }

    @Test
    fun underlagUtanOppetKopHarInteDeRaderna() {
        val etiketter = UnderlagEtiketter(
            vad = "Vad", varKopt = "Var", kopdatum = "Köpdatum", pris = "Pris", garantiManader = "Garanti (mån)",
            garantiSlut = "Garanti till", reklamationSlut = "Reklamation till", presumtionSlut = "Presumtion till",
            reklamationStatus = "Status", statusFelAntasFunnits = "Fel", statusGaller = "Gäller",
            statusUtgangen = "Utgången", anteckning = "Anteckning", skapad = "Skapad",
            oppetKop = "Öppet köp dagar", oppetKopSlut = "Öppet köp till", varutyp = "Typ av vara",
        )
        val underlag = byggUnderlag(kop("Kabel", null), LocalDate.of(2026, 9, 10), etiketter, "Underlag")
        assertEquals(false, underlag.rader.any { it.etikett.startsWith("Öppet köp") || it.etikett == "Typ av vara" })
    }
}
