package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning

class ManadsRapportTest {
    private val locale = Locale.US
    private val installning =
        Installning(
            normaltidMinPerDag = 480,
            standardRastMin = 30,
            ingangssaldoMin = 120,
        )

    private fun texter(manadAr: String = "October 2026") =
        ManadsRapportTexter(
            manadArRubrik = manadAr,
            ingaRegistreradeDagar = "No registered days",
            summaArbetadEtikett = "Total worked",
            summaUtfallEtikett = "Total balance change",
            saldoManadsslutEtikett = "Balance at month end",
            kolumnRubriker = emptyList(),
            typEtiketter = DagTyp.entries.associateWith { it.name },
            veckodagEtiketter =
                mapOf(
                    java.time.DayOfWeek.MONDAY to "Mon",
                    java.time.DayOfWeek.TUESDAY to "Tue",
                    java.time.DayOfWeek.WEDNESDAY to "Wed",
                ),
            saknasTecken = "-",
        )

    @Test
    fun treDagarOktober2026SummorOchSaldo() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(7, 30),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 14),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(15, 0),
                    rastMin = 30,
                ),
            )
        val rapport = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertEquals(3, rapport.rader.size)
        assertEquals("8:00", rapport.rader[0].arbetad)
        assertEquals("0:00", rapport.rader[0].utfall)
        assertEquals("9:00", rapport.rader[1].arbetad)
        assertEquals("+1:00", rapport.rader[1].utfall)
        assertEquals("6:30", rapport.rader[2].arbetad)
        assertEquals("-1:30", rapport.rader[2].utfall)
        assertEquals("23:30", rapport.summaArbetad)
        assertEquals("-0:30", rapport.summaUtfall)
        assertEquals("+1:30", rapport.saldoManadsslut)
        assertTrue(!rapport.ingaDagar)
    }

    @Test
    fun semesterRaknasInteISummaArbetad() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    typ = DagTyp.SEMESTER,
                ),
            )
        val rapport = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertEquals("0:00", rapport.rader[1].utfall)
        assertEquals("8:00", rapport.summaArbetad)
        assertEquals("0:00", rapport.summaUtfall)
    }

    @Test
    fun startUtanSlutVisasMenRaknasEjISummor() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = null,
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 13),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
            )
        val rapport = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertEquals("-", rapport.rader[0].slut)
        assertEquals("-", rapport.rader[0].arbetad)
        assertEquals("-", rapport.rader[0].utfall)
        assertEquals("8:00", rapport.summaArbetad)
        assertEquals("0:00", rapport.summaUtfall)
    }

    @Test
    fun tomManadIngaRaderSaldoOforandrat() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 9, 30),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
            )
        val rapport = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertTrue(rapport.ingaDagar)
        assertTrue(rapport.rader.isEmpty())
        assertNull(rapport.summaArbetad)
        assertNull(rapport.summaUtfall)
        assertEquals("+2:00", rapport.saldoManadsslut)
    }

    @Test
    fun dagarForeManadsslutIngarISaldo() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 9, 15),
                    start = LocalTime.of(7, 30),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
            )
        val rapport = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertEquals("+3:00", rapport.saldoManadsslut)
    }

    @Test
    fun dagEfterManadsslutIngarEjISaldo() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 30),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 11, 1),
                    start = LocalTime.of(7, 30),
                    slut = LocalTime.of(17, 0),
                    rastMin = 30,
                ),
            )
        val oktober = byggManadsRapport(dagar, installning, YearMonth.of(2026, 10), texter(), locale)
        assertEquals("+2:00", oktober.saldoManadsslut)
    }
}
