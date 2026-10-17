package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Avlasning

class AvlasningValideringTest {
    @Test
    fun sammaDatumAvvisas() {
        val befintliga =
            listOf(
                Avlasning(id = 1, matareId = 1, datum = LocalDate.of(2026, 1, 1), varde = 1000),
            )
        assertEquals(
            AvlasningFel.DatumRedanAnvant,
            valideraAvlasning("1000", LocalDate.of(2026, 1, 1), befintliga),
        )
    }

    @Test
    fun lagreVardeBehoverBekraftelse() {
        assertTrue(behoverBekraftelseLagreAnForegaende(900_000, 1_250_500))
    }

    @Test
    fun hogreVardeBehoverInteBekraftelse() {
        assertTrue(!behoverBekraftelseLagreAnForegaende(1_300_000, 1_250_500))
    }
}
