package se.tmconnect.styrelserummet.server

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.io.path.createTempDirectory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VerktygSkyddTest {
    private val json = Json { ignoreUnknownKeys = true }

    private val minFondplanJson =
        """{"franAr":2026,"antalAr":5,"ingaendeFond":300000,"atgarder":[]}"""

    private fun testMiljo(basUrl: String? = null) =
        ServerMiljo(
            dataRot = createTempDirectory("verktyg-skydd"),
            port = 0,
            tolkare = FalskTolkare(),
            synkronJobb = true,
            proxyPaa = true,
            betalning = BetalKonfig(stripeNyckel = null, basUrl = basUrl),
        )

    @Test
    fun sextioforstaAnropetGer429() = testApplication {
        application { module(testMiljo()) }
        repeat(MAX_VERKTYG_ANROP_PER_IP_TIMME) {
            val res = client.post("/api/fondplan") {
                header("X-Forwarded-For", "10.0.0.1, 203.0.113.50")
                contentType(ContentType.Application.Json)
                setBody(minFondplanJson)
            }
            assertEquals(HttpStatusCode.OK, res.status)
        }
        val over = client.post("/api/fondplan") {
            header("X-Forwarded-For", "10.0.0.1, 203.0.113.50")
            contentType(ContentType.Application.Json)
            setBody(minFondplanJson)
        }
        assertEquals(HttpStatusCode.TooManyRequests, over.status)
        val fel =
            json.parseToJsonElement(over.bodyAsText()).jsonObject["fel"]!!.jsonPrimitive.content
        assertEquals(felText(Felkoder.FOR_MANGA_VERKTYG), fel)
    }

    @Test
    fun annanKlientPaverkasInte() = testApplication {
        application { module(testMiljo()) }
        repeat(MAX_VERKTYG_ANROP_PER_IP_TIMME) {
            client.post("/api/fondplan") {
                header("X-Forwarded-For", "10.0.0.1, 203.0.113.50")
                contentType(ContentType.Application.Json)
                setBody(minFondplanJson)
            }
        }
        val annan = client.post("/api/fondplan") {
            header("X-Forwarded-For", "10.0.0.1, 203.0.113.51")
            contentType(ContentType.Application.Json)
            setBody(minFondplanJson)
        }
        assertEquals(HttpStatusCode.OK, annan.status)
    }

    @Test
    fun forStorKroppGer413() = testApplication {
        application { module(testMiljo()) }
        val utfyllnad = "x".repeat(MAX_VERKTYG_KROPP_BYTES)
        val stor =
            """{"franAr":2026,"antalAr":5,"ingaendeFond":300000,"utfyllnad":"$utfyllnad"}"""
        assertTrue(stor.toByteArray(Charsets.UTF_8).size > MAX_VERKTYG_KROPP_BYTES)
        val res = client.post("/api/fondplan") {
            contentType(ContentType.Application.Json)
            setBody(stor)
        }
        assertEquals(HttpStatusCode.PayloadTooLarge, res.status)
        val fel =
            json.parseToJsonElement(res.bodyAsText()).jsonObject["fel"]!!.jsonPrimitive.content
        assertEquals(felText(Felkoder.FOR_STOR_VERKTYGSFORFRAGAN), fel)
    }

    @Test
    fun robotsOchSitemapMedBasUrl() = testApplication {
        application { module(testMiljo(basUrl = "https://styrelserummet.se")) }
        val robots = client.get("/robots.txt").bodyAsText()
        assertTrue(robots.contains("Allow: /nyckeltal"))
        assertTrue(robots.contains("Sitemap: https://styrelserummet.se/sitemap.xml"))
        val sitemap = client.get("/sitemap.xml")
        assertEquals(HttpStatusCode.OK, sitemap.status)
        val xml = sitemap.bodyAsText()
        assertTrue(xml.contains("<loc>https://styrelserummet.se/nyckeltal</loc>"))
        assertTrue(xml.contains("<loc>https://styrelserummet.se/fondplan</loc>"))
        assertTrue(xml.contains("<loc>https://styrelserummet.se/integritet</loc>"))
        assertTrue(xml.contains("<loc>https://styrelserummet.se</loc>") || xml.contains("<loc>https://styrelserummet.se/</loc>"))
    }

    @Test
    fun sitemapSaknasUtanBasUrl() = testApplication {
        application { module(testMiljo(basUrl = null)) }
        val robots = client.get("/robots.txt").bodyAsText()
        assertFalse(robots.contains("Sitemap:"))
        assertEquals(HttpStatusCode.NotFound, client.get("/sitemap.xml").status)
    }

    @Test
    fun integritetNamnerVerktygen() = testApplication {
        application { module(testMiljo()) }
        val html = client.get("/integritet").bodyAsText()
        assertTrue(html.contains("Nyckeltal, Fondplan och Räntor"))
        assertTrue(html.contains("räknar i minnet"))
    }

    @Test
    fun verktygssidorHarCanonicalOchOpenGraph() = testApplication {
        application { module(testMiljo()) }
        val nyck = client.get("/nyckeltal").bodyAsText()
        assertTrue(nyck.contains("""rel="canonical" href="/nyckeltal""""))
        assertTrue(nyck.contains("property=\"og:title\""))
        assertTrue(nyck.contains("property=\"og:type\" content=\"website\""))
        val fond = client.get("/fondplan").bodyAsText()
        assertTrue(fond.contains("""rel="canonical" href="/fondplan""""))
        assertTrue(fond.contains("property=\"og:description\""))
    }
}
