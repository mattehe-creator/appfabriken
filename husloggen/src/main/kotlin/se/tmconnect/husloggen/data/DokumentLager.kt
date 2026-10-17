package se.tmconnect.husloggen.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID
import se.tmconnect.husloggen.regler.DOKUMENT_MAX_FIL_BYTES
import se.tmconnect.husloggen.regler.dokumentFilStorlekTillaten
import se.tmconnect.husloggen.regler.dokumentMimeTillaten

sealed interface KopieraDokumentResult {
    data class Ok(val filnamn: String, val mimeTyp: String) : KopieraDokumentResult

    data object ForStor : KopieraDokumentResult

    data object OgiltigTyp : KopieraDokumentResult

    data object Misslyckades : KopieraDokumentResult
}

class DokumentLager(
    private val context: Context,
    private val fileProviderAuthority: String,
) {
    private val dokumentKatalog: File
        get() = File(context.filesDir, KATALOG_NAMN).also { it.mkdirs() }

    fun kopieraFranUri(kallaUri: Uri): KopieraDokumentResult {
        val mimeTyp = context.contentResolver.getType(kallaUri) ?: return KopieraDokumentResult.OgiltigTyp
        if (!dokumentMimeTillaten(mimeTyp)) {
            return KopieraDokumentResult.OgiltigTyp
        }
        val storlek = hamtaStorlek(kallaUri) ?: return KopieraDokumentResult.Misslyckades
        if (!dokumentFilStorlekTillaten(storlek)) {
            return if (storlek > DOKUMENT_MAX_FIL_BYTES) {
                KopieraDokumentResult.ForStor
            } else {
                KopieraDokumentResult.Misslyckades
            }
        }
        val andelse = filandelseFranMime(mimeTyp)
        val filnamn = "${UUID.randomUUID()}.$andelse"
        return try {
            context.contentResolver.openInputStream(kallaUri)?.use { input ->
                File(dokumentKatalog, filnamn).outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return KopieraDokumentResult.Misslyckades
            KopieraDokumentResult.Ok(filnamn, mimeTyp)
        } catch (_: Exception) {
            File(dokumentKatalog, filnamn).delete()
            KopieraDokumentResult.Misslyckades
        }
    }

    fun innehallUriForFilnamn(filnamn: String): Uri {
        val fil = File(dokumentKatalog, filnamn)
        return FileProvider.getUriForFile(context, fileProviderAuthority, fil)
    }

    fun taBort(filnamn: String?) {
        if (filnamn.isNullOrBlank()) {
            return
        }
        File(dokumentKatalog, filnamn).delete()
    }

    fun filFinns(filnamn: String): Boolean = File(dokumentKatalog, filnamn).isFile

    fun lasRåBytes(filnamn: String): ByteArray? {
        val fil = File(dokumentKatalog, filnamn)
        if (!fil.isFile) {
            return null
        }
        return fil.readBytes()
    }

    fun sparaFranBytes(innehall: ByteArray, ursprungligtFilnamn: String): String {
        val delar = ursprungligtFilnamn.substringAfterLast('.', missingDelimiterValue = "")
        val andelse = delar.ifEmpty { "bin" }.take(10)
        val filnamn = "${UUID.randomUUID()}.$andelse"
        File(dokumentKatalog, filnamn).writeBytes(innehall)
        return filnamn
    }

    private fun hamtaStorlek(uri: Uri): Long? =
        try {
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
        } catch (_: Exception) {
            null
        }

    private fun filandelseFranMime(mimeTyp: String): String =
        when (mimeTyp) {
            "application/pdf" -> "pdf"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> mimeTyp.substringAfterLast('/', "bin").take(10).ifEmpty { "bin" }
        }

    companion object {
        const val KATALOG_NAMN = "dokument"
    }
}

fun skapaHusDokumentLager(context: Context): DokumentLager =
    DokumentLager(
        context = context,
        fileProviderAuthority = "${context.packageName}.bildfil",
    )
