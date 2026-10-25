package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

class BostadFilterTest {
    @Test
    fun filtrerarPaValdBostad() {
        val poster =
            listOf(
                Post(bostadId = 1, id = 1, datum = LocalDate.of(2026, 1, 1), vad = "A", typ = PostTyp.UNDERHALL),
                Post(bostadId = 2, id = 2, datum = LocalDate.of(2026, 1, 2), vad = "B", typ = PostTyp.UNDERHALL),
            )
        val filtrerat = filtreraPaBostad(poster, bostadId = 1) { it.bostadId }
        assertEquals(1, filtrerat.size)
        assertEquals("A", filtrerat.single().vad)
    }

    @Test
    fun nullBostadGerTomLista() {
        val poster =
            listOf(
                Post(bostadId = 1, datum = LocalDate.of(2026, 1, 1), vad = "A", typ = PostTyp.UNDERHALL),
            )
        assertEquals(emptyList<Post>(), filtreraPaBostad(poster, bostadId = null) { it.bostadId })
    }
}
