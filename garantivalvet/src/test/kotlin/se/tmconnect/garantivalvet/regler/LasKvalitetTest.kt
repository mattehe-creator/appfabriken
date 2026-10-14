package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LasKvalitetTest {
    private val idag = LocalDate.of(2026, 10, 14)

    @Test
    fun ikeaKannsIgenTrotsAvlasningsfel() {
        assertEquals("IKEA", hittaButik(listOf("[AEA Svenske", "Kvitto")))
        assertEquals("IKEA", hittaButik(listOf("Ordernr 123", "IKEA Svenska Forsaljnings AB")))
    }

    @Test
    fun biltemaKannsIgenOchSkrapRadVinnerInte() {
        assertEquals("BIL TEMA SWEDEN AB", hittaButik(listOf("EORPACKNINGAR ELLER ANVANDA VAROR", "BIL TEMA SWEDEN AB/LANDSKRONA 160")))
    }

    @Test
    fun oppetKopMedIAvlasningsfel() {
        assertEquals(30, tolkaOppetKop("OPPET KOP I 30 DAGAR MOT UPPVISANDE AV KVITTO"))
        assertEquals(30, tolkaOppetKop("DPPET KÖP\n30 DAGAR"))
        assertEquals(30, tolkaOppetKop("30 DAGARS ÖPPET KÖP"))
    }

    @Test
    fun bastaRotationenVinnerNarBildenLiggerPaSidan() {
        val ratt = "BILTEMA\nKvitto 2026-09-01 10:15\nSkrivplatta 79,80\nTotalt 79,80"
        val skrap = "xqz tkp wrb\n1l Il lI\nbvn ghd"
        assertEquals(ratt, valjBastaLasning(listOf(skrap, skrap, ratt, skrap), idag))
    }

    @Test
    fun bildenSomDenArBehallsOmIngenArTydligtBattre() {
        val a = "BILTEMA\nKvitto 2026-09-01 10:15\nTotalt 79,80"
        val b = "BILTEMA\nKvitto 2026-09-01 10:15\nTotalt 79,80\nTack"
        assertEquals(a, valjBastaLasning(listOf(a, b), idag))
    }

    @Test
    fun ingenTextGerNull() {
        assertNull(valjBastaLasning(listOf("", " "), idag))
    }
}
