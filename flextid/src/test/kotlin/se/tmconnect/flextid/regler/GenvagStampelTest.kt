package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class GenvagStampelTest {
    private val datum = LocalDate.of(2026, 10, 23)

    @Test
    fun stamplaInUtanStartAvrundarTillHelMinut() {
        val nu = LocalTime.of(8, 7, 42)
        val beslut = beslutGenvagStamplaIn(null, nu)
        assertTrue(beslut is GenvagStamplaInBeslut.SparaStart)
        assertEquals(LocalTime.of(8, 7), (beslut as GenvagStamplaInBeslut.SparaStart).start)
    }

    @Test
    fun stamplaInMedStartAndrarInte() {
        val start = LocalTime.of(7, 55)
        val dag = Dag(datum = datum, start = start)
        val beslut = beslutGenvagStamplaIn(dag, LocalTime.of(9, 0))
        assertTrue(beslut is GenvagStamplaInBeslut.RedanInstamplad)
        assertEquals(start, (beslut as GenvagStamplaInBeslut.RedanInstamplad).start)
    }

    @Test
    fun stamplaUtMedStartSatterSlut() {
        val dag = Dag(datum = datum, start = LocalTime.of(8, 0))
        val nu = LocalTime.of(16, 3, 59)
        val beslut = beslutGenvagStamplaUt(dag, nu)
        assertTrue(beslut is GenvagStamplaUtBeslut.SparaSlut)
        assertEquals(LocalTime.of(16, 3), (beslut as GenvagStamplaUtBeslut.SparaSlut).slut)
    }

    @Test
    fun stamplaUtUtanStart() {
        val beslut = beslutGenvagStamplaUt(null, LocalTime.of(17, 0))
        assertEquals(GenvagStamplaUtBeslut.IngenInstempling, beslut)
    }

    @Test
    fun stamplaUtUtanStartMenDagFinns() {
        val dag = Dag(datum = datum, typ = DagTyp.SEMESTER)
        val beslut = beslutGenvagStamplaUt(dag, LocalTime.of(17, 0))
        assertEquals(GenvagStamplaUtBeslut.IngenInstempling, beslut)
    }
}
