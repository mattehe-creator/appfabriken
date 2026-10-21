package se.tmconnect.styrelserummet.server

import io.ktor.client.request.delete
import io.ktor.client.request.header
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
import java.io.ByteArrayOutputStream
import kotlin.io.path.createTempDirectory
import kotlinx.coroutines.runBlocking

class BetalningApiTest {
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

    private class FalskBetalare : Betalare {
        var betalt = false
        var skapade = 0
        override fun skapaKassa(jobbId: String): Kassa {
            skapade++
            return Kassa("cs_test_abcdefgh123", "https://checkout.stripe.com/c/pay/cs_test_abcdefgh123")
        }
        override fun kontrollera(kassaId: String, jobbId: String): KassaStatus =
            if (betalt) KassaStatus.Betald else KassaStatus.EjBetald
    }

    @Test
    fun csvOchUtskriftKraverBetalningNarDenArPa() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-betal")
        val betalare = FalskBetalare()
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = FalskTolkare(),
                        synkronJobb = true,
                        betalning = BetalKonfig(stripeNyckel = "sk_test_x", basUrl = "https://exempel.se"),
                        betalare = betalare,
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
                            append("filer", pdfMedText("Underhall tak 500000"), Headers.build {
                                append(HttpHeaders.ContentType, "application/pdf")
                                append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                            })
                        },
                    ),
                )
            }
            assertEquals(HttpStatusCode.OK, res.status)
            val id = json.parseToJsonElement(res.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content

            val forst = json.parseToJsonElement(client.get("/api/jobb/$id").bodyAsText()).jsonObject
            assertEquals("KLAR", forst["meta"]!!.jsonObject["status"]!!.jsonPrimitive.content)
            val betal = forst["betalning"]!!.jsonObject
            assertEquals("true", betal["aktiv"]!!.jsonPrimitive.content)
            assertEquals("false", betal["betald"]!!.jsonPrimitive.content)
            assertEquals("990", betal["prisKr"]!!.jsonPrimitive.content)

            assertEquals(HttpStatusCode.PaymentRequired, client.get("/api/jobb/$id.csv").status)
            assertEquals(HttpStatusCode.PaymentRequired, client.get("/j/$id/utskrift").status)

            val kassa = client.post("/api/jobb/$id/kassa")
            assertEquals(HttpStatusCode.OK, kassa.status)
            assertTrue(kassa.bodyAsText().contains("https://checkout.stripe.com/"))
            assertEquals(1, betalare.skapade)

            val tidig = client.post("/api/jobb/$id/bekrafta") {
                contentType(ContentType.Application.Json)
                setBody("""{"kassa":"cs_test_abcdefgh123"}""")
            }
            assertEquals(HttpStatusCode.PaymentRequired, tidig.status)

            betalare.betalt = true
            // Kunden kommer inte tillbaka till sidan: nästa hämtning frågar Stripe och låser upp.
            val efter = json.parseToJsonElement(client.get("/api/jobb/$id").bodyAsText()).jsonObject
            assertEquals("true", efter["betalning"]!!.jsonObject["betald"]!!.jsonPrimitive.content)
            assertFalse(efter["meta"]!!.jsonObject.containsKey("kassaId") && efter["meta"]!!.jsonObject["kassaId"].toString() != "null")

            assertEquals(HttpStatusCode.OK, client.get("/api/jobb/$id.csv").status)
            assertEquals(HttpStatusCode.OK, client.get("/j/$id/utskrift").status)
        }
    }

    @Test
    fun avstangdBetalningLamnarUtDirekt() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-betal-av")
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = FalskTolkare(),
                        synkronJobb = true,
                        betalning = BetalKonfig(stripeNyckel = null, basUrl = null),
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
                            append("filer", pdfMedText("Underhall tak 500000"), Headers.build {
                                append(HttpHeaders.ContentType, "application/pdf")
                                append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                            })
                        },
                    ),
                )
            }
            val id = json.parseToJsonElement(res.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content
            assertEquals(HttpStatusCode.OK, client.get("/api/jobb/$id.csv").status)
            assertEquals(HttpStatusCode.Conflict, client.post("/api/jobb/$id/kassa").status)
        }
    }
}
