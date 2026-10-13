package se.tmconnect.garantivalvet.export

import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KvittoLager
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
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
        
        // Först läs kvittobytes för varje köp och avgör om de ska inkluderas
        val kopMedKvitto = kop.map { originalKop ->
            val exportKop0 = kopTillSakerhetskopia(originalKop)
            val exportKop = exportKop0.copy(
                felBilder = exportKop0.felBilder.filter { filnamn ->
                    val bytes = kvittoLager.lasRåBytes(filnamn)
                    bytes != null && bytes.size <= SAKERHETSKOPIA_MAX_FIL_BYTES
                },
            )
            if (exportKop.kvittoFil != null) {
                val bytes = kvittoLager.lasRåBytes(exportKop.kvittoFil)
                if (bytes == null || bytes.size > SAKERHETSKOPIA_MAX_FIL_BYTES) {
                    // Om kvittot saknas eller är för stort, ta bort referensen till det
                    exportKop.copy(kvittoFil = null)
                } else {
                    exportKop
                }
            } else {
                exportKop
            }
        }
        
        val innehall =
            SakerhetskopiaInnehall(
                version = SAKERHETSKOPIA_VERSION,
                skapad = skapad,
                kop = kopMedKvitto,
            )
        val json = skrivSakerhetskopiaJson(innehall)
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zip ->
            zip.putNextEntry(ZipEntry(SAKERHETSKOPIA_KOP_JSON))
            zip.write(json.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            for (k in kopMedKvitto) {
                for (filnamn in listOfNotNull(k.kvittoFil) + k.felBilder) {
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
    val buffer = ByteArrayOutputStream()
    val chunk = ByteArray(8192)
    var totalSize = 0
    while (true) {
        val las = input.read(chunk)
        if (las <= 0) {
            break
        }
        totalSize += las
        if (totalSize > maxBytes) {
            throw IllegalStateException("Filen är för stor")
        }
        buffer.write(chunk, 0, las)
    }
    return buffer.toByteArray()
}
