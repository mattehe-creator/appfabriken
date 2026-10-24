package se.tmconnect.styrelserummet.tolking

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AnthropicTolkare(
    private val apiNyckel: String,
    private val modell: String,
    private val transport: HttpTransport = JavaHttpTransport(),
    private val promptLaddare: PromptLaddare = PromptLaddare(),
    /** Väntetid före nytt försök efter 429, 500, 503 eller 529 (överbelastad). */
    private val vantaFore: (forsok: Int) -> Unit = { forsok -> Thread.sleep(2_000L * forsok * forsok) },
) : Tolkare {
    override fun foreslaKomponenter(
        underhallsplanUtdrag: String,
        sidor: List<Int>,
        filnamn: String,
    ): Result<String> {
        if (apiNyckel.isBlank()) {
            return Result.failure(TolkningMisslyckades("saknar_api_nyckel"))
        }
        val system = promptLaddare.las("underhallsplan_system.txt")
        val mall = promptLaddare.las("underhallsplan_anvandare.txt")
        val sidtext = sidor.joinToString(", ") { it.toString() }
        val user = mall
            .replace("{{FILNAMN}}", filnamn)
            .replace("{{SIDOR}}", sidtext)
            .replace("{{TEXT}}", underhallsplanUtdrag)
        val body = buildJsonObject {
            put("model", JsonPrimitive(modell))
            put("max_tokens", JsonPrimitive(MAX_SVAR_TOKENS))
            put("system", JsonPrimitive(system))
            put(
                "messages",
                buildJsonArray {
                    add(
                        buildJsonObject {
                            put("role", JsonPrimitive("user"))
                            put("content", JsonPrimitive(user))
                        },
                    )
                },
            )
        }.toString()
        var forsok = 0
        while (true) {
            forsok++
            val svar =
                try {
                    transport.post(
                        url = "https://api.anthropic.com/v1/messages",
                        headers = mapOf(
                            "Content-Type" to "application/json",
                            "x-api-key" to apiNyckel,
                            "anthropic-version" to "2023-06-01",
                        ),
                        body = body,
                    )
                } catch (e: java.net.http.HttpTimeoutException) {
                    return Result.failure(TolkningMisslyckades("tidsgrans"))
                } catch (e: java.io.IOException) {
                    if (forsok < MAX_FORSOK) {
                        vantaFore(forsok)
                        continue
                    }
                    return Result.failure(TolkningMisslyckades("natverk"))
                }
            if (svar.statusKod in TILLFALLIGA_FEL && forsok < MAX_FORSOK) {
                vantaFore(forsok)
                continue
            }
            if (svar.statusKod !in 200..299) {
                return Result.failure(TolkningMisslyckades("http_${svar.statusKod}"))
            }
            if (stoppOrsak(svar.kropp) == "max_tokens") {
                // Svaret klipptes av mitt i JSON:en; att tolka det vore att gissa.
                return Result.failure(TolkningMisslyckades("svaret_for_langt"))
            }
            val jsonText = extraheraText(svar.kropp)
                ?: return Result.failure(TolkningMisslyckades("saknar_svar"))
            return Result.success(jsonText)
        }
    }

    private fun stoppOrsak(kropp: String): String? =
        runCatching {
            Json.parseToJsonElement(kropp).jsonObject["stop_reason"]?.jsonPrimitive?.contentOrNull
        }.getOrNull()

    /** All text i svaret, i ordning; block av andra typer hoppas över. */
    private fun extraheraText(kropp: String): String? {
        return try {
            val root = Json.parseToJsonElement(kropp).jsonObject
            val content = root["content"]?.jsonArray ?: return null
            content
                .mapNotNull { block ->
                    val obj = block.jsonObject
                    if (obj["type"]?.jsonPrimitive?.contentOrNull != "text") return@mapNotNull null
                    obj["text"]?.jsonPrimitive?.contentOrNull
                }
                .joinToString("")
                .takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        const val MAX_SVAR_TOKENS = 16_000
        const val MAX_FORSOK = 3
        val TILLFALLIGA_FEL = setOf(429, 500, 503, 529)
    }
}

class PromptLaddare {
    fun las(filnamn: String): String {
        val stream = PromptLaddare::class.java.classLoader.getResourceAsStream("prompter/$filnamn")
            ?: error("Saknar promptfil prompter/$filnamn")
        return stream.bufferedReader().use { it.readText() }
    }
}

fun anthropicTolkareFranMiljo(transport: HttpTransport = JavaHttpTransport()): AnthropicTolkare {
    val nyckel = System.getenv("ANTHROPIC_API_KEY").orEmpty()
    val modell = System.getenv("STYRELSERUMMET_MODELL")?.takeIf { it.isNotBlank() } ?: "claude-sonnet-5-5"
    return AnthropicTolkare(nyckel, modell, transport)
}
