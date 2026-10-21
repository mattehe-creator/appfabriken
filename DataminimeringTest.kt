package se.tmconnect.styrelserummet.server

import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory

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
    fun efterKlartJobb() = runBlocking {
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
            
            Assert.assertEquals(200, res.status.value)
            val body = json.parseToJsonElement(res.bodyAsText()).jsonObject
            val id = body["id"]!!.jsonPrimitive.content
            
            // Vänta tills jobbet är klart
            var status = "KORAR"
            while (status != "KLAR") {
                val s = client.get("/api/jobb/$id")
                val statusBody = s.bodyAsText()
                val metaObj = json.parseToJsonElement(statusBody).jsonObject["meta"]!!.jsonObject
                status = metaObj["status"]!!.jsonPrimitive.content
            }
            
            // Lista alla filer i jobbkatalogen
            val jobbKatalog = tmp.resolve(id)
            val allaFiler: MutableList<Path> = mutableListOf()
            Files.walk(jobbKatalog)
                .filter { Files.isRegularFile(it) }
                .forEach { allaFiler.add(it) }
            
            // Kontrollera att endast tillåtna filer finns
            val tillatnaFiler = setOf("meta.json", "utkast.json")
            val forvarningar = mutableListOf<String>()
            
            for (fil in allaFiler) {
                val relativSokvag = jobbKatalog.relativize(fil).toString()
                
                // Kontrollera att filen är i filer-katalogen eller är en av tillåtna filer
                if (!relativSokvag.startsWith("filer/") && !tillatnaFiler.contains(relativSokvag)) {
                    forvarningar.add("Oväntad fil: $relativSokvag")
                }
                
                // Kontrollera att ingen fil innehåller rå text från PDF
                val innehall = Files.readString(fil)
                if (innehall.contains("Underhall tak 500000") || innehall.contains("Byggnader och mark 500 000")) {
                    forvarningar.add("Fil innehåller rå PDF-text: $relativSokvag")
                }
            }
            
            Assert.assertTrue(
                "Förväntade endast filer i 'filer/', 'meta.json' och 'utkast.json', men hittade: ${allaFiler.joinToString(", ") { jobbKatalog.relativize(it).toString() }}",
                forvarningar.isEmpty()
            )
        }
    }
}
