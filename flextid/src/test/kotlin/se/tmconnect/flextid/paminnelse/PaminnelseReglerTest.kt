package se.tmconnect.flextid.paminnelse

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp

class PaminnelseReglerTest {
    private val installning = Installning(normaltidMinPerDag = 480, standardRastMin = 30)

    @Test
    fun stamplaUtPaminnelseTid0800Plus8hPlusRast() {
        val tid = beraknaStamplaUtPaminnelseTid(
            start = LocalTime.of(8, 0),
            normaltidMinPerDag = 480,
            rastMin = 30,
        )
        assertEquals(LocalTime.of(16, 30), tid)
    }

    @Test
    fun ingenStamplaUtPaminnelseNarDagenHarSlut() {
        val dag =
            Dag(
                datum = LocalDate.of(2026, 10, 18),
                start = LocalTime.of(8, 0),
                slut = LocalTime.of(16, 30),
                rastMin = 30,
                typ = DagTyp.ARBETE,
            )
        assertFalse(
            skaPaminnelseStamplaUt(dag, installning, nu = LocalTime.of(17, 0)),
        )
    }

    @Test
    fun stamplaUtPaminnelseNarStartUtanSlutOchTidPasserad() {
        val dag =
            Dag(
                datum = LocalDate.of(2026, 10, 18),
                start = LocalTime.of(8, 0),
                slut = null,
                rastMin = 30,
                typ = DagTyp.ARBETE,
            )
        assertFalse(
            skaPaminnelseStamplaUt(dag, installning, nu = LocalTime.of(16, 0)),
        )
        assertTrue(
            skaPaminnelseStamplaUt(dag, installning, nu = LocalTime.of(16, 30)),
        )
    }
}
