package se.tmconnect.husloggen.widget

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Uppgift

class KommandeWidgetRaderTest {
    private val idag = LocalDate.of(2026, 10, 24)

    private fun uppgift(namn: String, start: LocalDate, bostad: Long = 1, senast: LocalDate? = null, intervall: Int = 12) =
        Uppgift(bostadId = bostad, namn = namn, intervallManader = intervall, startDatum = start, senastGjord = senast)

    @Test
    fun narmasteTreIDatumordning() {
        val lista =
            listOf(
                uppgift("Sotning", LocalDate.of(2027, 3, 1)),
                uppgift("Filter", LocalDate.of(2026, 11, 1)),
                uppgift("Hängrännor", LocalDate.of(2026, 10, 1)),
                uppgift("Takpapp", LocalDate.of(2030, 1, 1)),
            )
        val rader = kommandeWidgetRader(lista, bostadId = 1, idag = idag)
        assertEquals(listOf("Hängrännor", "Filter", "Sotning"), rader.map { it.namn })
        assertEquals(listOf(true, false, false), rader.map { it.forsenad })
    }

    @Test
    fun nastaDatumEfterSenastGjord() {
        val rad = kommandeWidgetRader(
            listOf(uppgift("Filter", LocalDate.of(2025, 1, 1), senast = LocalDate.of(2026, 9, 1), intervall = 3)),
            bostadId = 1,
            idag = idag,
        ).single()
        assertEquals(LocalDate.of(2026, 12, 1), rad.datum)
    }

    @Test
    fun baraValdBostad() {
        val lista = listOf(uppgift("A", idag, bostad = 1), uppgift("B", idag, bostad = 2))
        assertEquals(listOf("B"), kommandeWidgetRader(lista, bostadId = 2, idag = idag).map { it.namn })
        assertEquals(2, kommandeWidgetRader(lista, bostadId = null, idag = idag).size)
    }

    @Test
    fun tomLista() {
        assertEquals(emptyList<WidgetUppgiftRad>(), kommandeWidgetRader(emptyList(), 1, idag))
    }
}
