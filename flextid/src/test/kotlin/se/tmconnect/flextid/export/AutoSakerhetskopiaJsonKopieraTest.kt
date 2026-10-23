package se.tmconnect.flextid.export

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.karna.sakerhetskopia.MappLagring
import se.tmconnect.karna.sakerhetskopia.MappPost
import se.tmconnect.karna.sakerhetskopia.kopiera
import se.tmconnect.karna.sakerhetskopia.postAttRadera

class AutoSakerhetskopiaJsonKopieraTest {
    private class MinnesMapp(
        val poster: MutableList<Pair<MappPost, ByteArrayOutputStream>> = mutableListOf(),
        var nekaSkapa: Boolean = false,
        private var nr: Int = 0,
    ) : MappLagring {
        override fun lista(): List<MappPost> = poster.map { it.first }

        override fun skapa(namn: String): java.io.OutputStream? {
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

    private val dag = LocalDate.of(2026, 10, 23)
    private val exempelJson =
        skrivSakerhetskopiaJson(
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-23T08:00:00Z",
                installning =
                    SakerhetskopiaInstallning(
                        normaltidMinPerDag = 480,
                        standardRastMin = 30,
                        ingangssaldoMin = 0,
                        arbetsdagarMask = 31,
                        stamplaInPaminnelseMinEfterMidnatt = 480,
                    ),
                dagar = emptyList(),
            ),
        )

    @Test
    fun gallringFoljerJsonPrefix() {
        val poster =
            (1..6).map { MappPost("id$it", "${FLEXTID_AUTO_FILPREFIX}2026-10-0$it.json", 0L) } +
                MappPost("ny", "${FLEXTID_AUTO_FILPREFIX}2026-10-23 (1).json", 0L)
        val bort = postAttRadera(poster, FLEXTID_AUTO_FILPREFIX, filAndelse = FLEXTID_AUTO_FILANDELSE).map { it.id }
        assertEquals(listOf("id2", "id1"), bort)
    }

    @Test
    fun kopieraSkriverJsonOchGallrar() {
        val mapp = MinnesMapp()
        (1..6).forEach { mapp.laggTill("${FLEXTID_AUTO_FILPREFIX}2026-01-0$it.json", it * 10L) }
        assertTrue(
            kopiera(
                mapp,
                FLEXTID_AUTO_FILPREFIX,
                { it.write(exempelJson.toByteArray()) },
                dag,
                FLEXTID_AUTO_FILANDELSE,
            ),
        )
        assertTrue(mapp.lista().any { it.namn == "flextid-auto-2026-10-23.json" })
        assertFalse(mapp.lista().any { it.namn == "${FLEXTID_AUTO_FILPREFIX}2026-01-01.json" })
        val innehall = mapp.poster.single { it.first.namn == "flextid-auto-2026-10-23.json" }.second
        assertTrue(lasSakerhetskopiaJson(innehall.toString(Charsets.UTF_8.name())) is SakerhetskopiaJsonLasResult.Ok)
    }

    @Test
    fun misslyckadSkrivningRorInteGamla() {
        val mapp = MinnesMapp()
        mapp.laggTill("${FLEXTID_AUTO_FILPREFIX}2026-10-01.json", 5L)
        val ok = kopiera(mapp, FLEXTID_AUTO_FILPREFIX, { throw IOException("full") }, dag, FLEXTID_AUTO_FILANDELSE)
        assertFalse(ok)
        assertEquals(listOf("${FLEXTID_AUTO_FILPREFIX}2026-10-01.json"), mapp.lista().map { it.namn })
    }
}
