package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class SaldoVidOchMedTest {
    private val installning = Installning(ingangssaldoMin = 60)
    private val dagar =
        listOf(
            Dag(
                datum = LocalDate.of(2026, 10, 19),
                start = LocalTime.of(8, 0),
                slut = LocalTime.of(17, 0),
                rastMin = 30,
            ),
            Dag(
                datum = LocalDate.of(2026, 10, 20),
                start = LocalTime.of(8, 0),
                slut = LocalTime.of(16, 15),
                rastMin = 30,
            ),
            Dag(datum = LocalDate.of(2026, 10, 21), typ = DagTyp.FLEX),
            Dag(datum = LocalDate.of(2026, 10, 22), start = LocalTime.of(8, 0)),
            Dag(datum = LocalDate.of(2026, 10, 23), typ = DagTyp.SEMESTER),
            Dag(
                datum = LocalDate.of(2026, 10, 24),
                start = LocalTime.of(10, 0),
                slut = LocalTime.of(12, 0),
            ),
            Dag(
                datum = LocalDate.of(2026, 10, 26),
                start = LocalTime.of(8, 0),
                slut = LocalTime.of(18, 0),
            ),
        )

    @Test
    fun arKomplettDagMedStartOchSlut() {
        assertTrue(
            arKomplettDag(
                Dag(
                    datum = LocalDate.of(2026, 10, 19),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(17, 0),
                ),
            ),
        )
    }

    @Test
    fun arKomplettDagUtanSlut() {
        assertFalse(
            arKomplettDag(
                Dag(
                    datum = LocalDate.of(2026, 10, 19),
                    start = LocalTime.of(8, 0),
                ),
            ),
        )
    }

    @Test
    fun arKomplettDagUtanStart() {
        assertFalse(
            arKomplettDag(
                Dag(
                    datum = LocalDate.of(2026, 10, 19),
                    slut = LocalTime.of(17, 0),
                ),
            ),
        )
    }

    @Test
    fun avrundaTillHelMinutNedatMedNanosekunder() {
        assertEquals(
            LocalTime.of(8, 5),
            avrundaTillHelMinut(LocalTime.of(8, 5, 59, 999_000_000)),
        )
    }

    @Test
    fun avrundaTillHelMinutNedatMedSekunder() {
        assertEquals(
            LocalTime.of(23, 59),
            avrundaTillHelMinut(LocalTime.of(23, 59, 30)),
        )
    }

    @Test
    fun saldoIngaDagarAnnu() {
        assertEquals(60, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 18)))
    }

    @Test
    fun saldoEfterForstaDagen() {
        assertEquals(90, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 19)))
    }

    @Test
    fun saldoEfterAndraDagen() {
        assertEquals(75, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 20)))
    }

    @Test
    fun saldoEfterFlexDag() {
        assertEquals(-405, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 21)))
    }

    @Test
    fun saldoTillFredagSemesterOchEjUtstämplad() {
        assertEquals(-405, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 23)))
    }

    @Test
    fun saldoTillLordag() {
        assertEquals(-285, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 24)))
    }

    @Test
    fun saldoTillSlutetAvOktober() {
        assertEquals(-165, saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 31)))
    }

    @Test
    fun saldoOberoendeAvDagsordning() {
        assertEquals(-165, saldoVidOchMed(dagar.reversed(), installning, LocalDate.of(2026, 10, 31)))
    }

    @Test
    fun saldoTomLista() {
        assertEquals(
            -30,
            saldoVidOchMed(emptyList(), Installning(ingangssaldoMin = -30), LocalDate.of(2026, 10, 31)),
        )
    }
}
