package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OppetKopTest {
    private val kop = LocalDate.of(2026, 9, 1)

    @Test
    fun slutdagenArSistaDagen() {
        assertEquals(LocalDate.of(2026, 10, 1), oppetKopSlut(kop, 30))
    }

    @Test
    fun statusGallerSlutarSnartOchUtgangen() {
        assertEquals(OppetKopStatus.GALLER, oppetKopStatus(kop, 30, LocalDate.of(2026, 9, 10)))
        assertEquals(OppetKopStatus.SLUTAR_SNART, oppetKopStatus(kop, 30, LocalDate.of(2026, 9, 28)))
        assertEquals(OppetKopStatus.SLUTAR_SNART, oppetKopStatus(kop, 30, LocalDate.of(2026, 10, 1)))
        assertEquals(OppetKopStatus.UTGANGEN, oppetKopStatus(kop, 30, LocalDate.of(2026, 10, 2)))
    }

    @Test
    fun giltigaDagar() {
        assertEquals(30, giltigaOppetKopDagar(30))
        assertEquals(365, giltigaOppetKopDagar(365))
        assertNull(giltigaOppetKopDagar(0))
        assertNull(giltigaOppetKopDagar(366))
        assertNull(giltigaOppetKopDagar(-5))
        assertNull(giltigaOppetKopDagar(null))
    }

    @Test
    fun varutypFranNamn() {
        assertEquals(Varutyp.ELEKTRONIK, Varutyp.franNamn("ELEKTRONIK"))
        assertNull(Varutyp.franNamn("okänd"))
        assertNull(Varutyp.franNamn(null))
    }

    @Test
    fun tolkarOppetKopFranKvittotext() {
        assertEquals(30, tolkaOppetKop("ÖPPET KÖP I 30 DAGAR MOT UPPVISANDE AV KVITTO"))
        assertEquals(60, tolkaOppetKop("60 dagars öppet köp"))
        assertEquals(60, tolkaOppetKop("60-dagars öppet köp"))
        assertEquals(14, tolkaOppetKop("Öppet köp: 14 dgr"))
        assertEquals(30, tolkaOppetKop("OPPET KOP I 30 DAGAR"))
    }

    @Test
    fun ingetOppetKopUtanAntalEllerOverEttAr() {
        assertNull(tolkaOppetKop("Öppet köp"))
        assertNull(tolkaOppetKop("öppet köp i 400 dagar"))
        assertNull(tolkaOppetKop("Totalt 30 dagar"))
        assertNull(tolkaOppetKop(""))
    }

    @Test
    fun oppetKopNarKvittotHarFlerRaderAvLasning() {
        val text = "BIL TEMA\nTotalt 79,80\nÖPPET KÖP I 30 DAGAR\nMOT UPPVISANDE AV KVITTO"
        val forslag = tolkaKvitto(text, LocalDate.of(2026, 10, 12))
        assertEquals(30, forslag.oppetKopDagar)
    }

    @Test
    fun oppetKopFyllsBaraNarDetSaknas() {
        val forslag = KvittoForslag(null, null, null, 30)
        val idag = LocalDate.of(2026, 10, 12)
        val tomt = fyllTommaFalt(forslag, "", "", idag, idag, oppetKopDagar = null)
        assertEquals(30, tomt.oppetKopDagar)
        assertEquals(true, tomt.oppetKopFranKvitto)
        val ifyllt = fyllTommaFalt(forslag, "", "", idag, idag, oppetKopDagar = 14)
        assertEquals(14, ifyllt.oppetKopDagar)
        assertEquals(false, ifyllt.oppetKopFranKvitto)
    }
}
