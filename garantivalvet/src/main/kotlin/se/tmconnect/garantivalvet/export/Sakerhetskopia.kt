package se.tmconnect.garantivalvet.export

import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KvittoLager
import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

sealed interface SakerhetskopiaLasResult {
    data class Ok(
        val innehall: SakerhetskopiaInnehall,
        val kvittoFiler: Map<String, ByteArray>,
    ) : SakerhetskopiaLasResult

    enum class Fel : SakerhetskopiaLasResult {
        OkandVersion,
        TrasigFil,
        SaknarKopJson,
    }
}

object Sakerhetskopia {
    fun skriv(
        outputStream: OutputStream,
        kop: List<Kop>,
        kvittoLager: KvittoLager,
        skapad: String = Instant.now().toString(),
    ) {
        if (kop.size > SAKERHETSKOPIA_MAX_KOP) {
            error("För många köp för säkerhetskopia")
        }
        val exportKop = kop.map { kopTillSakerhetskopia(it) }
        val innehall =
            SakerhetskopiaInnehall(
                version = SAKERHETSKOPIA_VERSION,
                skapad = skapad,
                kop = exportKop,
            )
        val json = skrivSakerhetskopiaJson(innehall)
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zip ->
            zip.putNextEntry(ZipEntry(SAKERHETSKOPIA_KOP_JSON))
            zip.write(json.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            for (k in exportKop) {
                val filnamn = k.kvittoFil ?: continue
                val bytes = kvittoLager.lasRåBytes(filnamn) ?: continue
                if (bytes.size > SAKERHETSKOPIA_MAX_FIL_BYTES) {
                    continue
                }
                val postNamn = SAKERHETSKOPIA_KVITTON_MAPP + filnamn
                zip.putNextEntry(ZipEntry(postNamn))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
    }

    fun lasIn(inputStream: InputStream): SakerhetskopiaLasResult {
        var jsonBytes: ByteArray? = null
        val kvittoFiler = linkedMapOf<String, ByteArray>()
        var zipFel: SakerhetskopiaLasResult.Fel? = null
        return try {
            ZipInputStream(inputStream).use { zip ->
                while (zipFel == null) {
                    val entry = zip.nextEntry ?: break
                    val namn = entry.name
                    if (!arSakerZipPost(namn)) {
                        zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                        break
                    }
                    if (entry.size > SAKERHETSKOPIA_MAX_FIL_BYTES && entry.size != -1L) {
                        zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                        break
                    }
                    val bytes = lasBegransadeBytes(zip, SAKERHETSKOPIA_MAX_FIL_BYTES)
                    when {
                        namn == SAKERHETSKOPIA_KOP_JSON -> jsonBytes = bytes
                        namn.startsWith(SAKERHETSKOPIA_KVITTON_MAPP) -> {
                            val filnamn = namn.removePrefix(SAKERHETSKOPIA_KVITTON_MAPP)
                            if (filnamn.isEmpty() || filnamn.contains('/')) {
                                zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                                break
                            }
                            kvittoFiler[filnamn] = bytes
                        }
                    }
                    zip.closeEntry()
                }
            }
            zipFel?.let { return it }
            val json = jsonBytes ?: return SakerhetskopiaLasResult.Fel.SaknarKopJson
            when (val jsonResult = lasSakerhetskopiaJson(String(json, Charsets.UTF_8))) {
                is SakerhetskopiaJsonLasResult.Ok ->
                    SakerhetskopiaLasResult.Ok(jsonResult.innehall, kvittoFiler)
                is SakerhetskopiaJsonLasResult.Fel.OkandVersion ->
                    SakerhetskopiaLasResult.Fel.OkandVersion
                is SakerhetskopiaJsonLasResult.Fel.TrasigJson ->
                    SakerhetskopiaLasResult.Fel.TrasigFil
            }
        } catch (_: Exception) {
            SakerhetskopiaLasResult.Fel.TrasigFil
        }
    }
}

internal fun arSakerZipPost(namn: String): Boolean {
    if (namn.contains("..")) {
        return false
    }
    if (namn.startsWith("/") || namn.startsWith("\\")) {
        return false
    }
    if (Regex("""^[A-Za-z]:[/\\]""").containsMatchIn(namn)) {
        return false
    }
    return true
}

private fun lasBegransadeBytes(input: InputStream, maxBytes: Int): ByteArray {
    val buffer = ByteArrayOutputStreamLimited(maxBytes)
    val chunk = ByteArray(8192)
    while (true) {
        val las = input.read(chunk)
        if (las <= 0) {
            break
        }
        buffer.write(chunk, 0, las)
    }
    return buffer.toByteArray()
}

private class ByteArrayOutputStreamLimited(private val maxBytes: Int) {
    private val data = ArrayList<Byte>(256)
    private var size = 0

    fun write(buf: ByteArray, offset: Int, length: Int) {
        if (size + length > maxBytes) {
            throw IllegalStateException("Filen är för stor")
        }
        for (i in offset until offset + length) {
            data.add(buf[i])
        }
        size += length
    }

    fun toByteArray(): ByteArray = ByteArray(size) { data[it] }
}
