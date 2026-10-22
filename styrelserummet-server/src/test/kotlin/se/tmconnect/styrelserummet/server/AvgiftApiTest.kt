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

class AvgiftApiTest {
    @Test
    fun exempel() {
        val svar = beraknaAvgiftSvar(AvgiftRequest(3_100_000, 1_520_000, 600_000, 250_000, 4_200_000, 5_200))
        assertEquals(se.tmconnect.styrelserummet.regler.kronor(4_970_000), svar.behov)
        assertEquals("80 kr/kvm/mån", svar.behovPerKvmManad)
        assertEquals("+18,3 %", svar.forandring)
        assertThrows(VerktygFel::class.java) { beraknaAvgiftSvar(AvgiftRequest(driftkostnader = -1)) }
    }

    @Test
    fun rutter() = testApplication {
        application {
            module(ServerMiljo(dataRot = createTempDirectory("avgift"), port = 0, tolkare = FalskTolkare(), synkronJobb = true))
        }
        assertEquals(HttpStatusCode.OK, client.get("/avgift").status)
        val res = client.post("/api/avgift") {
            contentType(ContentType.Application.Json)
            setBody("""{"driftkostnader":3100000,"kapitalkostnader":1520000,"avsattning":600000,"ovrigaIntakter":250000,"nuvarandeArsavgifter":4200000,"kvmBostadsratt":5200}""")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        assertTrue(res.bodyAsText().contains("kr/kvm/mån"))
    }
}
