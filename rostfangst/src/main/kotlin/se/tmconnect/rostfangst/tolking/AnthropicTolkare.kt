package se.tmconnect.rostfangst.tolking

import org.json.JSONArray
import org.json.JSONObject
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.tolkaSvar

class AnthropicTolkare(
    private val apiNyckel: String,
    private val modell: String,
    private val transport: HttpTransport = HttpURLConnectionTransport(),
) : Tolkare {
    override fun tolka(text: String, systemprompt: String): Result<Forslag> {
        if (apiNyckel.isBlank()) {
            return Result.failure(TolkningMisslyckades("saknar_api_nyckel"))
        }
        val body = JSONObject()
            .put("model", modell)
            .put("max_tokens", 1024)
            .put("system", systemprompt)
            .put(
                "messages",
                JSONArray().put(
                    JSONObject()
                        .put("role", "user")
                        .put("content", text),
                ),
            )
            .toString()
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
        return Result.success(tolkaSvar(jsonText, text))
    }

    private fun extraheraText(kropp: String): String? {
        return try {
            val root = JSONObject(kropp)
            val content = root.optJSONArray("content") ?: return null
            if (content.length() == 0) return null
            val block = content.getJSONObject(0)
            block.optString("text").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
