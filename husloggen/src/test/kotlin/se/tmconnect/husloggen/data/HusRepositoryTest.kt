package se.tmconnect.husloggen.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.regler.GRATIS_MAX_POSTER

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusRepositoryTest {
    private lateinit var context: Context
    private lateinit var databas: HusDatabas
    private lateinit var repository: HusRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        nollstallFileProviderCache()
        databas = Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
            .allowMainThreadQueries()
            .build()
        repository = HusRepository(databas.husDao(), skapaHusBildLager(context))
    }

    private fun nollstallFileProviderCache() {
        val falt = androidx.core.content.FileProvider::class.java.getDeclaredField("sCache")
        falt.isAccessible = true
        (falt.get(null) as MutableMap<*, *>).clear()
    }

    private fun skapaTestBildUri(bredd: Int = 100, hojd: Int = 80): Uri {
        val bitmap = Bitmap.createBitmap(bredd, hojd, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.GRAY)
        val fil = File(context.cacheDir, "repo-test-${System.nanoTime()}.jpg")
        FileOutputStream(fil).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        return Uri.fromFile(fil)
    }

    @After
    fun tearDown() {
        databas.close()
    }

    @Test
    fun markeraSomGjordUppdaterarUppgiftOchSkaparPost() = runTest {
        val idag = LocalDate.of(2026, 10, 16)
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 1, 1),
            ),
        )
        val uppgift = repository.hamtaUppgift(uppgiftId)!!

        repository.markeraSomGjord(uppgift, idag, arPro = false)

        val uppdaterad = repository.hamtaUppgift(uppgiftId)!!
        assertEquals(idag, uppdaterad.senastGjord)

        val poster = repository.poster.first()
        assertEquals(1, poster.size)
        assertEquals("Rensa hängrännor", poster[0].vad)
        assertEquals(PostTyp.UNDERHALL, poster[0].typ)
        assertEquals(idag, poster[0].datum)
        assertEquals(uppgiftId, poster[0].uppgiftId)
        assertNull(poster[0].beloppOre)
    }

    @Test
    fun markeraSomGjordUtanTidigareSenastGjord() = runTest {
        val idag = LocalDate.of(2026, 6, 1)
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Brandvarnare",
                intervallManader = 12,
                startDatum = LocalDate.of(2025, 6, 1),
                senastGjord = null,
            ),
        )
        repository.markeraSomGjord(repository.hamtaUppgift(uppgiftId)!!, idag, arPro = false)
        assertEquals(idag, repository.hamtaUppgift(uppgiftId)!!.senastGjord)
        assertEquals(1, repository.poster.first().size)
    }

    @Test
    fun sparaAndraOchTaBortPost() = runTest {
        val idag = LocalDate.of(2026, 10, 16)
        val postId = when (
            val result = repository.sparaPost(
                Post(
                    datum = idag,
                    vad = "Målade fasaden",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 12_500L,
                ),
                arPro = false,
            )
        ) {
            is SparaPostResult.Sparad -> result.id
            SparaPostResult.GransNadd -> error("oväntat GransNadd")
        }
        val sparad = repository.hamtaPost(postId)!!
        assertEquals("Målade fasaden", sparad.vad)
        assertEquals(12_500L, sparad.beloppOre)

        repository.sparaPost(
            sparad.copy(vad = "Ommålning", beloppOre = 15_000L),
            arPro = false,
        )
        val uppdaterad = repository.hamtaPost(postId)!!
        assertEquals("Ommålning", uppdaterad.vad)
        assertEquals(15_000L, uppdaterad.beloppOre)

        repository.taBortPost(uppdaterad)
        assertNull(repository.hamtaPost(postId))
        assertEquals(0, repository.poster.first().size)
    }

    @Test
    fun postKoppladTillUppgiftBehallerKopplingVidAndring() = runTest {
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Rensa hängrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 1, 1),
            ),
        )
        val postId = (repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 5, 1),
                vad = "Rensning",
                typ = PostTyp.UNDERHALL,
                uppgiftId = uppgiftId,
            ),
            arPro = false,
        ) as SparaPostResult.Sparad).id
        val post = repository.hamtaPost(postId)!!
        repository.sparaPost(post.copy(vad = "Rensning klart"), arPro = false)
        assertEquals(uppgiftId, repository.hamtaPost(postId)!!.uppgiftId)
    }

    @Test
    fun postUtanBeloppSparasMedNull() = runTest {
        val postId = (repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 4, 1),
                vad = "Kontroll",
                typ = PostTyp.UNDERHALL,
                beloppOre = null,
            ),
            arPro = false,
        ) as SparaPostResult.Sparad).id
        assertNull(repository.hamtaPost(postId)!!.beloppOre)
    }

    @Test
    fun postUtanBilderGerTomLista() = runTest {
        val postId = (repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 1, 1),
                vad = "Utan bild",
                typ = PostTyp.UNDERHALL,
            ),
            arPro = false,
        ) as SparaPostResult.Sparad).id
        val post = repository.hamtaPost(postId)!!
        assertTrue(repository.lasBilder(post).isEmpty())
    }

    @Test
    fun laggTillOchTaBortBildPaPost() = runTest {
        val postId = (repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 2, 1),
                vad = "Med kvitto",
                typ = PostTyp.REPARATION,
            ),
            arPro = false,
        ) as SparaPostResult.Sparad).id
        val uri = skapaTestBildUri()
        assertTrue(repository.laggTillBild(postId, uri))

        val medBild = repository.hamtaPost(postId)!!
        val filnamn = repository.lasBilder(medBild).single()
        assertTrue(repository.bildfilFinns(filnamn))

        repository.taBortBild(postId, filnamn)
        assertTrue(repository.lasBilder(repository.hamtaPost(postId)!!).isEmpty())
        assertTrue(!repository.bildfilFinns(filnamn))
    }

    @Test
    fun taBortPostRaderarBildfiler() = runTest {
        val postId = (repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 3, 1),
                vad = "Raderas",
                typ = PostTyp.UNDERHALL,
            ),
            arPro = false,
        ) as SparaPostResult.Sparad).id
        assertTrue(repository.laggTillBild(postId, skapaTestBildUri()))
        val post = repository.hamtaPost(postId)!!
        val filnamn = repository.lasBilder(post).single()

        repository.taBortPost(post)
        assertNull(repository.hamtaPost(postId))
        assertTrue(!repository.bildfilFinns(filnamn))
    }

    @Test
    fun taBortUppgiftBehallerPostMedNullUppgiftId() = runTest {
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Service",
                intervallManader = 24,
                startDatum = LocalDate.of(2026, 1, 1),
            ),
        )
        val uppgift = repository.hamtaUppgift(uppgiftId)!!
        repository.markeraSomGjord(uppgift, LocalDate.of(2026, 3, 1), arPro = false)
        val postId = repository.poster.first().first().id

        repository.taBortUppgift(uppgift)

        assertNull(repository.hamtaUppgift(uppgiftId))
        val post = databas.husDao().hamtaPost(postId)!!
        assertNull(post.uppgiftId)
        assertEquals(1, repository.antalPoster())
    }

    @Test
    fun nyPostVidGransReturnerarGransNadd() = runTest {
        repeat(GRATIS_MAX_POSTER) { i ->
            repository.sparaPost(
                Post(
                    datum = LocalDate.of(2026, 1, 1).plusDays(i.toLong()),
                    vad = "Post $i",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            )
        }
        assertEquals(GRATIS_MAX_POSTER, repository.antalPoster())

        val result = repository.sparaPost(
            Post(
                datum = LocalDate.of(2026, 2, 1),
                vad = "För mycket",
                typ = PostTyp.UNDERHALL,
            ),
            arPro = false,
        )
        assertEquals(SparaPostResult.GransNadd, result)
        assertEquals(GRATIS_MAX_POSTER, repository.antalPoster())
    }

    @Test
    fun andraBefintligPostOverGransenFungerar() = runTest {
        repeat(GRATIS_MAX_POSTER) { i ->
            repository.sparaPost(
                Post(
                    datum = LocalDate.of(2026, 1, 1).plusDays(i.toLong()),
                    vad = "Post $i",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            )
        }
        val forsta = repository.poster.first().first()
        val result = repository.sparaPost(forsta.copy(vad = "Ändrad"), arPro = false)
        assertTrue(result is SparaPostResult.Sparad)
        assertEquals("Ändrad", repository.hamtaPost(forsta.id)!!.vad)
    }

    @Test
    fun markeraSomGjordVidGransFlyttarUppgiftUtanNyPost() = runTest {
        repeat(GRATIS_MAX_POSTER) { i ->
            repository.sparaPost(
                Post(
                    datum = LocalDate.of(2026, 1, 1).plusDays(i.toLong()),
                    vad = "Post $i",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            )
        }
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Service",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 1, 1),
            ),
        )
        val idag = LocalDate.of(2026, 10, 16)
        val result = repository.markeraSomGjord(
            repository.hamtaUppgift(uppgiftId)!!,
            idag,
            arPro = false,
        )
        assertEquals(MarkeraGjordResult.UtanPostGransNadd, result)
        assertEquals(idag, repository.hamtaUppgift(uppgiftId)!!.senastGjord)
        assertEquals(GRATIS_MAX_POSTER, repository.antalPoster())
    }

    @Test
    fun markeraSomGjordMedProSkaparPostVidGrans() = runTest {
        repeat(GRATIS_MAX_POSTER) { i ->
            repository.sparaPost(
                Post(
                    datum = LocalDate.of(2026, 1, 1).plusDays(i.toLong()),
                    vad = "Post $i",
                    typ = PostTyp.UNDERHALL,
                ),
                arPro = false,
            )
        }
        val uppgiftId = repository.sparaUppgift(
            Uppgift(
                namn = "Service",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 1, 1),
            ),
        )
        val idag = LocalDate.of(2026, 10, 16)
        val result = repository.markeraSomGjord(
            repository.hamtaUppgift(uppgiftId)!!,
            idag,
            arPro = true,
        )
        assertEquals(MarkeraGjordResult.MedPost, result)
        assertEquals(GRATIS_MAX_POSTER + 1, repository.antalPoster())
    }
}
