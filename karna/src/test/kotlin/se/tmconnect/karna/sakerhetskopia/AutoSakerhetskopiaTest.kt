package se.tmconnect.karna.sakerhetskopia

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoSakerhetskopiaTest {
    private val filPrefix = "testapp-auto-"

    private class MinnesMapp(
        val poster: MutableList<Pair<MappPost, ByteArrayOutputStream>> = mutableListOf(),
        var nekaSkapa: Boolean = false,
        private var nr: Int = 0,
    ) : MappLagring {
        override fun lista(): List<MappPost> = poster.map { it.first }

        override fun skapa(namn: String): OutputStream? {
            if (nekaSkapa) return null
            val ut = ByteArrayOutputStream()
            poster += MappPost("id${nr++}", namn, 1_000_000L + nr) to ut
            return ut
        }

        override fun radera(post: MappPost): Boolean = poster.removeAll { it.first.id == post.id }

        fun laggTill(namn: String, andrad: Long) {
            poster += MappPost("id${nr++}", namn, andrad) to ByteArrayOutputStream()
        }
    }

    private val dag = LocalDate.of(2026, 10, 13)
    private val dagMs = TimeUnit.DAYS.toMillis(1)

    @Test
    fun dagsForNyKopia() {
        assertTrue(arSakerhetskopiaDags(null, 5 * dagMs, 7))
        assertFalse(arSakerhetskopiaDags(10 * dagMs, 16 * dagMs, 7))
        assertTrue(arSakerhetskopiaDags(10 * dagMs, 17 * dagMs, 7))
    }

    @Test
    fun filnamnMedPrefix() {
        assertEquals("testapp-auto-2026-10-13.zip", autoFilnamn(dag, filPrefix))
    }

    @Test
    fun gallringFoljerPrefix() {
        val poster =
            (1..6).map { MappPost("id$it", "${filPrefix}2026-10-0$it.zip", 0L) } +
                MappPost("ny", "${filPrefix}2026-10-13 (1).zip", 0L)
        val bort = postAttRadera(poster, filPrefix).map { it.id }
        assertEquals(listOf("id2", "id1"), bort)
    }

    @Test
    fun kopieraSkriverOchGallrar() {
        val mapp = MinnesMapp()
        (1..6).forEach { mapp.laggTill("${filPrefix}2026-01-0$it.zip", it * 10L) }
        assertTrue(kopiera(mapp, filPrefix, { it.write("zip".toByteArray()) }, dag))
        assertTrue(mapp.lista().any { it.namn == "testapp-auto-2026-10-13.zip" })
        assertFalse(mapp.lista().any { it.namn == "${filPrefix}2026-01-01.zip" })
    }

    @Test
    fun misslyckadSkrivningRorInteGamla() {
        val mapp = MinnesMapp()
        mapp.laggTill("${filPrefix}2026-10-01.zip", 5L)
        val ok = kopiera(mapp, filPrefix, { throw IOException("full") }, dag)
        assertFalse(ok)
        assertEquals(listOf("${filPrefix}2026-10-01.zip"), mapp.lista().map { it.namn })
    }
}
