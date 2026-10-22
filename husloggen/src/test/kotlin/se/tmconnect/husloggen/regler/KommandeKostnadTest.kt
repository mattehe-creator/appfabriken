package se.tmconnect.husloggen.regler

import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class KommandeKostnadTest {

    @Test
    fun `uppskattadKostnadKommande summerar senaste kostnad per jobb enligt exempel`() {
        val senasteKostnad = mapOf(7L to 120_000L, 8L to 50_000L)
        val kommande =
            mapOf(
                YearMonth.of(2026, 11) to
                    listOf(
                        KommandeJobb(7L, "A", LocalDate.of(2026, 11, 1), false),
                        KommandeJobb(9L, "B", LocalDate.of(2026, 11, 15), false),
                    ),
                YearMonth.of(2027, 5) to
                    listOf(
                        KommandeJobb(7L, "A", LocalDate.of(2027, 5, 1), false),
                        KommandeJobb(8L, "C", LocalDate.of(2027, 5, 10), false),
                    ),
            )

        val result = uppskattadKostnadKommande(kommande, senasteKostnad)

        assertEquals(KommandeKostnad(summaOre = 290_000L, antalMedKostnad = 3, antalUtanKostnad = 1), result)
    }

    @Test
    fun `uppskattadKostnadKommande tom kommande map ger nollor`() {
        val result = uppskattadKostnadKommande(emptyMap(), mapOf(7L to 120_000L))

        assertEquals(KommandeKostnad(summaOre = 0L, antalMedKostnad = 0, antalUtanKostnad = 0), result)
    }
}
