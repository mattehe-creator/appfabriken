package se.tmconnect.husloggen.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class LoggFilterTest {
    private val poster = listOf(
        Post(
            id = 1,
            bostadId = 1L,
            datum = LocalDate.now(),
            vad = "Service värmepump",
            typ = PostTyp.REPARATION,
            utforare = "Kyl & Värme AB",
        ),
        Post(
            id = 2,
            bostadId = 1L,
            datum = LocalDate.now(),
            vad = "Målning fasad söder",
            typ = PostTyp.UNDERHALL,
            anteckning = "Två lager",
        ),
        Post(
            id = 3,
            bostadId = 1L,
            datum = LocalDate.now(),
            vad = "Ny altan",
            typ = PostTyp.OMBYGGNAD,
            utforare = "Byggfirma",
        ),
    )

    @Test
    fun filtreraPoster_exempel() {
        assertEquals(listOf(1L), filtreraPoster(poster, "värmepump", null).map { it.id })
        assertEquals(listOf(1L), filtreraPoster(poster, "VÄRME kyl", null).map { it.id })
        assertEquals(listOf(3L), filtreraPoster(poster, "byggfirma", PostTyp.OMBYGGNAD).map { it.id })
        assertEquals(listOf(2L), filtreraPoster(poster, "", PostTyp.UNDERHALL).map { it.id })
        assertEquals(listOf(2L), filtreraPoster(poster, "lager fasad", null).map { it.id })
        assertTrue(filtreraPoster(poster, "altan", PostTyp.REPARATION).isEmpty())
    }
}
