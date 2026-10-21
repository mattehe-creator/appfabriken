package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FalskTolkareTest {
    @Test
    fun senasteUtdragSparas() {
        val tolkare = FalskTolkare()
        tolkare.foreslaKomponenter("maskad text", listOf(1), "plan.pdf")
        assertEquals("maskad text", tolkare.senasteUtdrag)
    }

    @Test
    fun standardJsonFungerar() {
        val resultat = FalskTolkare().foreslaKomponenter("utdrag", emptyList(), "plan.pdf")
        assertTrue(resultat.isSuccess)
        val json = resultat.getOrNull()!!
        assertTrue(json.contains("komponenter"))
        assertTrue(json.contains("Tak"))
    }

    @Test
    fun timeoutGerFailure() {
        val resultat =
            FalskTolkare(lage = SvarLage.TIMEOUT).foreslaKomponenter("utdrag", emptyList(), "plan.pdf")
        assertTrue(resultat.isFailure)
    }

    @Test
    fun ogiltigtJsonSvar() {
        val resultat =
            FalskTolkare(lage = SvarLage.OGILTIGT_JSON).foreslaKomponenter("utdrag", emptyList(), "plan.pdf")
        assertEquals("inte json", resultat.getOrNull())
    }

    @Test
    fun tomtSvar() {
        val resultat =
            FalskTolkare(lage = SvarLage.TOMT).foreslaKomponenter("utdrag", emptyList(), "plan.pdf")
        assertEquals("", resultat.getOrNull())
    }
}
