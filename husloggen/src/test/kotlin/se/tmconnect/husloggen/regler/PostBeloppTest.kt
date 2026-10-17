package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class PostBeloppTest {
    @Test
    fun nettoBeloppMedRotRutAvdrag() {
        val post =
            Post(
                datum = LocalDate.of(2025, 1, 1),
                vad = "Renovering",
                typ = PostTyp.OMBYGGNAD,
                beloppOre = 1_000_000L,
                rotRutAvdragOre = 300_000L,
            )
        assertEquals(700_000L, postBeloppForSammanstallningOre(post))
    }

    @Test
    fun bruttoBeloppUtanAvdrag() {
        val post =
            Post(
                datum = LocalDate.of(2025, 1, 1),
                vad = "Renovering",
                typ = PostTyp.REPARATION,
                beloppOre = 1_000_000L,
            )
        assertEquals(1_000_000L, postBeloppForSammanstallningOre(post))
    }

    @Test
    fun utanBeloppGerNull() {
        val post =
            Post(
                datum = LocalDate.of(2025, 1, 1),
                vad = "Anteckning",
                typ = PostTyp.UNDERHALL,
            )
        assertNull(postBeloppForSammanstallningOre(post))
    }

    @Test
    fun summeraPerArAnvanderNetto() {
        val medAvdrag =
            Post(
                datum = LocalDate.of(2025, 6, 1),
                vad = "A",
                typ = PostTyp.UNDERHALL,
                beloppOre = 1_000_000L,
                rotRutAvdragOre = 300_000L,
            )
        val utanAvdrag =
            Post(
                datum = LocalDate.of(2025, 7, 1),
                vad = "B",
                typ = PostTyp.UNDERHALL,
                beloppOre = 200_000L,
            )
        val summa = summeraPerAr(listOf(medAvdrag, utanAvdrag))
        assertEquals(700_000L + 200_000L, summa[2025]!![PostTyp.UNDERHALL])
    }
}
