package se.tmconnect.husloggen.export

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Matare

class SakerhetskopiaMatareAvlasningTest {
    @Test
    fun testMatareTillSakerhetskopia() {
        val matare =
            Matare(
                id = 7,
                bostadId = 3,
                namn = "El",
                enhet = "kWh",
                anteckning = "Huvudmätare",
                prisOrePerEnhet = 150,
            )
        assertEquals(
            SakerhetskopiaMatare(
                namn = "El",
                enhet = "kWh",
                anteckning = "Huvudmätare",
                bostadIndex = 0,
                prisOrePerEnhet = 150,
            ),
            matareTillSakerhetskopia(matare, mapOf(3L to 0)),
        )
    }

    @Test
    fun testMatareTillSakerhetskopiaSaknadBostadKastar() {
        val matare = Matare(id = 7, bostadId = 3, namn = "El", enhet = "kWh")
        assertThrows(NoSuchElementException::class.java) {
            matareTillSakerhetskopia(matare, mapOf(1L to 0))
        }
    }

    @Test
    fun testSakerhetskopiaMatareTillMatareTrimmarOchTarBortNegativtPris() {
        val resultat =
            sakerhetskopiaMatareTillMatare(
                SakerhetskopiaMatare(
                    namn = "  Vatten ",
                    enhet = " m3 ",
                    anteckning = "  ",
                    prisOrePerEnhet = -5,
                ),
                bostadId = 2,
            )
        assertEquals(0L, resultat.id)
        assertEquals(2L, resultat.bostadId)
        assertEquals("Vatten", resultat.namn)
        assertEquals("m3", resultat.enhet)
        assertNull(resultat.anteckning)
        assertNull(resultat.prisOrePerEnhet)
    }

    @Test
    fun testSakerhetskopiaMatareTillMatareNollPrisBehalls() {
        val resultat =
            sakerhetskopiaMatareTillMatare(
                SakerhetskopiaMatare(namn = "El", enhet = "kWh", prisOrePerEnhet = 0),
                bostadId = 1,
            )
        assertEquals(0L, resultat.prisOrePerEnhet)
    }

    @Test
    fun testAvlasningTillSakerhetskopia() {
        val avlasning =
            Avlasning(
                id = 5,
                matareId = 9,
                datum = LocalDate.of(2026, 3, 1),
                varde = 12_345_600,
            )
        assertEquals(
            SakerhetskopiaAvlasning(
                matareIndex = 2,
                datum = LocalDate.of(2026, 3, 1),
                vardeTusendel = 12_345_600,
            ),
            avlasningTillSakerhetskopia(avlasning, mapOf(9L to 2)),
        )
    }

    @Test
    fun testSakerhetskopiaAvlasningTillAvlasning() {
        val resultat =
            sakerhetskopiaAvlasningTillAvlasning(
                SakerhetskopiaAvlasning(
                    matareIndex = 1,
                    datum = LocalDate.of(2026, 3, 1),
                    vardeTusendel = 500,
                ),
                matareId = 42,
            )
        assertEquals(0L, resultat.id)
        assertEquals(42L, resultat.matareId)
        assertEquals(LocalDate.of(2026, 3, 1), resultat.datum)
        assertEquals(500L, resultat.varde)
    }

    @Test
    fun testArSakerhetskopiaMatareLikIgnorerarIndexOchPris() {
        assertTrue(
            arSakerhetskopiaMatareLik(
                SakerhetskopiaMatare("El", "kWh", null, 0, 150),
                SakerhetskopiaMatare("El", "kWh", null, 2, 99),
            ),
        )
    }

    @Test
    fun testArSakerhetskopiaMatareLikSkiljerEnhet() {
        assertFalse(
            arSakerhetskopiaMatareLik(
                SakerhetskopiaMatare("El", "kWh"),
                SakerhetskopiaMatare("El", "KWh"),
            ),
        )
    }

    @Test
    fun testArSakerhetskopiaAvlasningLikSamma() {
        assertTrue(
            arSakerhetskopiaAvlasningLik(
                SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500),
                SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500),
            ),
        )
    }

    @Test
    fun testArSakerhetskopiaAvlasningLikSkiljerVarde() {
        assertFalse(
            arSakerhetskopiaAvlasningLik(
                SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 500),
                SakerhetskopiaAvlasning(0, LocalDate.of(2026, 1, 1), 501),
            ),
        )
    }
}
