package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class ForsaljningSammanstallningTest {
    private fun post(datum: LocalDate, typ: PostTyp, beloppOre: Long? = 10_000L) =
        Post(datum = datum, vad = "Test", typ = typ, beloppOre = beloppOre)

    @Test
    fun kanVaraForbattring_exempel() {
        val forsaljningsar = 2026
        assertEquals(
            ForbattringMojlighet.KravBedomning,
            kanVaraForbattring(post(LocalDate.of(2021, 6, 1), PostTyp.REPARATION), forsaljningsar),
        )
        assertEquals(
            ForbattringMojlighet.Nej,
            kanVaraForbattring(post(LocalDate.of(2020, 12, 31), PostTyp.REPARATION), forsaljningsar),
        )
        assertEquals(
            ForbattringMojlighet.Ja,
            kanVaraForbattring(post(LocalDate.of(2015, 1, 1), PostTyp.OMBYGGNAD), forsaljningsar),
        )
    }

    @Test
    fun arStodjerGransbelopp_granser() {
        val ar = 2024
        val under = listOf(post(LocalDate.of(ar, 3, 1), PostTyp.OMBYGGNAD, 499_900L))
        val over = listOf(post(LocalDate.of(ar, 3, 1), PostTyp.OMBYGGNAD, 500_000L))
        assertFalse(arStodjerGransbelopp(under, ar))
        assertTrue(arStodjerGransbelopp(over, ar))
    }

    @Test
    fun summeraPerAr_perTyp() {
        val poster = listOf(
            post(LocalDate.of(2025, 1, 1), PostTyp.UNDERHALL, 100_000L),
            post(LocalDate.of(2025, 6, 1), PostTyp.REPARATION, 50_000L),
            post(LocalDate.of(2025, 7, 1), PostTyp.UNDERHALL, 25_000L),
        )
        val summa = summeraPerAr(poster)
        assertEquals(100_000L + 25_000L, summa[2025]!![PostTyp.UNDERHALL])
        assertEquals(50_000L, summa[2025]!![PostTyp.REPARATION])
    }
}
