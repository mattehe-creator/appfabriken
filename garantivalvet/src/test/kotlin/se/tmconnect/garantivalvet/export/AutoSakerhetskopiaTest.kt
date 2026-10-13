package se.tmconnect.garantivalvet.export

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
    /** Mapp i minnet. */
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
    fun dagsForNyKopiaNarIngenGjortsEllerIntervalletGatt() {
        assertTrue(arSakerhetskopiaDags(null, 5 * dagMs, 7))
        assertFalse(arSakerhetskopiaDags(10 * dagMs, 16 * dagMs, 7))
        assertTrue(arSakerhetskopiaDags(10 * dagMs, 17 * dagMs, 7))
        assertTrue(arSakerhetskopiaDags(10 * dagMs, 11 * dagMs, 1))
        assertFalse(arSakerhetskopiaDags(10 * dagMs, 10 * dagMs + 1, 1))
        assertTrue(arSakerhetskopiaDags(0L, 30 * dagMs, 30))
    }

    @Test
    fun ettOgiltigtIntervallBehandlasSomEttDygn() {
        assertTrue(arSakerhetskopiaDags(0L, dagMs, 0))
    }

    @Test
    fun filnamnetHarPrefixOchDatum() {
        assertEquals("garantivalvet-auto-2026-10-13.zip", autoFilnamn(dag))
    }

    @Test
    fun deFemSenasteBehallsOchAnvandarensEgnaFilerRorsInte() {
        val poster = (1..8).map { MappPost("id$it", "${AUTO_FILPREFIX}x$it.zip", it * 100L) } +
            MappPost("egen", "min-egen-fil.zip", 1L) +
            MappPost("manuell", "garantivalvet-sakerhetskopia-2026-01-01.zip", 1L)
        val bort = postAttRadera(poster).map { it.id }
        assertEquals(listOf("id3", "id2", "id1"), bort)
    }

    @Test
    fun faKopiorGerIngenGallring() {
        val poster = (1..3).map { MappPost("id$it", "${AUTO_FILPREFIX}x$it.zip", it * 10L) }
        assertTrue(postAttRadera(poster).isEmpty())
    }

    @Test
    fun enKopiaSkrivsOchInnehalletHamnarIFilen() {
        val mapp = MinnesMapp()
        val ok = kopiera(mapp, { it.write("zipdata".toByteArray()) }, dag)
        assertTrue(ok)
        assertEquals(1, mapp.poster.size)
        assertEquals("garantivalvet-auto-2026-10-13.zip", mapp.poster[0].first.namn)
        assertEquals("zipdata", mapp.poster[0].second.toString())
    }

    @Test
    fun enKopiaFranSammaDagErsatterDenGamla() {
        val mapp = MinnesMapp()
        kopiera(mapp, { it.write("forsta".toByteArray()) }, dag)
        kopiera(mapp, { it.write("andra".toByteArray()) }, dag)
        assertEquals(1, mapp.poster.size)
        assertEquals("andra", mapp.poster[0].second.toString())
    }

    @Test
    fun gamlaKopiorGallrasMenInteAnvandarensFiler() {
        val mapp = MinnesMapp()
        (1..6).forEach { mapp.laggTill("${AUTO_FILPREFIX}2026-01-0$it.zip", it * 10L) }
        mapp.laggTill("min-egen-fil.zip", 1L)
        assertTrue(kopiera(mapp, { it.write(1) }, dag))
        val namn = mapp.lista().map { it.namn }
        assertEquals(5 + 1, namn.size)
        assertTrue("min-egen-fil.zip" in namn)
        assertTrue("garantivalvet-auto-2026-10-13.zip" in namn)
        assertFalse("${AUTO_FILPREFIX}2026-01-01.zip" in namn)
    }

    @Test
    fun gaterInteAttSkapaFilenGerFalse() {
        assertFalse(kopiera(MinnesMapp(nekaSkapa = true), { it.write(1) }, dag))
    }

    @Test
    fun misslyckadSkrivningTarBortDenHalvfardigaFilenOchRorInteGamla() {
        val mapp = MinnesMapp()
        mapp.laggTill("garantivalvet-auto-2026-10-01.zip", 5L)
        val ok = kopiera(mapp, { throw IOException("disken full") }, dag)
        assertFalse(ok)
        assertEquals(listOf("garantivalvet-auto-2026-10-01.zip"), mapp.lista().map { it.namn })
    }
}
