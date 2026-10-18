package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusDaoTest {
    private lateinit var databas: HusDatabas
    private lateinit var dao: HusDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        databas = skapaTestHusDatabas(context)
        dao = databas.husDao()
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun uppgiftInfogaHamtaUppdateraTaBort() = runTest {
        val id = dao.infogaUppgift(
            Uppgift(
                namn = "Rensa takrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
            ),
        )

        val hamtad = dao.hamtaUppgift(id)
        assertNotNull(hamtad)
        assertEquals("Rensa takrännor", hamtad!!.namn)

        dao.uppdateraUppgift(hamtad.copy(namn = "Kontrollera takrännor"))
        assertEquals("Kontrollera takrännor", dao.hamtaUppgift(id)!!.namn)

        dao.taBortUppgift(hamtad.copy(namn = "Kontrollera takrännor"))
        assertNull(dao.hamtaUppgift(id))
    }

    @Test
    fun postInfogaHamtaUppdateraTaBort() = runTest {
        val id = dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 3, 1),
                vad = "Ommålning fasad",
                typ = PostTyp.UNDERHALL,
                beloppOre = 50_000L,
            ),
        )

        val hamtad = dao.hamtaPost(id)
        assertNotNull(hamtad)
        assertEquals(PostTyp.UNDERHALL, hamtad!!.typ)

        dao.uppdateraPost(hamtad.copy(vad = "Fasadtvätt"))
        assertEquals("Fasadtvätt", dao.hamtaPost(id)!!.vad)

        dao.taBortPost(hamtad.copy(vad = "Fasadtvätt"))
        assertNull(dao.hamtaPost(id))
        assertEquals(0, dao.antalPoster())
    }

    @Test
    fun posterSorterasDatumSenastSkapad() = runTest {
        dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 1, 10),
                vad = "A",
                typ = PostTyp.UNDERHALL,
                skapad = 100L,
            ),
        )
        dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 6, 1),
                vad = "B",
                typ = PostTyp.REPARATION,
                skapad = 200L,
            ),
        )
        dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 6, 1),
                vad = "C",
                typ = PostTyp.REPARATION,
                skapad = 300L,
            ),
        )

        val lista = dao.allaPosterFlow().first()
        assertEquals(listOf("C", "B", "A"), lista.map { it.vad })
    }

    @Test
    fun postOverleverBorttagenUppgift() = runTest {
        val uppgiftId = dao.infogaUppgift(
            Uppgift(
                namn = "Service värmepump",
                intervallManader = 24,
                startDatum = LocalDate.of(2025, 1, 1),
            ),
        )
        val postId = dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 2, 1),
                vad = "Service",
                typ = PostTyp.UNDERHALL,
                uppgiftId = uppgiftId,
            ),
        )

        dao.taBortUppgift(dao.hamtaUppgift(uppgiftId)!!)

        val post = dao.hamtaPost(postId)!!
        assertNull(post.uppgiftId)
        assertEquals(1, dao.antalPoster())
    }

    @Test
    fun antalPoster() = runTest {
        assertEquals(0, dao.antalPoster())
        dao.infogaPost(
            Post(
                datum = LocalDate.of(2026, 1, 1),
                vad = "Test",
                typ = PostTyp.OMBYGGNAD,
            ),
        )
        assertEquals(1, dao.antalPoster())
    }
}
