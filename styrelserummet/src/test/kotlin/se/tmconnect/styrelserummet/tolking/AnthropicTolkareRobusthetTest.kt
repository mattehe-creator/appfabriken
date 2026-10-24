package se.tmconnect.styrelserummet.tolking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnthropicTolkareRobusthetTest {
    private val okKropp = """{"content":[{"type":"text","text":"{\"komponenter\":[]}"}],"stop_reason":"end_turn"}"""

    private fun tolkare(svar: List<() -> HttpSvar>): Pair<AnthropicTolkare, () -> Int> {
        var anrop = 0
        val transport = HttpTransport { _, _, _ -> svar[minOf(anrop++, svar.size - 1)]() }
        return AnthropicTolkare("nyckel", "modell", transport, vantaFore = {}) to { anrop }
    }

    private fun fel(r: Result<String>): String? = r.exceptionOrNull()?.message

    @Test
    fun overbelastadForsokerIgen() {
        val (t, anrop) = tolkare(listOf({ HttpSvar(529, "{}") }, { HttpSvar(429, "{}") }, { HttpSvar(200, okKropp) }))
        val r = t.foreslaKomponenter("text", listOf(1), "plan.pdf")
        assertTrue(r.isSuccess)
        assertEquals(3, anrop())
    }

    @Test
    fun geUppEfterTreForsok() {
        val (t, anrop) = tolkare(listOf({ HttpSvar(529, "{}") }))
        assertEquals("http_529", fel(t.foreslaKomponenter("text", listOf(1), "plan.pdf")))
        assertEquals(3, anrop())
    }

    @Test
    fun klientfelForsokerInteIgen() {
        val (t, anrop) = tolkare(listOf({ HttpSvar(400, "{}") }))
        assertEquals("http_400", fel(t.foreslaKomponenter("text", listOf(1), "plan.pdf")))
        assertEquals(1, anrop())
    }

    @Test
    fun avklipptSvarGerFel() {
        val kropp = """{"content":[{"type":"text","text":"{\"komponenter\":[{\"namn\":"}],"stop_reason":"max_tokens"}"""
        val (t, _) = tolkare(listOf({ HttpSvar(200, kropp) }))
        assertEquals("svaret_for_langt", fel(t.foreslaKomponenter("text", listOf(1), "plan.pdf")))
    }

    @Test
    fun natverksfelBlirResultat() {
        val (t, anrop) = tolkare(listOf({ throw java.io.IOException("nere") }))
        assertEquals("natverk", fel(t.foreslaKomponenter("text", listOf(1), "plan.pdf")))
        assertEquals(3, anrop())
    }

    @Test
    fun tidsgransForsokerInteIgen() {
        val (t, anrop) = tolkare(listOf({ throw java.net.http.HttpTimeoutException("sent") }))
        assertEquals("tidsgrans", fel(t.foreslaKomponenter("text", listOf(1), "plan.pdf")))
        assertEquals(1, anrop())
    }

    @Test
    fun flerTextblockSlasIhop() {
        val kropp = """{"content":[{"type":"text","text":"{\"komponenter\":"},{"type":"text","text":"[]}"}],"stop_reason":"end_turn"}"""
        val (t, _) = tolkare(listOf({ HttpSvar(200, kropp) }))
        assertEquals("{\"komponenter\":[]}", t.foreslaKomponenter("text", listOf(1), "plan.pdf").getOrThrow())
    }
}
