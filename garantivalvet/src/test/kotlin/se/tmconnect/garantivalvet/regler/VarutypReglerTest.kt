package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.garantivalvet.data.Kop

class VarutypReglerTest {
    private val idag = LocalDate.of(2026, 10, 12)

    private fun kop(varutyp: String?, oppetKopDagar: Int? = null, kopdatum: LocalDate = LocalDate.of(2026, 10, 10)) =
        Kop(vad = "Vara", kopdatum = kopdatum, garantiManader = 24, oppetKopDagar = oppetKopDagar, varutyp = varutyp)

    @Test
    fun barMatOchDryckArFarskvara() {
        assertTrue(arFarskvara("MAT_DRYCK"))
        assertFalse(arFarskvara("ELEKTRONIK"))
        assertFalse(arFarskvara("KLADER_SKOR"))
        assertFalse(arFarskvara("HEM_BYGG"))
        assertFalse(arFarskvara("OVRIGT"))
        assertFalse(arFarskvara(null))
        assertFalse(arFarskvara("okänd"))
    }

    @Test
    fun farskvaraFarEgenStatusOvrigaRaknasSomForut() {
        assertEquals(KopStatus.FARSKVARA, statusForKop(kop("MAT_DRYCK"), idag))
        assertEquals(KopStatus.GARANTI_GALLER, statusForKop(kop("ELEKTRONIK"), idag))
        assertEquals(KopStatus.GARANTI_GALLER, statusForKop(kop(null), idag))
    }

    @Test
    fun farskvaraHarIngenGarantiEllerReklamationsfrist() {
        assertNull(narmasteFrist(kop("MAT_DRYCK"), idag))
        assertEquals(NarmasteFristTyp.GARANTI, narmasteFrist(kop("ELEKTRONIK"), idag)!!.typ)
    }

    @Test
    fun farskvaraMedOppetKopVisarBaraOppetKop() {
        val f = narmasteFrist(kop("MAT_DRYCK", oppetKopDagar = 14), idag)!!
        assertEquals(NarmasteFristTyp.OPPET_KOP, f.typ)
        assertEquals(LocalDate.of(2026, 10, 24), f.datum)
    }

    @Test
    fun farskvaraHamnarInteUnderGarantiGrupper() {
        assertEquals(ListaGrupp.UTGANGNA, listaGrupp(kop("MAT_DRYCK"), idag))
        assertEquals(ListaGrupp.GALLER, listaGrupp(kop("MAT_DRYCK", oppetKopDagar = 14), idag))
        assertEquals(ListaGrupp.GALLER, listaGrupp(kop("ELEKTRONIK"), idag))
    }

    @Test
    fun underlagForFarskvaraSaknarGarantiOchReklamationsrader() {
        val etiketter = UnderlagEtiketter(
            vad = "Vad", varKopt = "Var", kopdatum = "Köpdatum", pris = "Pris", garantiManader = "Garanti (mån)",
            garantiSlut = "Garanti till", reklamationSlut = "Reklamation till", presumtionSlut = "Presumtion till",
            reklamationStatus = "Status", statusFelAntasFunnits = "Fel", statusGaller = "Gäller",
            statusUtgangen = "Utgången", anteckning = "Anteckning", skapad = "Skapad",
            farskvara = "Typ av vara", farskvaraText = "Färskvara",
        )
        val rader = byggUnderlag(kop("MAT_DRYCK"), idag, etiketter, "Underlag").rader.associate { it.etikett to it.varde }
        assertEquals("Färskvara", rader["Typ av vara"])
        assertFalse(rader.containsKey("Garanti (mån)"))
        assertFalse(rader.containsKey("Garanti till"))
        assertFalse(rader.containsKey("Reklamation till"))
        val ovriga = byggUnderlag(kop("ELEKTRONIK"), idag, etiketter, "Underlag").rader.associate { it.etikett to it.varde }
        assertTrue(ovriga.containsKey("Garanti till"))
        assertTrue(ovriga.containsKey("Reklamation till"))
    }

    @Test
    fun farskvaraSorterasMedUtgangnaEfterKopdatum() {
        val aktiv = kop("ELEKTRONIK")
        val mat = kop("MAT_DRYCK").copy(vad = "Mat")
        val sorterad = sorteraEfterNarmasteFrist(listOf(mat, aktiv), idag)
        assertEquals(listOf(aktiv.vad, "Mat"), sorterad.map { it.vad })
    }
}
