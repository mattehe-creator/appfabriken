package se.tmconnect.styrelserummet.server

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.tolking.anthropicTolkareFranMiljo
import java.nio.file.Path
import java.time.Year

data class ServerMiljo(
    val dataRot: Path,
    val port: Int,
    val tolkare: Tolkare,
    val synkronJobb: Boolean = false,
    val proxyPaa: Boolean = proxyPaaFranMiljo(),
)

fun miljoFranEnv(): ServerMiljo {
    val data = System.getenv("STYRELSERUMMET_DATA") ?: "./data/styrelserummet"
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val tolkare = anthropicTolkareFranMiljo()
    return ServerMiljo(Path.of(data), port, tolkare)
}

fun ApplicationCall.klientIp(proxyPaa: Boolean): String =
    klientAdress(
        request.headers["X-Forwarded-For"],
        request.local.remoteHost,
        proxyPaa,
    )

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
            call.respond(HttpStatusCode.BadRequest, mapOf("fel" to felText(cause.kod)))
        }
        exception<ServerFelBegaran> { call, cause ->
            call.respond(
                HttpStatusCode.fromValue(felTillHttpStatus(cause.fel)),
                mapOf("fel" to felText(serverFelTillKod(cause.fel))),
            )
        }
        exception<Throwable> { call, _ ->
            call.respond(HttpStatusCode.InternalServerError, mapOf("fel" to felText(Felkoder.INTERNT_FEL)))
        }
    }

    intercept(ApplicationCallPipeline.Call) {
        for ((namn, varde) in svarshuvudenForSokvag(call.request.path())) {
            call.response.headers.append(namn, varde)
        }
    }

    routing {
        get("/halsa") {
            call.respondText("ok")
        }

        staticResources("/", "static")

        get("/j/{id}") {
            call.respondText(
                javaClass.classLoader.getResource("static/jobb.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        get("/j/{id}/utskrift") {
            val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
            if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
            val meta = lagring.lasMeta(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
            val utkast = lagring.lasUtkast(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.SAKNAR_UTKAST)))
            if (meta.status != JobbStatus.KLAR.name) {
                throw FelBegaran(Felkoder.JOBB_EJ_KLART)
            }
            lagring.uppdateraAktivitet(id)
            val visning = tillVisning(utkast)
            call.respondText(
                utskriftHtml(meta.forening, utkast.arsslut, visning.utskrift),
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
                val ip = call.klientIp(miljo.proxyPaa)
                val nuMs = System.currentTimeMillis()
                if (!ipGrans.farSkapaJobb(ip, nuMs)) {
                    val fel = ServerFel.ForMangaJobb(ipGrans.nastaForsokEpochMs(ip, nuMs))
                    call.respond(
                        HttpStatusCode.fromValue(felTillHttpStatus(fel)),
                        mapOf("fel" to felText(serverFelTillKod(fel))),
                    )
                    return@post
                }
                var forening = ""
                var arsslutText = ""
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
                            "arsslut" -> arsslutText = part.value.trim()
                        }
                        is PartData.FileItem -> {
                            if (filAntal >= MAX_FILER) {
                                part.dispose()
                                throw FelBegaran(Felkoder.FOR_MANGA_FILER)
                            }
                            val bytes = part.streamProvider().use { it.readBytes() }
                            totalBytes += bytes.size
                            if (totalBytes > MAX_BYTES_PER_JOBB) {
                                part.dispose()
                                lagring.raderaJobb(id)
                                throw ServerFelBegaran(ServerFel.ForStorFil)
                            }
                            val namn = part.originalFileName ?: "fil.pdf"
                            if (!arPdf(bytes)) {
                                part.dispose()
                                lagring.raderaJobb(id)
                                throw ServerFelBegaran(ServerFel.IntePdf)
                            }
                            lagring.sparaFil(id, sakertFilnamn(namn), bytes)
                            filAntal++
                        }
                        else -> Unit
                    }
                    part.dispose()
                }

                foreningFelKod(forening)?.let { kod ->
                    lagring.raderaJobb(id)
                    throw FelBegaran(kod)
                }
                val ar = valideraArsslut(arsslutText, Year.now().value)
                    ?: run {
                        lagring.raderaJobb(id)
                        throw FelBegaran(Felkoder.ARSSLUT_SAKNAS)
                    }
                if (filAntal == 0) {
                    lagring.raderaJobb(id)
                    throw FelBegaran(Felkoder.INGA_PDF)
                }

                lagring.skrivMeta(
                    meta.copy(forening = forening, arsslut = ar, senastAktivMs = System.currentTimeMillis()),
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
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val utkast = lagring.lasUtkast(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                lagring.uppdateraAktivitet(id)
                val csv = tillCsv(utkast.komponenter, utkast.arsslut)
                call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"komponentlista.csv\"")
                call.respondText(csv, ContentType.parse("text/csv; charset=utf-8"))
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                lagring.uppdateraAktivitet(id)
                val utkast = lagring.lasUtkast(id)
                call.respond(jobbResponse(meta, utkast))
            }

            put("/{id}/rader") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (meta.status != JobbStatus.KLAR.name) {
                    throw FelBegaran(Felkoder.JOBB_EJ_KLART)
                }
                val body = call.receive<RaderRequest>()
                val gammalt = lagring.lasUtkast(id) ?: throw FelBegaran(Felkoder.SAKNAR_UTKAST)
                val nytt = uppdateraKomponenter(body.komponenter, gammalt)
                lagring.sparaUtkast(id, nytt)
                lagring.uppdateraAktivitet(id)
                call.respond(jobbResponse(meta, nytt))
            }

            post("/{id}/jamforelse") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (meta.status != JobbStatus.KLAR.name) {
                    throw FelBegaran(Felkoder.JOBB_EJ_KLART)
                }
                val utkast = lagring.lasUtkast(id) ?: throw FelBegaran(Felkoder.SAKNAR_UTKAST)
                val body = call.receive<JamforelseRequest>()
                lagring.uppdateraAktivitet(id)
                call.respond(jamforelseMotCsv(utkast, body.csv))
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                lagring.raderaJobb(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

class FelBegaran(val kod: String) : Exception(felText(kod))

private class ServerFelBegaran(val fel: ServerFel) : Exception()

fun main() {
    val miljo = miljoFranEnv()
    val env = System.getenv()
    val rader = kontrolleraMiljo(env) { katalog ->
        val path = Path.of(katalog)
        if (path.exists()) {
            Files.isWritable(path)
        } else {
            false
        }
    }
    skrivMiljoRapporter(rader)
    embeddedServer(CIO, port = miljo.port) {
        module(miljo)
    }.start(wait = true)
}
