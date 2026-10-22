package se.tmconnect.styrelserummet.server

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RantorApiTest {
    @Test
    fun standardsteg() {
        val svar = beraknaRantorSvar(RantorRequest(38_000_000, 4_200_000, 5_200))
        assertEquals(4, svar.rader.size)
        assertEquals("+1,0 procentenhet", svar.rader[1].hojning)
        assertEquals("+9,0 %", svar.rader[1].avgiftshojning)
        assertEquals("6 kr/kvm/mån", svar.rader[1].perKvmManad)
    }

    @Test
    fun kontroller() {
        assertThrows(VerktygFel::class.java) { beraknaRantorSvar(RantorRequest(-1, 4_200_000)) }
        assertThrows(VerktygFel::class.java) { beraknaRantorSvar(RantorRequest(1, 1, steg = listOf(0))) }
        assertThrows(VerktygFel::class.java) { beraknaRantorSvar(RantorRequest(1, 1, steg = emptyList())) }
    }

    @Test
    fun rutter() = testApplication {
        application {
            module(ServerMiljo(dataRot = createTempDirectory("rantor"), port = 0, tolkare = FalskTolkare(), synkronJobb = true))
        }
        val sida = client.get("/rantor")
        assertEquals(HttpStatusCode.OK, sida.status)
        assertTrue(sida.bodyAsText().contains("""rel="canonical" href="/rantor""""))
        val res = client.post("/api/rantor") {
            contentType(ContentType.Application.Json)
            setBody("""{"skulder":38000000,"arsavgifter":4200000,"kvmBostadsratt":5200}""")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        assertTrue(res.bodyAsText().contains("procentenhet"))
    }
}
