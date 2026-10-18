package se.tmconnect.rostfangst.tolking

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.rostfangst.regler.ForslagTyp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AnthropicTolkareTest {
    @Test
    fun tolkaReturnerarForslagFranFakeHttp() {
        val transport = HttpTransport { _, _, _ ->
            HttpSvar(
                200,
                """
                {"content":[{"type":"text","text":"{\"typ\":\"PAMINNELSE\",\"titel\":\"Ring tandläkaren\",\"start\":\"2026-10-20T07:00:00Z\",\"slut\":null,\"plats\":null,\"svar\":null}"}]}
                """.trimIndent(),
            )
        }
        val tolkare = AnthropicTolkare("test-nyckel", "claude-haiku-5-5", transport)
        val result = tolkare.tolka("påminn mig imorgon", "system")
        assertTrue(result.isSuccess)
        val forslag = result.getOrThrow()
        assertEquals(ForslagTyp.PAMINNELSE, forslag.typ)
        assertEquals("Ring tandläkaren", forslag.titel)
    }

    @Test
    fun saknarApiNyckelGerFel() {
        val tolkare = AnthropicTolkare("", "claude-haiku-5-5") { _, _, _ ->
            HttpSvar(200, "{}")
        }
        assertTrue(tolkare.tolka("hej", "system").isFailure)
    }
}
