package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ButikKontaktTest {
    @Test
    fun hittarEpostTelefonOchOrgnr() {
        val text = """
            BIL TEMA SWEDEN AB/LANDSKRONA 160
            ORGANISATIONSNR: 556297-3320
            ÖPPETTIDER: MÅN-FRE 7-20
            TELEFONNUMMER: 077-520 00 00
            kundtjanst@Biltema.se
        """.trimIndent()
        val k = hittaButikKontakt(text)
        assertEquals("kundtjanst@biltema.se", k.epost)
        assertEquals("077-520 00 00", k.telefon)
        assertEquals("556297-3320", k.orgnr)
    }

    @Test
    fun personnummerSomOrganisationsnummerSparasAldrig() {
        assertNull(hittaButikKontakt("Org.nr: 801102-4109").orgnr)
        assertNull(hittaButikKontakt("Org 850709-1234").orgnr)
        assertEquals("556000-0000", hittaButikKontakt("Org.nr 556000-0000").orgnr)
    }

    @Test
    fun orgnrUtanNyckelordSparasInte() {
        assertNull(hittaButikKontakt("Kund 556000-0000").orgnr)
    }

    @Test
    fun telefonBaraMedNyckelordOchNullInteEnTelefon() {
        assertNull(hittaButikKontakt("Telefonnummer: null").telefon)
        assertNull(hittaButikKontakt("Ordernr 0771234567").telefon)
        assertEquals("08-123 45", hittaButikKontakt("Tel 08-123 45").telefon)
    }

    @Test
    fun telefonPaRadenEfterNyckelordet() {
        assertEquals("040-123 45 67", hittaButikKontakt("Telefon:\n040-123 45 67").telefon)
    }

    @Test
    fun tomTextOchArTom() {
        assertTrue(hittaButikKontakt("").arTom)
        assertTrue(hittaButikKontakt("Bara text").arTom)
    }
}
