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
            put("max_tokens", JsonPrimitive(8192))
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
            return Result.failure(TolkningMisslyckades("http_${svar.statusKod}"))
        }
        val jsonText = extraheraText(svar.kropp)
            ?: return Result.failure(TolkningMisslyckades("saknar_svar"))
        return Result.success(jsonText)
    }

    private fun extraheraText(kropp: String): String? {
        return try {
            val root = Json.parseToJsonElement(kropp).jsonObject
            val content = root["content"]?.jsonArray ?: return null
            if (content.isEmpty()) return null
            val block = content[0].jsonObject
            block["text"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
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
