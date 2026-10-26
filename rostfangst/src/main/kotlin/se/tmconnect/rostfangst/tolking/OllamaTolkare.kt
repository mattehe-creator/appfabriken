package se.tmconnect.rostfangst.tolking

import java.io.IOException
import java.net.SocketTimeoutException
import se.tmconnect.rostfangst.lokal.byggOllamaChat
import se.tmconnect.rostfangst.lokal.lasOllamaSvar
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import se.tmconnect.rostfangst.regler.tolkaSvar

/**
 * Tolkar text med en Ollama-server som användaren själv anger (adress, modellnamn och valfri bearer-token).
 * Orsakerna i [TolkningMisslyckades] är `saknar_adress`, `saknar_modell`, `natfel`, `timeout`,
 * `http_<kod>` och `saknar_svar`.
 */
class OllamaTolkare(
    private val adress: String,
    private val modell: String,
    private val bearerToken: String? = null,
    private val transport: HttpTransport = HttpURLConnectionTransport(),
) : Tolkare {
    override fun tolka(text: String, systemprompt: String): Result<Forslag> {
        if (adress.isBlank()) return Result.failure(TolkningMisslyckades("saknar_adress"))
        if (modell.isBlank()) return Result.failure(TolkningMisslyckades("saknar_modell"))
        val body = byggOllamaChat(modell, systemprompt, text, ForslagTyp.values().map { it.name })
        val headers = mutableMapOf("Content-Type" to "application/json")
        if (!bearerToken.isNullOrBlank()) headers["Authorization"] = "Bearer $bearerToken"
        val svar = try {
            transport.post(adress.trim().trimEnd('/') + "/api/chat", headers, body)
        } catch (_: SocketTimeoutException) {
            return Result.failure(TolkningMisslyckades("timeout"))
        } catch (_: IOException) {
            return Result.failure(TolkningMisslyckades("natfel"))
        }
        if (svar.statusKod !in 200..299) {
            return Result.failure(TolkningMisslyckades("http_${svar.statusKod}"))
        }
        val jsonText = lasOllamaSvar(svar.kropp)
            ?: return Result.failure(TolkningMisslyckades("saknar_svar"))
        return Result.success(tolkaSvar(jsonText, text))
    }
}
