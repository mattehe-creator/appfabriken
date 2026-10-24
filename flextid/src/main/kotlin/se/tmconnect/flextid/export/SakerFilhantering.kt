package se.tmconnect.flextid.export

import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.cancellation.CancellationException

/**
 * Öppnar och skriver en fil från systemets filväljare. Sant bara när allt skrevs.
 * Fel från filleverantören (borttagen fil, molnmapp utan nät, nekad åtkomst) ger falskt
 * i stället för att krascha appen.
 */
suspend fun skrivSakert(
    oppna: () -> OutputStream?,
    skriv: suspend (OutputStream) -> Unit,
): Boolean =
    try {
        val strom = oppna()
        if (strom == null) {
            false
        } else {
            strom.use { skriv(it) }
            true
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

/** Läser en fil från systemets filväljare; null om den inte gick att öppna eller läsa. */
suspend fun lasSakert(
    oppna: () -> InputStream?,
    las: suspend (InputStream) -> String,
): String? =
    try {
        oppna()?.use { las(it) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
