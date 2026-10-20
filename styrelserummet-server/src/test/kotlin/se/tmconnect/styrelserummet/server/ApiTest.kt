package se.tmconnect.styrelserummet.server

import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormPart
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
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
import java.io.ByteArrayOutputStream
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.runBlocking

class ApiTest {
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
    fun skapaHamtaCsvRadera() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-test")
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = FalskTolkare(),
                        synkronJobb = true,
                    ),
                )
            }
            val client = createClient { }
            run {
        val planBytes = pdfMedText("Underhall tak 500000")
        val arBytes = pdfMedText("Byggnader och mark 500 000")
        val res = client.post("/api/jobb") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("forening", "Test brf")
                        append("arsslut", "2025")
                        append("filer", planBytes, Headers.build {
                            append(HttpHeaders.ContentType, "application/pdf")
                            append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                        })
                        append("filer", arBytes, Headers.build {
                            append(HttpHeaders.ContentType, "application/pdf")
                            append(HttpHeaders.ContentDisposition, "filename=arsredovisning.pdf")
                        })
                    },
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, res.status)
        val body = json.parseToJsonElement(res.bodyAsText()).jsonObject
        val id = body["id"]!!.jsonPrimitive.content
        assertTrue(arGiltigtJobbId(id))

        val s = client.get("/api/jobb/$id")
        val statusBody = s.bodyAsText()
        assertEquals(HttpStatusCode.OK, s.status)
        val root = json.parseToJsonElement(statusBody).jsonObject
        val metaObj = root["meta"]?.jsonObject
        if (metaObj == null) {
            org.junit.Assert.fail("Saknar meta: $statusBody")
        }
        val status = metaObj!!["status"]!!.jsonPrimitive.content
        if (status == "FEL") {
            val fel = metaObj["felmeddelande"]?.jsonPrimitive?.content
            org.junit.Assert.fail("Jobb FEL: $fel\n$statusBody")
        }
        assertEquals("KLAR", status)

        val csv = client.get("/api/jobb/$id.csv")
        assertEquals(HttpStatusCode.OK, csv.status)
        assertTrue(csv.bodyAsText().contains("UTKAST"))

        val uppdatera = client.put("/api/jobb/$id/rader") {
            contentType(ContentType.Application.Json)
            setBody("""{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"restvarde":0,"kalla":"plan","sakerhet":"HOG"}]}""")
        }
        assertEquals(HttpStatusCode.OK, uppdatera.status)

        val bort = client.delete("/api/jobb/$id")
        assertEquals(HttpStatusCode.NoContent, bort.status)
        assertEquals(HttpStatusCode.NotFound, client.get("/api/jobb/$id").status)
            }
        }
    }

    @Test
    fun forMangaFiler() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-test")
        testApplication {
            application {
                module(ServerMiljo(tmp, 0, FalskTolkare(), synkronJobb = true))
            }
            val client = createClient { }
            val pdf = pdfMedText("x")
            val res = client.post("/api/jobb") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("forening", "X")
                            append("arsslut", "2025")
                            repeat(11) { i ->
                                append("filer", pdf, Headers.build {
                                    append(HttpHeaders.ContentDisposition, "filename=f$i.pdf")
                                })
                            }
                        },
                    ),
                )
            }
            assertEquals(HttpStatusCode.BadRequest, res.status)
        }
    }

    @Test
    fun rensningsregel() {
        val gammal = System.currentTimeMillis() - (31L * 24 * 60 * 60 * 1000)
        assertTrue(skaRensas(gammal))
        assertFalse(skaRensas(System.currentTimeMillis()))
    }
}
