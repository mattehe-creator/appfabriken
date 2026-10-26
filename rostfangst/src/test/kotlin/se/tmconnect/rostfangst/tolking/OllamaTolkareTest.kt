package se.tmconnect.rostfangst.tolking

import java.io.IOException
import java.net.SocketTimeoutException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.rostfangst.regler.ForslagTyp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class OllamaTolkareTest {
    private val godtSvar = HttpSvar(
        200,
        """{"model":"m","message":{"role":"assistant","content":"{\"typ\":\"PAMINNELSE\",\"titel\":\"Ring tandläkaren\"}"},"done":true}""",
    )

    private fun orsak(r: Result<*>): String? = (r.exceptionOrNull() as? TolkningMisslyckades)?.orsak

    @Test
    fun giltigtSvarGerForslag() {
        val r = OllamaTolkare("https://dator.example.ts.net", "qwen3:8b", null) { _, _, _ -> godtSvar }
            .tolka("påminn mig", "system")
        assertTrue(r.isSuccess)
        assertEquals(ForslagTyp.PAMINNELSE, r.getOrThrow().typ)
        assertEquals("Ring tandläkaren", r.getOrThrow().titel)
    }

    @Test
    fun adressenFarEnChatSökvägUtanDubbelSnedstreck() {
        var url = ""
        OllamaTolkare("https://dator.example.ts.net/", "m", null) { u, _, _ -> url = u; godtSvar }.tolka("x", "s")
        assertEquals("https://dator.example.ts.net/api/chat", url)
    }

    @Test
    fun bearerTokenSkickasBaraNärDenFinns() {
        var med: Map<String, String> = emptyMap()
        OllamaTolkare("https://a", "m", "hemlig") { _, h, _ -> med = h; godtSvar }.tolka("x", "s")
        assertEquals("Bearer hemlig", med["Authorization"])
        var utan: Map<String, String> = emptyMap()
        OllamaTolkare("https://a", "m", " ") { _, h, _ -> utan = h; godtSvar }.tolka("x", "s")
        assertTrue(!utan.containsKey("Authorization"))
    }

    @Test
    fun felGerRättOrsak() {
        assertEquals("saknar_adress", orsak(OllamaTolkare("", "m") { _, _, _ -> godtSvar }.tolka("x", "s")))
        assertEquals("saknar_modell", orsak(OllamaTolkare("https://a", " ") { _, _, _ -> godtSvar }.tolka("x", "s")))
        assertEquals("timeout", orsak(OllamaTolkare("https://a", "m") { _, _, _ -> throw SocketTimeoutException() }.tolka("x", "s")))
        assertEquals("natfel", orsak(OllamaTolkare("https://a", "m") { _, _, _ -> throw IOException() }.tolka("x", "s")))
        assertEquals("http_500", orsak(OllamaTolkare("https://a", "m") { _, _, _ -> HttpSvar(500, "") }.tolka("x", "s")))
        assertEquals("saknar_svar", orsak(OllamaTolkare("https://a", "m") { _, _, _ -> HttpSvar(200, "{\"message\":{}}") }.tolka("x", "s")))
    }

    @Test
    fun ogiltigtInnehallBlirAnteckningMedOriginaltexten() {
        val svar = HttpSvar(200, """{"message":{"role":"assistant","content":"inte json"}}""")
        val r = OllamaTolkare("https://a", "m") { _, _, _ -> svar }.tolka("köp mjölk", "s")
        assertEquals(ForslagTyp.ANTECKNING, r.getOrThrow().typ)
        assertEquals("köp mjölk", r.getOrThrow().titel)
    }
}
