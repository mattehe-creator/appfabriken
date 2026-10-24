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
import se.tmconnect.styrelserummet.regler.CSV_BOM
import se.tmconnect.styrelserummet.regler.avskrivningsplanCsv
import se.tmconnect.styrelserummet.regler.avskrivningsplanPerGrupp
import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.tolking.anthropicTolkareFranMiljo
import java.nio.file.Files
import java.nio.file.Path
import java.time.Year

data class ServerMiljo(
    val dataRot: Path,
    val port: Int,
    val tolkare: Tolkare,
    val synkronJobb: Boolean = false,
    val proxyPaa: Boolean = proxyPaaFranMiljo(),
    val betalning: BetalKonfig = betalKonfigFranMiljo(),
    val betalare: Betalare? = null,
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
    val verktygGrans = VerktygAnropsGrans()
    val scope = CoroutineScope(SupervisorJob())
    val ko = JobbKo(lagring, miljo.tolkare, scope = scope, synkron = miljo.synkronJobb)
    rensaGamlaJobb(lagring)
    val betalKonfig = miljo.betalning
    val betalare: Betalare? = miljo.betalare ?: if (betalKonfig.aktiv) StripeBetalare(betalKonfig) else null
    val publikBas = publikBasUrl(betalKonfig)

    /** Har kunden betalat men inte kommit tillbaka till sidan? Fråga Stripe en gång per hämtning. */
    fun medBetalning(meta: JobbMeta): JobbMeta {
        val kassa = meta.kassaId
        if (!betalKonfig.aktiv || meta.betald || kassa == null || betalare == null) return meta
        return when (betalare.kontrollera(kassa, meta.id)) {
            KassaStatus.Betald -> meta.copy(betald = true).also { lagring.skrivMeta(it) }
            else -> meta
        }
    }

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
        exception<VerktygFel> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("fel" to (cause.message ?: felText(Felkoder.INTERNT_FEL))))
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

        get("/robots.txt") {
            call.respondText(robotsTxt(publikBas), ContentType.Text.Plain)
        }

        get("/sitemap.xml") {
            val sitemap = sitemapXml(publikBas)
            if (sitemap == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respondText(sitemap.second, ContentType.Application.Xml)
            }
        }

        get("/nyckeltal") {
            call.respondText(
                javaClass.classLoader.getResource("static/nyckeltal.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        get("/fondplan") {
            call.respondText(
                javaClass.classLoader.getResource("static/fondplan.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        post("/api/nyckeltal") {
            kontrolleraVerktygGrans(call, verktygGrans, miljo.proxyPaa)
            val raw = call.taEmotVerktygJson()
            val req = tolkaVerktygJson<NyckeltalRequest>(raw)
            call.respond(beraknaNyckeltalSvar(req))
        }

        get("/protokoll") {
            call.respondText(
                javaClass.classLoader.getResource("static/protokoll.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        get("/avgift") {
            call.respondText(
                javaClass.classLoader.getResource("static/avgift.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        get("/stamma") {
            call.respondText(
                javaClass.classLoader.getResource("static/stamma.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        post("/api/stamma") {
            kontrolleraVerktygGrans(call, verktygGrans, miljo.proxyPaa)
            val raw = call.taEmotVerktygJson()
            val req = tolkaVerktygJson<StammaRequest>(raw)
            call.respond(beraknaStammaSvar(req))
        }

        post("/api/avgift") {
            kontrolleraVerktygGrans(call, verktygGrans, miljo.proxyPaa)
            val raw = call.taEmotVerktygJson()
            val req = tolkaVerktygJson<AvgiftRequest>(raw)
            call.respond(beraknaAvgiftSvar(req))
        }

        get("/rantor") {
            call.respondText(
                javaClass.classLoader.getResource("static/rantor.html")!!.readText(),
                ContentType.Text.Html,
            )
        }

        post("/api/rantor") {
            kontrolleraVerktygGrans(call, verktygGrans, miljo.proxyPaa)
            val raw = call.taEmotVerktygJson()
            val req = tolkaVerktygJson<RantorRequest>(raw)
            call.respond(beraknaRantorSvar(req))
        }

        post("/api/fondplan") {
            kontrolleraVerktygGrans(call, verktygGrans, miljo.proxyPaa)
            val raw = call.taEmotVerktygJson()
            val req = tolkaVerktygJson<FondplanRequest>(raw)
            call.respond(beraknaFondplanSvar(req))
        }

        get("/api/betalning") {
            call.respond(BetalStatus(aktiv = betalKonfig.aktiv, betald = false, prisKr = betalKonfig.prisOre / 100))
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
            if (!farLamnaUt(betalKonfig, medBetalning(meta))) {
                return@get call.respondText(
                    betalningKravsHtml(id),
                    ContentType.Text.Html,
                    HttpStatusCode.PaymentRequired,
                )
            }
            lagring.uppdateraAktivitet(id)
            val visning = tillVisning(utkast)
            call.respondText(
                utskriftHtml(
                    meta.forening,
                    utkast.arsslut,
                    visning.utskrift,
                    planForUtkast(utkast),
                    avskrivningsplanPerGrupp(utkast.komponenter.map { franDto(it) }, utkast.arsslut + 1, PLAN_ANTAL_AR),
                ),
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

                // Jobbet finns redan på disk; tas bort om något går fel innan det är köat.
                val ar = try {
                    val multipart = call.receiveMultipart(formFieldLimit = MAX_BYTES_PER_JOBB.toLong() + 1)
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
                                // Läs aldrig mer än det som får plats i jobbet, så att en stor fil inte fyller minnet.
                                val kvar = MAX_BYTES_PER_JOBB - totalBytes
                                val bytes = part.streamProvider().use { lasHogst(it, kvar) }
                                totalBytes += bytes?.size?.toLong() ?: (kvar + 1)
                                if (bytes == null || totalBytes > MAX_BYTES_PER_JOBB) {
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
                                lagring.sparaFil(id, lagringsnamn(filAntal + 1, namn), bytes)
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
                    val giltigtAr = valideraArsslut(arsslutText, Year.now().value)
                        ?: run {
                            lagring.raderaJobb(id)
                            throw FelBegaran(Felkoder.ARSSLUT_SAKNAS)
                        }
                    if (filAntal == 0) {
                        lagring.raderaJobb(id)
                        throw FelBegaran(Felkoder.INGA_PDF)
                    }
                    giltigtAr
                } catch (fel: Throwable) {
                    lagring.raderaJobb(id)
                    throw fel
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
                val csvMeta = lagring.lasMeta(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (!farLamnaUt(betalKonfig, medBetalning(csvMeta))) {
                    return@get call.respond(
                        HttpStatusCode.PaymentRequired,
                        mapOf("fel" to felText(Felkoder.BETALNING_KRAVS)),
                    )
                }
                lagring.uppdateraAktivitet(id)
                val csv = tillCsv(utkast.komponenter, utkast.arsslut)
                call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"komponentlista.csv\"")
                call.respondText(CSV_BOM + csv, ContentType.parse("text/csv; charset=utf-8"))
            }

            get("/{id}/atgarder") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val utkast = lagring.lasUtkast(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                lagring.uppdateraAktivitet(id)
                call.respond(JobbAtgarder(franAr = utkast.arsslut + 1, atgarder = utkast.atgarder))
            }

            get("/{id}/plan.csv") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val utkast = lagring.lasUtkast(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                val planMeta = lagring.lasMeta(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (!farLamnaUt(betalKonfig, medBetalning(planMeta))) {
                    return@get call.respond(
                        HttpStatusCode.PaymentRequired,
                        mapOf("fel" to felText(Felkoder.BETALNING_KRAVS)),
                    )
                }
                lagring.uppdateraAktivitet(id)
                val csv = avskrivningsplanCsv(planForUtkast(utkast))
                call.response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"avskrivningsplan.csv\"")
                call.respondText(CSV_BOM + csv, ContentType.parse("text/csv; charset=utf-8"))
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                lagring.uppdateraAktivitet(id)
                val utkast = lagring.lasUtkast(id)
                val aktuell = medBetalning(meta)
                call.respond(jobbResponse(aktuell, utkast, betalStatus(betalKonfig, aktuell)))
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
                call.respond(jobbResponse(meta, nytt, betalStatus(betalKonfig, meta)))
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

            post("/{id}/kassa") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (meta.status != JobbStatus.KLAR.name) throw FelBegaran(Felkoder.JOBB_EJ_KLART)
                if (!betalKonfig.aktiv || betalare == null) {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("fel" to felText(Felkoder.BETALNING_EJ_AKTIV)))
                }
                val aktuell = medBetalning(meta)
                if (aktuell.betald) {
                    return@post call.respond(mapOf("betald" to "true"))
                }
                val kassa = betalare.skapaKassa(id)
                    ?: return@post call.respond(
                        HttpStatusCode.BadGateway,
                        mapOf("fel" to felText(Felkoder.BETALNING_MISSLYCKADES)),
                    )
                lagring.skrivMeta(aktuell.copy(kassaId = kassa.id, senastAktivMs = System.currentTimeMillis()))
                call.respond(mapOf("url" to kassa.url))
            }

            post("/{id}/bekrafta") {
                val id = call.parameters["id"] ?: throw FelBegaran(Felkoder.SAKNAR_JOBB_ID)
                if (!arGiltigtJobbId(id)) throw FelBegaran(Felkoder.OGILIG_LANK)
                val meta = lagring.lasMeta(id)
                    ?: return@post call.respond(HttpStatusCode.NotFound, mapOf("fel" to felText(Felkoder.JOBB_SAKNAS)))
                if (!betalKonfig.aktiv || betalare == null) {
                    return@post call.respond(HttpStatusCode.Conflict, mapOf("fel" to felText(Felkoder.BETALNING_EJ_AKTIV)))
                }
                val body = call.receive<BekraftaRequest>()
                if (!arGiltigtKassaId(body.kassa)) throw FelBegaran(Felkoder.BETALNING_MISSLYCKADES)
                val status = if (meta.betald) KassaStatus.Betald else betalare.kontrollera(body.kassa, id)
                if (status != KassaStatus.Betald) {
                    return@post call.respond(
                        HttpStatusCode.PaymentRequired,
                        mapOf("fel" to felText(Felkoder.BETALNING_MISSLYCKADES)),
                    )
                }
                val ny = meta.copy(betald = true, kassaId = body.kassa)
                lagring.skrivMeta(ny)
                call.respond(jobbResponse(ny, lagring.lasUtkast(id), betalStatus(betalKonfig, ny)))
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

fun main() {
    val miljo = miljoFranEnv()
    val env = System.getenv()
    val rader = kontrolleraMiljo(env) { katalog ->
        val path = Path.of(katalog)
        if (Files.exists(path)) {
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
