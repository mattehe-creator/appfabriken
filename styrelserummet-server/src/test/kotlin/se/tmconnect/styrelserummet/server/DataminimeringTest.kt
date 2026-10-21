package se.tmconnect.styrelserummet.server

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.Headers
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
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
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.walk

class DataminimeringTest {
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
    fun efterKlartJobbBaraTillatnaFiler() = runBlocking {
        val planText = "Underhall tak 500000"
        val arText = "Byggnader och mark 500 000"
        val tmp = createTempDirectory("styrelserummet-dataminimering-test")
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

            val planBytes = pdfMedText(planText)
            val arBytes = pdfMedText(arText)

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

            assertEquals(200, res.status.value)
            val body = json.parseToJsonElement(res.bodyAsText()).jsonObject
            val id = body["id"]!!.jsonPrimitive.content

            var status = "KORAR"
            while (status != "KLAR") {
                val s = client.get("/api/jobb/$id")
                val statusBody = s.bodyAsText()
                val metaObj = json.parseToJsonElement(statusBody).jsonObject["meta"]!!.jsonObject
                status = metaObj["status"]!!.jsonPrimitive.content
            }

            val jobbKatalog = tmp / "jobb" / id
            val allaFiler = jobbKatalog.walk().filter { it.isRegularFile() }.toList()

            assertEquals(
                "Exakt 2 PDF + meta.json + utkast.json",
                4,
                allaFiler.size,
            )

            val tillatnaRotfiler = setOf("meta.json", "utkast.json")
            val fel = mutableListOf<String>()

            for (fil in allaFiler) {
                val relativSokvag = jobbKatalog.relativize(fil).toString().replace('\\', '/')

                if (!relativSokvag.startsWith("filer/") && relativSokvag !in tillatnaRotfiler) {
                    fel.add("Oväntad fil: $relativSokvag")
                }

                if (fil.extension.equals("pdf", ignoreCase = true)) {
                    continue
                }
                val innehall = fil.readText()
                if (innehall.contains(planText) || innehall.contains(arText)) {
                    fel.add("Fil innehåller rå PDF-text (parallell textkopia): $relativSokvag")
                }
            }

            assertTrue(
                "Förväntade endast filer i 'filer/', 'meta.json' och 'utkast.json', men hittade: " +
                    allaFiler.joinToString(", ") { jobbKatalog.relativize(it).toString().replace('\\', '/') },
                fel.isEmpty(),
            )
        }
    }

    @Test
    fun efterRaderaFinnsIngenKatalog() = runBlocking {
        val planText = "Underhall tak 500000"
        val arText = "Byggnader och mark 500 000"
        val tmp = createTempDirectory("styrelserummet-dataminimering-test")
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

            val planBytes = pdfMedText(planText)
            val arBytes = pdfMedText(arText)

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

            assertEquals(200, res.status.value)
            val body = json.parseToJsonElement(res.bodyAsText()).jsonObject
            val id = body["id"]!!.jsonPrimitive.content

            var status = "KORAR"
            while (status != "KLAR") {
                val s = client.get("/api/jobb/$id")
                val statusBody = s.bodyAsText()
                val metaObj = json.parseToJsonElement(statusBody).jsonObject["meta"]!!.jsonObject
                status = metaObj["status"]!!.jsonPrimitive.content
            }

            // Radera jobbet
            val raderaRes = client.delete("/api/jobb/$id")
            assertEquals(204, raderaRes.status.value)

            // Kontrollera att katalogen inte finns
            val jobbKatalog = tmp / "jobb" / id
            assertTrue("Jobbkatalogen ska inte finnas efter radering", !jobbKatalog.exists())
        }
    }
}
