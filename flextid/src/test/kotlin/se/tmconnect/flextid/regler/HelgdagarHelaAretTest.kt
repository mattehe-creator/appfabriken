package se.tmconnect.flextid.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HelgdagarHelaAretTest {
    private fun d(s: String) = LocalDate.parse(s)

    @Test
    fun rorliga2026() {
        assertEquals(
            listOf("2026-04-03", "2026-04-05", "2026-04-06", "2026-05-14", "2026-05-24").map(::d),
            rorligaHelgdagar(2026).map { it.datum },
        )
    }

    @Test
    fun rorliga2025() {
        assertEquals(
            listOf("2025-04-18", "2025-04-20", "2025-04-21", "2025-05-29", "2025-06-08").map(::d),
            rorligaHelgdagar(2025).map { it.datum },
        )
    }

    @Test
    fun forstaLordagen() {
        assertEquals(d("2026-06-20"), forstaLordagenFran(d("2026-06-20")))
        assertEquals(d("2025-06-21"), forstaLordagenFran(d("2025-06-20")))
        assertEquals(d("2027-11-06"), forstaLordagenFran(d("2027-10-31")))
    }

    @Test
    fun alla2026() {
        val h = svenskaHelgdagar(2026)
        assertEquals(13, h.size)
        assertEquals("Nyårsdagen", h.first().namn)
        assertEquals("Annandag jul", h.last().namn)
        assertEquals(d("2026-06-20"), h.single { it.namn == "Midsommardagen" }.datum)
        assertEquals(d("2026-10-31"), h.single { it.namn == "Alla helgons dag" }.datum)
        assertEquals(h.sortedBy { it.datum }, h)
    }

    @Test
    fun forstaMajOchHimmelsfard2008() {
        val h = svenskaHelgdagar(2008)
        assertEquals(13, h.size)
        assertEquals("Första maj", helgdagPa(d("2008-05-01"), false)?.namn)
    }

    @Test
    fun aftnar() {
        assertEquals(listOf("2026-06-19", "2026-12-24", "2026-12-31").map(::d), svenskaAftnar(2026).map { it.datum })
        assertEquals(d("2027-06-25"), svenskaAftnar(2027).first().datum)
        for (ar in 2000..2100) {
            assertEquals(java.time.DayOfWeek.FRIDAY, svenskaAftnar(ar).first().datum.dayOfWeek)
            assertTrue(svenskaAftnar(ar).all { it.arAfton })
        }
    }

    @Test
    fun uppslag() {
        assertEquals("Långfredagen", helgdagPa(d("2026-04-03"), false)?.namn)
        assertNull(helgdagPa(d("2026-12-24"), false))
        assertEquals("Julafton", helgdagPa(d("2026-12-24"), true)?.namn)
        assertNull(helgdagPa(d("2026-04-07"), true))
        assertTrue(arRodDag(d("2026-06-06")))
        assertFalse(arRodDag(d("2026-06-19")))
        assertTrue(arLedigDag(d("2026-06-19"), true))
        assertFalse(arLedigDag(d("2026-06-19"), false))
        assertTrue(arLedigDag(d("2026-05-14"), false))
    }
}
