package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.flextid.data.Dag

class VeckoTextTest {
    @Test
    fun vecka42ExempelFranUppgiften() {
        val dagar =
            listOf(
                Dag(
                    datum = LocalDate.of(2026, 10, 12),
                    start = LocalTime.of(8, 0),
                    slut = LocalTime.of(16, 30),
                    rastMin = 30,
                ),
                Dag(datum = LocalDate.of(2026, 10, 13), typ = DagTyp.SEMESTER),
                Dag(
                    datum = LocalDate.of(2026, 10, 14),
                    start = LocalTime.of(7, 45),
                    slut = LocalTime.of(16, 45),
                    rastMin = 30,
                ),
                Dag(
                    datum = LocalDate.of(2026, 10, 15),
                    start = LocalTime.of(8, 0),
                    slut = null,
                ),
            )
        val texter =
            VeckoTextTexter(
                rubrik = "Vecka 42",
                veckodagar =
                    mapOf(
                        DayOfWeek.MONDAY to "Mån",
                        DayOfWeek.TUESDAY to "Tis",
                        DayOfWeek.WEDNESDAY to "Ons",
                        DayOfWeek.THURSDAY to "Tors",
                        DayOfWeek.FRIDAY to "Fre",
                        DayOfWeek.SATURDAY to "Lör",
                        DayOfWeek.SUNDAY to "Sön",
                    ),
                typEtiketter = mapOf(DagTyp.SEMESTER to "Semester"),
                summaEtikett = "Summa",
            )
        val forvantat =
            """
            Vecka 42
            Mån 08:00–16:30 (8:00)
            Tis Semester
            Ons 07:45–16:45 (8:30)
            Tors 08:00–
            Summa 16:30
            """.trimIndent()

        val resultat =
            veckoText(
                dagar = dagar,
                mandag = LocalDate.of(2026, 10, 12),
                texter = texter,
                locale = Locale.forLanguageTag("sv-SE"),
            )
        assertEquals(forvantat, resultat)
    }
}
