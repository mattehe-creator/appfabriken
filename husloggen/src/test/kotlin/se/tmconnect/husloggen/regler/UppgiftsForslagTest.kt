package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UppgiftsForslagTest {
    @Test
    fun uppgiftsforslagLista() {
        assertEquals(8, UPPGIFTSFORSLAG.size)
        assertEquals(UPPGIFTSFORSLAG.size, UPPGIFTSFORSLAG.map { it.id }.toSet().size)
        UPPGIFTSFORSLAG.forEach { assertTrue(it.intervallManader in 1..120) }
        assertEquals(IntervallBestamdAv.KOMMUNEN, UPPGIFTSFORSLAG.first { it.id == "sotning" }.bestamdAv)
        assertEquals(
            IntervallBestamdAv.TILLVERKAREN,
            UPPGIFTSFORSLAG.first { it.id == "service_varmepump" }.bestamdAv,
        )
        assertEquals(
            IntervallBestamdAv.TILLVERKAREN,
            UPPGIFTSFORSLAG.first { it.id == "service_ventilation" }.bestamdAv,
        )
    }
}
