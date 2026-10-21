package se.tmconnect.styrelserummet.server

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Headers
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.encryption.AccessPermission
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayOutputStream
import kotlin.io.path.createTempDirectory

class FelSvarTest {
    private val json = Json { ignoreUnknownKeys = true }

    private val textPdfLosenord =
        "PDF:en är lösenordsskyddad. Spara en kopia utan lösenord och ladda upp den."
    private val textAiTidsgrans =
        "Tjänsten hann inte svara i tid. Försök igen om en liten stund."
    private val textAiOgiltigtSvar =
        "Komponentförslagen kunde inte tas fram. Försök igen eller ladda upp en tydligare handling."
    private val textGeneriskt = "Något gick fel. Försök igen."

    @Test
    fun okandKodGerGenerisktSvar() {
        assertEquals(textGeneriskt, felText("FINNS_EJ"))
    }

    @Test
    fun krypteradPdfVisarLosenordstext() = runBlocking {
        jobbFelMeddelande(
            pdfBytes = pdfKrypteradMedText("Underhall tak 500000"),
            forvantatMeddelande = textPdfLosenord,
        )
    }

    @Test
    fun aiTimeoutVisarTidsgransText() = runBlocking {
        jobbFelMeddelande(
            pdfBytes = pdfMedText("Underhall tak 500000"),
            tolkare = FalskTolkare(lage = SvarLage.TIMEOUT),
            forvantatMeddelande = textAiTidsgrans,
        )
    }

    @Test
    fun aiOgiltigtSvarVisarEgenText() = runBlocking {
        jobbFelMeddelande(
            pdfBytes = pdfMedText("Underhall tak 500000"),
            tolkare = FalskTolkare(lage = SvarLage.OGILTIGT_JSON),
            forvantatMeddelande = textAiOgiltigtSvar,
        )
    }

    private suspend fun jobbFelMeddelande(
        pdfBytes: ByteArray,
        forvantatMeddelande: String,
        tolkare: FalskTolkare = FalskTolkare(),
    ) {
        val tmp = createTempDirectory("styrelserummet-felsvar")
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = tolkare,
                        synkronJobb = true,
                    ),
                )
            }
            val client = createClient { }
            val res = client.post("/api/jobb") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("forening", "Test brf")
                            append("arsslut", "2025")
                            append("filer", pdfBytes, Headers.build {
                                append(HttpHeaders.ContentType, "application/pdf")
                                append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                            })
                        },
                    ),
                )
            }
            assertEquals(HttpStatusCode.OK, res.status)
            val id = json.parseToJsonElement(res.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

            val s = client.get("/api/jobb/$id")
            val meta = json.parseToJsonElement(s.bodyAsText()).jsonObject["meta"]!!.jsonObject
            assertEquals("FEL", meta["status"]!!.jsonPrimitive.content)
            assertEquals(forvantatMeddelande, meta["felmeddelande"]!!.jsonPrimitive.content)
        }
    }

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
            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }

    private fun pdfKrypteradMedText(text: String): ByteArray {
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
            val policy = StandardProtectionPolicy("", "hemligt", AccessPermission())
            policy.encryptionKeyLength = 128
            doc.protect(policy)
            return ByteArrayOutputStream().also { doc.save(it) }.toByteArray()
        }
    }
}
