package se.tmconnect.styrelserummet.server

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProtokollSidaTest {
    @Test
    fun sidanFinnsOchSkickarInget() = testApplication {
        application {
            module(ServerMiljo(dataRot = createTempDirectory("protokoll"), port = 0, tolkare = FalskTolkare(), synkronJobb = true))
        }
        val sida = client.get("/protokoll")
        assertEquals(HttpStatusCode.OK, sida.status)
        assertTrue(sida.bodyAsText().contains("""rel="canonical" href="/protokoll""""))
        val js = client.get("/protokoll.js").bodyAsText()
        assertFalse("Protokollet ska inte skickas till servern", js.contains("fetch("))
        assertTrue(client.get("/robots.txt").bodyAsText().contains("/protokoll"))
    }
}
