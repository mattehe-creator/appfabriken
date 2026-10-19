package se.tmconnect.styrelserummet.underlag

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

data class HttpSvar(val statusKod: Int, val kropp: String)

fun interface HttpTransport {
    fun post(url: String, headers: Map<String, String>, body: String): HttpSvar
}

class HttpClientTransport : HttpTransport {
    private val client = java.net.http.HttpClient.newBuilder()
        .connectTimeout(java.time.Duration.ofSeconds(30))
        .build()

    override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
        val builder = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .timeout(java.time.Duration.ofSeconds(90))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
        headers.forEach { (k, v) -> builder.header(k, v) }
        val svar = client.send(builder.build(), java.net.http.HttpResponse.BodyHandlers.ofString())
        return HttpSvar(svar.statusCode(), svar.body())
    }
}

class AnthropicTolkare(
    private val apiNyckel: String,
    private val modell: String,
    private val transport: HttpTransport = HttpClientTransport(),
    private val systemprompt: String,
) : Tolkare {
    override fun foreslaKomponenter(underhallsplanText: String, filnamn: String): Result<TolkningsForslag> {
        if (apiNyckel.isBlank()) {
            return Result.failure(TolkningMisslyckades("API-nyckel saknas."))
        }
        val body = buildJsonObject {
            put("model", modell)
            put("max_tokens", 4096)
            put("system", systemprompt)
            putJsonArray("messages") {
                add(
                    buildJsonObject {
                        put("role", "user")
                        put("content", "Fil: $filnamn\n\n$underhallsplanText")
                    },
                )
            }
        }.toString()
        val svar = transport.post(
            url = "https://api.anthropic.com/v1/messages",
            headers = mapOf(
                "Content-Type" to "application/json",
                "x-api-key" to apiNyckel,
                "anthropic-version" to "2023-06-01",
            ),
            body = body,
        )
        if (svar.statusKod !in 200..299) {
            return Result.failure(TolkningMisslyckades("Tolkningen misslyckades (HTTP ${svar.statusKod})."))
        }
        val text = extraheraText(svar.kropp)
            ?: return Result.failure(TolkningMisslyckades("Tolkningen gav inget svar."))
        val resultat = tolkaForslag(text, filnamn)
        return Result.success(
            TolkningsForslag(resultat.komponenter, resultat.anmarkningar),
        )
    }

    private fun extraheraText(kropp: String): String? {
        return try {
            val root = Json.parseToJsonElement(kropp) as JsonObject
            val content = root["content"] as? JsonArray ?: return null
            if (content.isEmpty()) return null
            val block = content[0] as JsonObject
            block["text"]?.toString()?.trim('"')?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
