package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class SenasteArbetsdagTest {
    private fun d(datum: String, start: String?, slut: String?, typ: DagTyp = DagTyp.ARBETE) =
        Dag(LocalDate.parse(datum), start?.let(LocalTime::parse), slut?.let(LocalTime::parse), 30, typ)

    @Test
    fun valjerSenasteAvslutadeArbetsdagForeDatum() {
        val dagar = listOf(
            d("2026-10-19", "07:30", "16:00"),
            d("2026-10-20", "08:00", "17:00"),
            d("2026-10-21", "08:00", null),
            d("2026-10-22", null, null, DagTyp.SEMESTER),
            d("2026-10-26", "09:00", "18:00"),
        )
        assertEquals(LocalDate.parse("2026-10-20"), senasteArbetsdagFore(dagar, LocalDate.parse("2026-10-23"))?.datum)
        assertEquals(LocalDate.parse("2026-10-19"), senasteArbetsdagFore(dagar, LocalDate.parse("2026-10-20"))?.datum)
    }

    @Test
    fun ingenTidigareArbetsdag() {
        assertNull(senasteArbetsdagFore(emptyList(), LocalDate.parse("2026-10-23")))
        assertNull(senasteArbetsdagFore(listOf(d("2026-10-23", "08:00", "17:00")), LocalDate.parse("2026-10-23")))
    }
}
