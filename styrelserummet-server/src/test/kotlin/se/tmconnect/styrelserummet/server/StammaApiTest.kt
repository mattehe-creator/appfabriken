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
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.regler.STAMMA_RADGIVNING_RAD
import se.tmconnect.styrelserummet.regler.kronor

class StammaApiTest {

    @Test
    fun tidplanOchAvgifter() {
        val svar = beraknaStammaSvar(
            StammaRequest(
                arsslut = "2025-12-31",
                stammodatum = "2026-05-15",
                kallelseVeckorFran = 4,
                kallelseVeckorTill = 6,
                overlatelseAvgift = 1480,
                pantsattningsAvgift = 592,
                prisbasbeloppAr = 2026,
            ),
        )
        assertEquals(7, svar.rader.size)
        assertEquals("2026-04-17 (fredag)", svar.rader[3].datum)
        assertTrue(svar.csv.contains("etikett;datum;anmärkning;källa"))
        assertTrue(svar.csv.endsWith("$STAMMA_RADGIVNING_RAD\n"))
        assertEquals(kronor(1480), svar.overlatelse!!.hogsta)
        assertNull(svar.overlatelse!!.fel)
        assertEquals(kronor(592), svar.pantsattning!!.hogsta)
        assertTrue(svar.prisbasbeloppInfo!!.contains("2026"))
    }

    @Test
    fun saknatPrisbasbeloppAr() {
        val svar = beraknaStammaSvar(
            StammaRequest(
                arsslut = "2025-12-31",
                stammodatum = "2026-05-15",
                kallelseVeckorFran = 4,
                kallelseVeckorTill = 6,
                overlatelseAvgift = 1000,
                prisbasbeloppAr = 2019,
            ),
        )
        assertNull(svar.overlatelse)
        assertTrue(svar.prisbasbeloppInfo!!.contains("2019"))
    }

    @Test
    fun ogiltigKallelsefrist() {
        assertThrows(VerktygFel::class.java) {
            beraknaStammaSvar(
                StammaRequest("2025-12-31", "2026-05-15", 5, 3),
            )
        }
    }

    @Test
    fun rutter() = testApplication {
        application {
            module(
                ServerMiljo(
                    dataRot = createTempDirectory("stamma"),
                    port = 0,
                    tolkare = FalskTolkare(),
                    synkronJobb = true,
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, client.get("/stamma").status)
        val html = client.get("/stamma").bodyAsText()
        assertTrue(html.contains("""rel="canonical" href="/stamma""""))
        val res = client.post("/api/stamma") {
            contentType(ContentType.Application.Json)
            setBody("""{"arsslut":"2025-12-31","stammodatum":"2026-05-15","kallelseVeckorFran":4,"kallelseVeckorTill":6}""")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        assertTrue(res.bodyAsText().contains("Senaste kallelsedag"))
        assertTrue(client.get("/robots.txt").bodyAsText().contains("/stamma"))
    }
}
