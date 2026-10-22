package se.tmconnect.husloggen.regler

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

class LoggFilterTest {
    private val d = LocalDate.of(2026, 1, 1)
    private val poster = listOf(
        Post(id = 1, datum = d, vad = "Service värmepump", typ = PostTyp.REPARATION, utforare = "Kyl & Värme AB"),
        Post(id = 2, datum = d, vad = "Målning fasad söder", typ = PostTyp.UNDERHALL, anteckning = "Två lager"),
        Post(id = 3, datum = d, vad = "Ny altan", typ = PostTyp.OMBYGGNAD, utforare = "Byggfirma"),
    )

    private fun ids(sok: String, typ: PostTyp?) = filtreraPoster(poster, sok, typ).map { it.id }

    @Test
    fun exempel() {
        assertEquals(listOf(1L), ids("värmepump", null))
        assertEquals(listOf(1L), ids("VÄRME kyl", null))
        assertEquals(listOf(3L), ids("byggfirma", PostTyp.OMBYGGNAD))
        assertEquals(listOf(2L), ids("", PostTyp.UNDERHALL))
        assertEquals(listOf(2L), ids("lager fasad", null))
        assertEquals(emptyList<Long>(), ids("altan", PostTyp.REPARATION))
        assertEquals(listOf(1L, 2L, 3L), ids("  ", null))
    }
}
