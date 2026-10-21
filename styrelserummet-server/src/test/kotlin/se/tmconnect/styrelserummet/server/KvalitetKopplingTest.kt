package se.tmconnect.styrelserummet.server

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.sammanfatta
import java.io.ByteArrayOutputStream
import kotlin.io.path.createTempDirectory

class KvalitetKopplingTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun pdfMedText(text: String): ByteArray {
        PDDocument().use { doc ->
            val page = PDPage()
            doc.addPage(page)
            PDPageContentStream(doc, page).use { cs ->
                cs.beginText()
                cs.setFont(PDType1Font(Standard14Fonts.FontName.HELVETICA), 12f)
                cs.newLineAtOffset(50f, 700f)
                cs.showText(text)
                cs.endText()
            }
            val out = ByteArrayOutputStream()
            doc.save(out)
            return out.toByteArray()
        }
    }

    @Test
    fun visningSammanfattningExakt() {
        val utkast = UtkastDto(
            forening = "Test brf",
            arsslut = 2025,
            byggnadensVarde = 0,
            komponenter = listOf(
                KomponentDto("Tak", "TAK", 1_000_000, 2010, 40, 0, "plan", "HOG"),
            ),
            avstamning = AvstamningDto(1_000_000, 0, 1_000_000, false),
            anmarkningar = emptyList(),
        )
        val visning = tillVisning(utkast)
        assertEquals(
            sammanfatta(
                listOf(
                    Komponent("Tak", Komponentgrupp.TAK, 1_000_000, 2010, 40, 0, "plan", Sakerhet.HOG),
                ),
                null,
                2025,
            ),
            visning.sammanfattning,
        )
    }

    @Test
    fun visningNyttjandeperiodOchGruppForslag() {
        val utkast = UtkastDto(
            forening = "X",
            arsslut = 2025,
            byggnadensVarde = 0,
            komponenter = listOf(
                KomponentDto("Yttertak papp", "OVRIGT", 100_000, 2010, 99, 0, "plan", "MEDEL"),
                KomponentDto("Tak", "TAK", 100_000, 2010, 50, 0, "plan", "HOG"),
            ),
            avstamning = AvstamningDto(200_000, 0, 200_000, false),
            anmarkningar = emptyList(),
        )
        val visning = tillVisning(utkast)
        val ovrigt = visning.komponenter[0]
        assertEquals("TAK", ovrigt.gruppForslag)
        assertEquals("100\u00A0000 kr", ovrigt.anskaffningsvardeText)
        assertTrue(ovrigt.nyttjandeperiodVarning)
        assertTrue(ovrigt.nyttjandeperiodHjalp!!.contains("Förslag"))
        val tak = visning.komponenter[1]
        assertFalse(tak.nyttjandeperiodVarning)
        assertEquals(null, tak.gruppForslag)
    }

    @Test
    fun jamforelseCsv() {
        val utkast = UtkastDto(
            forening = "X",
            arsslut = 2025,
            byggnadensVarde = 0,
            komponenter = listOf(
                KomponentDto("Tak", "TAK", 500_000, 2010, 40, 0, "plan", "HOG"),
            ),
            avstamning = AvstamningDto(500_000, 0, 500_000, false),
            anmarkningar = emptyList(),
        )
        val csv = """
            Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet
            Hiss;Stomme;50000;2015;30;40000;plan;HOG
        """.trimIndent()
        val svar = jamforelseMotCsv(utkast, csv)
        assertEquals(2, svar.forandringar.size)
        assertEquals("BORTTAGEN", svar.forandringar[0].typ)
        assertEquals("Hiss", svar.forandringar[0].namn)
        assertEquals("NY", svar.forandringar[1].typ)
        assertEquals("Tak", svar.forandringar[1].namn)
    }

    @Test
    fun apiVisningEfterJobb() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-kvalitet")
        testApplication {
            application {
                module(ServerMiljo(tmp, 0, FalskTolkare(), synkronJobb = true))
            }
            val client = createClient { }
            val id = skapaJobb(client)
            val res = client.get("/api/jobb/$id")
            assertEquals(HttpStatusCode.OK, res.status)
            val root = json.parseToJsonElement(res.bodyAsText()).jsonObject
            val visning = root["visning"]!!.jsonObject
            assertTrue(visning["sammanfattning"]!!.jsonArray.size >= 5)
            assertTrue(visning["komponenter"]!!.jsonArray.isNotEmpty())
            assertTrue(visning["utskrift"]!!.jsonArray.isNotEmpty())
        }
    }

    @Test
    fun apiJamforelseEndpoint() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-jamforelse")
        val tolkare = FalskTolkare(
            jsonSvar = """
                {"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"restvarde":0,"kalla":"plan","sakerhet":"HOG"}]}
            """.trimIndent(),
        )
        testApplication {
            application {
                module(ServerMiljo(tmp, 0, tolkare, synkronJobb = true))
            }
            val client = createClient { }
            val id = skapaJobb(client)
            val res = client.post("/api/jobb/$id/jamforelse") {
                contentType(ContentType.Application.Json)
                setBody(
                    """{"csv":"Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet\nTak;Tak;500000;2010;40;400000;plan;HOG"}""",
                )
            }
            assertEquals(HttpStatusCode.OK, res.status)
            val andringar = json.parseToJsonElement(res.bodyAsText()).jsonObject["forandringar"]!!.jsonArray
            assertTrue(andringar.isEmpty())
        }
    }

    @Test
    fun utskriftHtmlInnehallerFormateradeBelopp() {
        val utkast = UtkastDto(
            forening = "Brf Sol",
            arsslut = 2025,
            byggnadensVarde = 0,
            komponenter = listOf(
                KomponentDto("El", "EL", 500_000, 2015, 50, 0, "plan", "HOG"),
            ),
            avstamning = AvstamningDto(500_000, 0, 500_000, false),
            anmarkningar = emptyList(),
        )
        val html = utskriftHtml("Brf Sol", 2025, tillVisning(utkast).utskrift)
        assertTrue(html.contains("500\u00A0000 kr"))
        assertTrue(html.contains("UTKAST"))
    }

    @Test
    fun felBegaranAnvanderForklara() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-fel")
        testApplication {
            application {
                module(ServerMiljo(tmp, 0, FalskTolkare(), synkronJobb = true))
            }
            val client = createClient { }
            val res = client.put("/api/jobb/00000000000000000000000000000001/rader") {
                contentType(ContentType.Application.Json)
                setBody("""{"komponenter":[]}""")
            }
            assertEquals(HttpStatusCode.NotFound, res.status)
            assertEquals(
                felText(Felkoder.JOBB_SAKNAS),
                json.parseToJsonElement(res.bodyAsText()).jsonObject["fel"]!!.jsonPrimitive.content,
            )
        }
    }

    @Test
    fun nyttjandeperiodHjalpText() {
        assertEquals(
            "Förslag enligt standardtabell: 50 år (typintervall 40\u2013100). UTKAST.",
            nyttjandeperiodHjalp(50, "40\u2013100"),
        )
    }

    private suspend fun skapaJobb(client: io.ktor.client.HttpClient): String {
        val pdf = pdfMedText("Underhall tak 500000")
        val res = client.post("/api/jobb") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("forening", "Test brf")
                        append("arsslut", "2025")
                        append("filer", pdf, Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                        })
                    },
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, res.status)
        return json.parseToJsonElement(res.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content
    }
}
