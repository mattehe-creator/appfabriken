package se.tmconnect.styrelserummet.server

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.io.ByteArrayOutputStream
import kotlin.io.path.createTempDirectory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JobbAtgarderTest {
    private fun pdf(text: String): ByteArray {
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
    fun atgarderFoljerMedTillFondplan() = testApplication {
        val tolkare = FalskTolkare(
            """{"komponenter":[{"namn":"Tak","grupp":"TAK","anskaffningsvarde":500000,"anskaffningsar":2010,"nyttjandeperiodAr":40,"kalla":"plan, sida 2","sakerhet":"HOG"}],"anmarkningar":[],"atgarder":[{"ar":2028,"belopp":600000,"beskrivning":"Fönster"}]}""",
        )
        application {
            module(ServerMiljo(dataRot = createTempDirectory("atgarder"), port = 0, tolkare = tolkare, synkronJobb = true))
        }
        val res = client.post("/api/jobb") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("forening", "Test brf")
                        append("arsslut", "2025")
                        append("filer", pdf("Underhall tak 500000"), Headers.build {
                            append(HttpHeaders.ContentType, "application/pdf")
                            append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                        })
                    },
                ),
            )
        }
        assertEquals(HttpStatusCode.OK, res.status)
        val id = Json.parseToJsonElement(res.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content
        val svar = client.get("/api/jobb/$id/atgarder")
        assertEquals(HttpStatusCode.OK, svar.status)
        val text = svar.bodyAsText()
        assertTrue(text.contains("\"franAr\":2026"))
        assertTrue(text.contains("Fönster"))
        assertEquals(HttpStatusCode.BadRequest, client.get("/api/jobb/ogiltig/atgarder").status)
    }
}
