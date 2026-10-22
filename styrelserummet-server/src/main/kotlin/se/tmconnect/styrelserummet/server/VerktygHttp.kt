package se.tmconnect.styrelserummet.server

import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.contentLength
import io.ktor.server.request.receiveText
import kotlinx.serialization.json.Json

class ServerFelBegaran(val fel: ServerFel) : Exception()

val verktygJson = Json { ignoreUnknownKeys = true }

suspend fun ApplicationCall.taEmotVerktygJson(): String {
    val langd = request.contentLength()
    if (langd != null && langd > MAX_VERKTYG_KROPP_BYTES) {
        throw ServerFelBegaran(ServerFel.ForStorVerktygsforfragan)
    }
    val raw = receiveText()
    if (raw.toByteArray(Charsets.UTF_8).size > MAX_VERKTYG_KROPP_BYTES) {
        throw ServerFelBegaran(ServerFel.ForStorVerktygsforfragan)
    }
    return raw
}

inline fun <reified T> tolkaVerktygJson(raw: String): T =
    runCatching { verktygJson.decodeFromString<T>(raw) }
        .getOrElse { throw VerktygFel("Uppgifterna kunde inte läsas.") }

fun kontrolleraVerktygGrans(
    call: ApplicationCall,
    grans: VerktygAnropsGrans,
    proxyPaa: Boolean,
) {
    val ip = call.klientIp(proxyPaa)
    val nuMs = System.currentTimeMillis()
    if (!grans.farAnropa(ip, nuMs)) {
        throw ServerFelBegaran(ServerFel.ForMangaVerktygAnrop)
    }
    grans.registreraAnrop(ip, nuMs)
}
