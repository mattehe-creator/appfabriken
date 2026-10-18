package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.paminnelse.skaPaminnelseStamplaIn
import se.tmconnect.flextid.data.Installning

class StamplaInPaminnelseTest {
    private val installning = Installning(normaltidMinPerDag = 480, standardRastMin = 30)
    private val mandag = LocalDate.of(2026, 10, 12)

    @Test
    fun ingetSparatVardeGer0800() {
        assertEquals(
            LocalTime.of(8, 0),
            stamplaInPaminnelseKlockan(normaliseraStamplaInPaminnelseMinEfterMidnatt(null)),
        )
    }

    @Test
    fun ogiltigtVardeGer0800() {
        assertEquals(480, normaliseraStamplaInPaminnelseMinEfterMidnatt(90))
        assertEquals(480, normaliseraStamplaInPaminnelseMinEfterMidnatt(13 * 60))
    }

    @Test
    fun nastaPaminnelseIdag0630NarKlockan0600() {
        val nasta =
            beraknaNastaStamplaInPaminnelse(
                idag = mandag,
                nu = LocalTime.of(6, 0),
                dag = null,
                arbetsdagarMask = installning.arbetsdagarMask,
                paminnelseMinEfterMidnatt = 6 * 60 + 30,
            )
        assertEquals(LocalDateTime.of(mandag, LocalTime.of(6, 30)), nasta)
    }

    @Test
    fun nastaPaminnelseNastaArbetsdag0630NarKlockan0700() {
        val nasta =
            beraknaNastaStamplaInPaminnelse(
                idag = mandag,
                nu = LocalTime.of(7, 0),
                dag = null,
                arbetsdagarMask = installning.arbetsdagarMask,
                paminnelseMinEfterMidnatt = 6 * 60 + 30,
            )
        assertEquals(
            LocalDateTime.of(mandag.plusDays(1), LocalTime.of(6, 30)),
            nasta,
        )
    }

    @Test
    fun ingenPaminnelseStamplaInNarDagenHarStart() {
        val dag =
            Dag(
                datum = mandag,
                start = LocalTime.of(7, 0),
                slut = null,
                rastMin = 30,
                typ = DagTyp.ARBETE,
            )
        assertFalse(
            skaPaminnelseStamplaIn(
                dag = dag,
                installning = installning,
                idag = mandag,
                nu = LocalTime.of(9, 0),
                stamplaInPaminnelseMinEfterMidnatt = 6 * 60 + 30,
            ),
        )
        val nasta =
            beraknaNastaStamplaInPaminnelse(
                idag = mandag,
                nu = LocalTime.of(9, 0),
                dag = dag,
                arbetsdagarMask = installning.arbetsdagarMask,
                paminnelseMinEfterMidnatt = 6 * 60 + 30,
            )
        assertEquals(
            LocalDateTime.of(mandag.plusDays(1), LocalTime.of(6, 30)),
            nasta,
        )
    }

    @Test
    fun giltigaAlternativVarjeHalvtimmeMellan05Och12() {
        val lista = giltigaStamplaInPaminnelseMinEfterMidnatt()
        assertEquals(15, lista.size)
        assertEquals(5 * 60, lista.first())
        assertEquals(12 * 60, lista.last())
        assertEquals(30, lista[1] - lista[0])
    }
}
