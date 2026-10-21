package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponentgrupp

class NyttjandeperioderTabellGransfallTest {

    private val tabell = lasNyttjandeperioderFranResurs()

    @Test
    fun enRadPerGrupp() {
        assertTrue(tabell.size >= 12)
        val grupper = tabell.map { it.grupp }.toSet()
        Komponentgrupp.entries.forEach { grupp ->
            assertTrue("$grupp saknas i tabellen", grupp in grupper)
        }
    }

    @Test
    fun forslagInomIntervall() {
        tabell.filter { it.intervallAr != null }.forEach { rad ->
            val intervall = rad.intervallAr!!
            assertTrue(
                "${rad.grupp}: forslag ${rad.forslagAr} utanför ${intervall.first}..${intervall.last}",
                rad.forslagAr in intervall,
            )
        }
    }

    @Test
    fun markOchGarageNollAndel() {
        val rad = tabell.find { it.grupp == Komponentgrupp.MARK_OCH_GARAGE }
        assertNotNull(rad)
        assertEquals(0, rad!!.standardandelProcent)
        assertEquals(0, rad.forslagAr)
        assertEquals(null, rad.intervallAr)
    }

    @Test
    fun summaAndelarUtanMark() {
        val summa = tabell
            .filter { it.grupp != Komponentgrupp.MARK_OCH_GARAGE }
            .sumOf { it.standardandelProcent }
        assertEquals(100, summa)
    }

    @Test
    fun stommeEnligtExempel() {
        val stomme = tabell.find { it.grupp == Komponentgrupp.STOMME }
        assertNotNull(stomme)
        assertEquals(100, stomme!!.forslagAr)
        assertEquals(75..120, stomme.intervallAr)
        assertEquals(63, stomme.standardandelProcent)
    }
}
