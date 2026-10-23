package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VanligDagForslagTest {
    private val start = LocalTime.of(8, 0)
    private val slut = LocalTime.of(16, 30)
    private val rast = 30

    @Test
    fun onsdagMedStandardtiderGerForslag() {
        val onsdag = LocalDate.of(2026, 10, 21)
        val forslag =
            dagsForslagVanligDag(onsdag, ARBETSDAGAR_MANDAG_FREDAG, start, slut, rast)
        requireNotNull(forslag)
        assertEquals(start, forslag.start)
        assertEquals(slut, forslag.slut)
        assertEquals(30, forslag.rastMin)
        assertEquals(
            480,
            arbetadeMinuter(forslag.start, forslag.slut, forslag.rastMin),
        )
    }

    @Test
    fun lordagMedStandardmaskGerIngetForslag() {
        val lordag = LocalDate.of(2026, 10, 24)
        assertNull(
            dagsForslagVanligDag(lordag, ARBETSDAGAR_MANDAG_FREDAG, start, slut, rast),
        )
    }

    @Test
    fun slutForeStartGerIngetForslag() {
        val onsdag = LocalDate.of(2026, 10, 21)
        assertNull(
            dagsForslagVanligDag(
                onsdag,
                ARBETSDAGAR_MANDAG_FREDAG,
                LocalTime.of(8, 0),
                LocalTime.of(7, 0),
                rast,
            ),
        )
        assertNull(
            dagsForslagVanligDag(
                onsdag,
                ARBETSDAGAR_MANDAG_FREDAG,
                LocalTime.of(8, 0),
                LocalTime.of(8, 0),
                rast,
            ),
        )
    }

    @Test
    fun helgdagGerIngetForslag() {
        val nyarsdagen = LocalDate.of(2026, 1, 1)
        assertNull(
            dagsForslagVanligDag(nyarsdagen, ARBETSDAGAR_MANDAG_FREDAG, start, slut, rast),
        )
    }
}
