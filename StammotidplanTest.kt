package se.tmconnect.styrelserummet.regler

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StammotidplanTest {

    @Test
    fun testTidplan() {
        // Testfall 1: arsslut 2025-12-31, stamma 2026-05-15, kallelse 4..6
        val result1 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 5, 15),
            kallelsefristVeckor = 4..6
        )
        
        assertEquals(LocalDate.of(2026, 5, 15), result1.stammodatum)
        assertEquals(LocalDate.of(2026, 11, 30), result1.senasteStammodag)
        assertEquals(LocalDate.of(2026, 3, 27), result1.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 17), result1.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 24), result1.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 5, 1), result1.forstaDagArsredovisningTillganglig)
        assertEquals(true, result1.stammaInomLagligFrist)

        // Testfall 2: stamma 2026-05-15, kallelse 2..4
        val result2 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 5, 15),
            kallelsefristVeckor = 2..4
        )
        
        assertEquals(LocalDate.of(2026, 5, 15), result2.stammodatum)
        assertEquals(LocalDate.of(2026, 11, 30), result2.senasteStammodag)
        assertEquals(LocalDate.of(2026, 3, 27), result2.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 5, 1), result2.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 4, 24), result2.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 5, 1), result2.forstaDagArsredovisningTillganglig)
        assertEquals(true, result2.stammaInomLagligFrist)

        // Testfall 3: stamma 2026-08-01, samma arsslut → stammaInomLagligFrist false
        val result3 = tidplan(
            arsslut = LocalDate.of(2025, 12, 31),
            stammodatum = LocalDate.of(2026, 8, 1),
            kallelsefristVeckor = 2..4
        )
        
        assertEquals(LocalDate.of(2026, 8, 1), result3.stammodatum)
        assertEquals(LocalDate.of(2026, 11, 30), result3.senasteStammodag)
        assertEquals(LocalDate.of(2026, 5, 27), result3.tidigasteKallelsedag)
        assertEquals(LocalDate.of(2026, 5, 1), result3.senasteKallelsedag)
        assertEquals(LocalDate.of(2026, 5, 27), result3.senasteRevisionTillStyrelsen)
        assertEquals(LocalDate.of(2026, 5, 27), result3.forstaDagArsredovisningTillganglig)
        assertEquals(false, result3.stammaInomLagligFrist)
    }

    @Test
    fun testInvalidKallelsefrist() {
        // Testa att tom range kastar exception
        assertFailsWith<IllegalArgumentException> {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 4..3
            )
        }
        
        // Testa att värden utanför 2-6 kastar exception
        assertFailsWith<IllegalArgumentException> {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 1..4
            )
        }
        
        assertFailsWith<IllegalArgumentException> {
            tidplan(
                arsslut = LocalDate.of(2025, 12, 31),
                stammodatum = LocalDate.of(2026, 5, 15),
                kallelsefristVeckor = 4..7
            )
        }
    }
}
