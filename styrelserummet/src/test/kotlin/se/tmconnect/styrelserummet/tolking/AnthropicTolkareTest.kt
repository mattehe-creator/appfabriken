package se.tmconnect.styrelserummet.tolking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.underlag.tolkaForslag

class AnthropicTolkareTest {
    @Test
    fun foreslaKomponenterReturnerarJsonFranFakeTransport() {
        val inspelat = """
            {"komponenter":[{"namn":"Fasad","grupp":"FASAD","anskaffningsvarde":4000000,"anskaffningsar":1975,"nyttjandeperiodAr":40,"kalla":"underhallsplan.pdf, sida 2","sakerhet":"HOG"}],"anmarkningar":[]}
        """.trimIndent()
        val transport = HttpTransport { _, _, _ ->
            HttpSvar(
                200,
                """{"content":[{"type":"text","text":${jsonString(inspelat)}}]}""",
            )
        }
        val tolkare = AnthropicTolkare("test-nyckel", "claude-sonnet-5-5", transport)
        val result = tolkare.foreslaKomponenter("Fasad puts 2028", listOf(2), "underhallsplan.pdf")
        assertTrue(result.isSuccess)
        val tolkat = tolkaForslag(result.getOrThrow(), "underhallsplan.pdf")
        assertEquals(1, tolkat.komponenter.size)
        assertEquals("Fasad", tolkat.komponenter[0].namn)
    }

    @Test
    fun saknarApiNyckelGerFel() {
        val tolkare = AnthropicTolkare(
            "",
            "claude-sonnet-5-5",
            HttpTransport { _, _, _ -> HttpSvar(200, "{}") },
        )
        assertTrue(tolkare.foreslaKomponenter("text", listOf(1), "plan.pdf").isFailure)
    }

    private fun jsonString(text: String): String =
        "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
