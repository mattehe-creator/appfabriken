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
    fun helhetFlodeMedTreKomponenterOchMaskning() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-test")
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = FalskTolkare(
                            jsonSvar = """
                                {
                                    "komponenter": [
                                        {
                                            "namn": "Tak",
                                            "grupp": "TAK",
                                            "anskaffningsvarde": 500000,
                                            "anskaffningsar": 2010,
                                            "nyttjandeperiodAr": 40,
                                            "restvarde": 0,
                                            "kalla": "plan",
                                            "sakerhet": "HOG"
                                        },
                                        {
                                            "namn": "Fasad",
                                            "grupp": "FASAD",
                                            "anskaffningsvarde": 300000,
                                            "anskaffningsar": 2015,
                                            "nyttjandeperiodAr": 30,
                                            "restvarde": 0,
                                            "kalla": "arsredovisning",
                                            "sakerhet": "HOG"
                                        },
                                        {
                                            "namn": "Fonster",
                                            "grupp": "FONSTER",
                                            "anskaffningsvarde": 200000,
                                            "anskaffningsar": 2020,
                                            "nyttjandeperiodAr": 20,
                                            "restvarde": 0,
                                            "kalla": "plan",
                                            "sakerhet": "HOG"
                                        }
                                    ]
                                }
                            """.trimIndent()
                        ),
                        synkronJobb = true,
                    ),
                )
            }
            val client = createClient { }
            run {
                val planBytes = pdfMedText("Styrelseledamot 19800101-1234 Underhall tak")
                val arBytes = pdfMedText("Byggnader och mark 1 000 000")
                
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
                
                // Kontrollera CSV-rubriker
                val csv = client.get("/api/jobb/$id.csv")
                assertEquals(HttpStatusCode.OK, csv.status)
                val csvText = csv.bodyAsText()
                
                // Kontrollera att CSV innehåller UTKAST
                assertTrue("CSV ska innehålla UTKAST", csvText.contains("UTKAST"))
                
                // Kontrollera CSV-rubriker
                val rubriker = "Komponent;Grupp;Anskaffningsvärde;Anskaffningsår;Nyttjandeperiod;Bokfört värde;Källa;Säkerhet"
                assertTrue("CSV ska innehålla rätt rubriker", csvText.startsWith(rubriker))
                
                // Räkna antal datarader (exklusive rubrik och utkastrad)
                val rader = csvText.lines()
                val datarader = rader.drop(1).dropLast(1) // Ta bort rubrik och sista raden (UTKAST-rad)
                assertEquals("Ska ha tre datarader", 3, datarader.size)
                
                // Summera anskaffningsvärden
                var summa = 0L
                for (rad in datarader) {
                    val falt = rad.split(';')
                    if (falt.size >= 3) {
                        summa += falt[2].toLongOrNull() ?: 0L
                    }
                }
                assertEquals("Summan av anskaffningsvärden ska vara 1 000 000", 1_000_000L, summa)
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

    @Test
    fun halsaUtanData() = runBlocking {
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = createTempDirectory("styrelserummet-halsa"),
                        port = 0,
                        tolkare = FalskTolkare(),
                    ),
                )
            }
            val client = createClient { }
            val res = client.get("/halsa")
            assertEquals(HttpStatusCode.OK, res.status)
            assertEquals("ok", res.bodyAsText())
        }
    }

    @Test
    fun svarshuvudenPaStartOchJobb() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-huvuden")
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
            val start = client.get("/")
            assertEquals("no-referrer", start.headers["Referrer-Policy"])
            assertEquals("nosniff", start.headers["X-Content-Type-Options"])
            assertTrue(start.headers["Content-Security-Policy"]!!.contains("default-src 'self'"))

            val pdf = pdfMedText("Underhall tak 500000")
            val skapa = client.post("/api/jobb") {
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
            val id = json.parseToJsonElement(skapa.bodyAsText()).jsonObject["id"]!!.jsonPrimitive.content
            val jobb = client.get("/api/jobb/$id")
            assertEquals("no-store", jobb.headers["Cache-Control"])
            assertEquals("noindex", jobb.headers["X-Robots-Tag"])
            assertEquals("no-referrer", jobb.headers["Referrer-Policy"])
        }
    }

    @Test
    fun ipGransPerForwardedForNarProxyPa() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-proxy")
        testApplication {
            application {
                module(
                    ServerMiljo(
                        dataRot = tmp,
                        port = 0,
                        tolkare = FalskTolkare(),
                        synkronJobb = true,
                        proxyPaa = true,
                    ),
                )
            }
            val client = createClient { }
            val pdf = pdfMedText("Underhall tak 500000")
            repeat(MAX_JOBB_PER_IP_TIMME) {
                val res = client.post("/api/jobb") {
                    header("X-Forwarded-For", "203.0.113.5, 10.0.0.1")
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
            }
            val over = client.post("/api/jobb") {
                header("X-Forwarded-For", "203.0.113.5, 10.0.0.1")
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
            assertEquals(HttpStatusCode.TooManyRequests, over.status)
        }
    }

    @Test
    fun tomForeningGer400() = runBlocking {
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
                            append("forening", "")
                            append("arsslut", "2025")
                            append("filer", pdf, Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=plan.pdf")
                            })
                        },
                    ),
                )
            }
            assertEquals(HttpStatusCode.BadRequest, res.status)
            val forvantatFel = valideraForeningsnamn("")!!
            assertTrue(res.bodyAsText().contains(forvantatFel))
        }
    }

    @Test
    fun pdfFilnamnMenIntePdfBytesGer400() = runBlocking {
        val tmp = createTempDirectory("styrelserummet-test")
        testApplication {
            application {
                module(ServerMiljo(tmp, 0, FalskTolkare(), synkronJobb = true))
            }
            val client = createClient { }
            val res = client.post("/api/jobb") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("forening", "Test brf")
                            append("arsslut", "2025")
                            append("filer", "NOTPDF".encodeToByteArray(), Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=rapport.pdf")
                            })
                        },
                    ),
                )
            }
            assertEquals(HttpStatusCode.BadRequest, res.status)
            val fel = json.parseToJsonElement(res.bodyAsText()).jsonObject["fel"]!!.jsonPrimitive.content
            assertEquals("Bara PDF-filer kan laddas upp.", fel)
        }
    }
}
