package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class GenvagStamplaTest {
    private val datum = LocalDate.of(2026, 10, 23)

    @Test
    fun stamplaInUtanStartSpararAvrundadTid() {
        val nu = LocalTime.of(8, 47, 59)
        val plan =
            planeraStamplaIn(
                befintlig = null,
                datum = datum,
                nu = nu,
                standardRastMin = 30,
                antalDagar = 0,
                arPro = false,
            )
        assertTrue(plan is PlaneradStamplaIn.Spara)
        val spara = plan as PlaneradStamplaIn.Spara
        assertEquals(LocalTime.of(8, 47), spara.dag.start)
        assertEquals(datum, spara.dag.datum)
        assertEquals(30, spara.dag.rastMin)
        assertEquals(DagTyp.ARBETE, spara.dag.typ)
    }

    @Test
    fun stamplaInMedStartAndrarInte() {
        val start = LocalTime.of(7, 55)
        val dag = Dag(datum = datum, start = start, rastMin = 45)
        val plan =
            planeraStamplaIn(
                befintlig = dag,
                datum = datum,
                nu = LocalTime.of(9, 0),
                standardRastMin = 30,
                antalDagar = 1,
                arPro = false,
            )
        assertEquals(PlaneradStamplaIn.Oforandrad(start), plan)
    }

    @Test
    fun stamplaInNyDagOverGransBlockeras() {
        val plan =
            planeraStamplaIn(
                befintlig = null,
                datum = datum,
                nu = LocalTime.of(8, 0),
                standardRastMin = 30,
                antalDagar = 15,
                arPro = false,
            )
        assertEquals(PlaneradStamplaIn.GransUtanPro, plan)
    }

    @Test
    fun stamplaUtMedStartUtanSlutSpararNu() {
        val dag = Dag(datum = datum, start = LocalTime.of(8, 0), rastMin = 30)
        val plan = planeraStamplaUt(befintlig = dag, nu = LocalTime.of(16, 5, 12))
        assertTrue(plan is PlaneradStamplaUt.Spara)
        assertEquals(LocalTime.of(16, 5), (plan as PlaneradStamplaUt.Spara).dag.slut)
    }

    @Test
    fun stamplaUtUtanStartAndrarInte() {
        val plan = planeraStamplaUt(befintlig = null, nu = LocalTime.of(17, 0))
        assertEquals(PlaneradStamplaUt.IngenInstampling, plan)
    }

    @Test
    fun stamplaUtSlutForeStartOgiltigt() {
        val dag = Dag(datum = datum, start = LocalTime.of(17, 0))
        val plan = planeraStamplaUt(befintlig = dag, nu = LocalTime.of(8, 0))
        assertTrue(plan is PlaneradStamplaUt.OgiltigTid)
        assertEquals(TidValideringFel.SlutForeStart, (plan as PlaneradStamplaUt.OgiltigTid).fel)
    }

    @Test
    fun stamplaUtRedanSlutAndrarInte() {
        val slut = LocalTime.of(16, 0)
        val dag = Dag(datum = datum, start = LocalTime.of(8, 0), slut = slut)
        val plan = planeraStamplaUt(befintlig = dag, nu = LocalTime.of(18, 0))
        assertEquals(PlaneradStamplaUt.Oforandrad(slut), plan)
    }

    @Test
    fun avrundaTillHelMinutBehallerMinut() {
        assertEquals(LocalTime.of(12, 30), avrundaTillHelMinut(LocalTime.of(12, 30, 45)))
    }
}
