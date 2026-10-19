package se.tmconnect.styrelserummet.server

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.cio.CIO
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.http.content.staticResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import se.tmconnect.styrelserummet.regler.tillCsv
import se.tmconnect.styrelserummet.underlag.AnthropicTolkare
import se.tmconnect.styrelserummet.underlag.Tolkare
import se.tmconnect.styrelserummet.underlag.uppdateraKomponenter
import java.nio.file.Path

data class ServerMiljo(
    val dataRot: Path,
    val port: Int,
    val tolkare: Tolkare,
    val synkronJobb: Boolean = false,
)

fun miljoFranEnv(): ServerMiljo {
    val data = System.getenv("STYRELSERUMMET_DATA") ?: "./data/styrelserummet"
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val nyckel = System.getenv("ANTHROPIC_API_KEY").orEmpty()
    val modell = System.getenv("STYRELSERUMMET_MODELL") ?: "claude-sonnet-5-5"
    val prompt = checkNotNull(
        Thread.currentThread().contextClassLoader.getResourceAsStream("prompter/komponenter.txt"),
    ) { "Saknar prompter/komponenter.txt" }.bufferedReader().readText()
    val tolkare = AnthropicTolkare(nyckel, modell, systemprompt = prompt)
    return ServerMiljo(Path.of(data), port, tolkare)
}

fun Application.module(miljo: ServerMiljo) {
    val lagring = JobbLagring(miljo.dataRot)
    val ipGrans = IpGrans(miljo.dataRot)
    val scope = CoroutineScope(SupervisorJob())
    val ko = JobbKo(lagring, miljo.tolkare, scope = scope, synkron = miljo.synkronJobb)
    rensaGamlaJobb(lagring)

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(StatusPages) {
        exception<FelBegaran> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("fel" to cause.meddelande))
        }
        exception<Throwable> { call, _ ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("fel" to "Något gick fel. Försök igen."))
        }
    }

    routing {
        staticResources("/", "static")

        get("/j/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respondText(
                javaClass.classLoader.getResource("static/jobb.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        get("/integritet") {
            call.respondText(
                javaClass.classLoader.getResource("static/integritet.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        route("/api/jobb") {
            post {
                val ip = call.request.local.remoteHost
                if (!ipGrans.farSkapaJobb(ip)) {
                    call.respond(
                        HttpStatusCode.TooManyRequests,
                        mapOf("fel" to "För många jobb från samma adress. Vänta en timme och försök igen."),
                    )
                    return@post
                }
                var forening = ""
                var arsslut = 0
                var filAntal = 0
                var totalBytes = 0L
                val id = genereraJobbId()
                val meta = JobbMeta(
                    id = id,
                    forening = "",
                    arsslut = 0,
                    status = JobbStatus.VANTAR.name,
                    skapadMs = System.currentTimeMillis(),
                    senastAktivMs = System.currentTimeMillis(),
                    klientIpHash = ipGrans.hashIp(ip),
                )
                lagring.skapa(meta)

                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> when (part.name) {
                            "forening" -> forening = part.value.trim()
                            "arsslut" -> arsslut = part.value.trim().toIntOrNull() ?: 0
                        }
                        is PartData.FileItem -> {
                            if (filAntal >= MAX_FILER) {
                                part.dispose()
                                throw FelBegaran("Högst $MAX_FILER filer per jobb.")
                            }
                            val bytes = part.streamProvider().use { it.readBytes() }
                            totalBytes += bytes.size
                            if (totalBytes > MAX_BYTES_PER_JOBB) {
                                part.dispose()
                                lagring.raderaJobb(id)
                                throw FelBegaran("Filerna får vara högst 25 MB tillsammans.")
                            }
                            val namn = part.originalFileName ?: "fil.pdf"
                            if (!namn.lowercase().endsWith(".pdf")) {
                                part.dispose()
                                lagring.raderaJobb(id)
                                throw FelBegaran("Bara PDF-filer kan laddas upp.")
                            }
                            lagring.sparaFil(id, namn, bytes)
                            filAntal++
                        }
                        else -> Unit
                    }
                    part.dispose()
                }

                if (forening.isBlank()) {
                    lagring.raderaJobb(id)
                    throw FelBegaran("Ange föreningens namn.")
                }
                if (arsslut !in 2000..2100) {
                    lagring.raderaJobb(id)
                    throw FelBegaran("Ange räkenskapsårets slut (år).")
                }
                if (filAntal == 0) {
                    lagring.raderaJobb(id)
                    throw FelBegaran("Ladda upp minst en PDF.")
                }

                lagring.skrivMeta(
                    meta.copy(forening = forening, arsslut = arsslut, senastAktivMs = System.currentTimeMillis()),
                )
                ipGrans.registreraJobb(ip)
                ko.lagTill(id)

                call.respond(
                    mapOf(
                        "id" to id,
                        "lank" to "/j/$id",
                        "meddelande" to "Spara länken. Den visas bara nu och kan inte skickas till er senare.",
                    ),
                )
            }

            get("/{id}.csv") {
                val id = call.parameters["id"] ?: throw FelBegaran("Saknar jobb-id.")
                if (!arGiltigtJobbId(id)) throw FelBegaran("Ogiltig länk.")
                val utkast = lagring.lasUtkast(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to "Jobbet finns inte."))
                lagring.uppdateraAktivitet(id)
                val csv = tillCsv(utkast.komponenter, utkast.arsslut)
                call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"komponentlista.csv\"")
                call.respondText(csv, ContentType.parse("text/csv; charset=utf-8"))
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: throw FelBegaran("Saknar jobb-id.")
                if (!arGiltigtJobbId(id)) throw FelBegaran("Ogiltig länk.")
                val meta = lagring.lasMeta(id) ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to "Jobbet finns inte."))
                lagring.uppdateraAktivitet(id)
                val utkast = lagring.lasUtkast(id)
                call.respond(JobbResponse(meta, utkast))
            }

            put("/{id}/rader") {
                val id = call.parameters["id"] ?: throw FelBegaran("Saknar jobb-id.")
                if (!arGiltigtJobbId(id)) throw FelBegaran("Ogiltig länk.")
                val meta = lagring.lasMeta(id) ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("fel" to "Jobbet finns inte."))
                if (meta.status != JobbStatus.KLAR.name) {
                    throw FelBegaran("Jobbet är inte klart än.")
                }
                val body = call.receive<RaderRequest>()
                val gammalt = lagring.lasUtkast(id) ?: throw FelBegaran("Saknar utkast.")
                val nytt = uppdateraKomponenter(
                    body.komponenter,
                    gammalt.byggnadensVarde,
                    gammalt.forening,
                    gammalt.arsslut,
                )
                lagring.sparaUtkast(id, nytt)
                lagring.uppdateraAktivitet(id)
                call.respond(nytt)
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: throw FelBegaran("Saknar jobb-id.")
                if (!arGiltigtJobbId(id)) throw FelBegaran("Ogiltig länk.")
                lagring.raderaJobb(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

class FelBegaran(val meddelande: String) : Exception(meddelande)

fun main() {
    val miljo = miljoFranEnv()
    embeddedServer(CIO, port = miljo.port) {
        module(miljo)
    }.start(wait = true)
}
