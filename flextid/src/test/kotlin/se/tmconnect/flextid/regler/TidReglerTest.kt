package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class TidReglerTest {
    private val installning = Installning(normaltidMinPerDag = 480, standardRastMin = 30)

    @Test
    fun exempel0800Till1630Rast30() {
        val arbetat = arbetadeMinuter(LocalTime.of(8, 0), LocalTime.of(16, 30), 30)
        assertEquals(480, arbetat)
        val utfall =
            dagensUtfall(
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                installning.normaltidMinPerDag,
                installning.arbetsdagarMask,
            )
        assertEquals(0, utfall)
    }

    @Test
    fun exempel0730Till1700Rast30() {
        val arbetat = arbetadeMinuter(LocalTime.of(7, 30), LocalTime.of(17, 0), 30)
        assertEquals(540, arbetat)
        val utfall =
            dagensUtfall(
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(7, 30),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
                installning.normaltidMinPerDag,
                installning.arbetsdagarMask,
            )
        assertEquals(60, utfall)
    }

    @Test
    fun exempel0800Till1500Rast30() {
        val utfall =
            dagensUtfall(
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(15, 0),
                    rastMin = 30,
                ),
                installning.normaltidMinPerDag,
                installning.arbetsdagarMask,
            )
        assertEquals(-90, utfall)
    }

    @Test
    fun semesterGerUtfallNoll() {
        val utfall =
            dagensUtfall(
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    typ = DagTyp.SEMESTER,
                ),
                installning.normaltidMinPerDag,
                installning.arbetsdagarMask,
            )
        assertEquals(0, utfall)
    }

    @Test
    fun ingangssaldoPlusUtfall() {
        val inst = installning.copy(ingangssaldoMin = -120)
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(7, 30),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
            )
        assertEquals(-60, beraknaSaldo(dagar, inst))
    }

    @Test
    fun slutForeStartAvvisas() {
        assertEquals(
            TidValideringFel.SlutForeStart,
            valideraTider(LocalTime.of(16, 0), LocalTime.of(8, 0)),
        )
        assertNull(valideraTider(LocalTime.of(8, 0), LocalTime.of(16, 0)))
    }
}
