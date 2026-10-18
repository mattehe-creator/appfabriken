package se.tmconnect.husloggen.export

import java.io.BufferedOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.data.DokumentLager
import se.tmconnect.karna.bild.BildLager
import se.tmconnect.karna.sakerhetskopia.arSakerZipPost
import se.tmconnect.karna.sakerhetskopia.lasBegransadeBytes

sealed interface SakerhetskopiaLasResult {
    data class Ok(
        val innehall: SakerhetskopiaInnehall,
        val bildFiler: Map<String, ByteArray>,
        val dokumentFiler: Map<String, ByteArray> = emptyMap(),
    ) : SakerhetskopiaLasResult

    enum class Fel : SakerhetskopiaLasResult {
        OkandVersion,
        TrasigFil,
        SaknarLoggJson,
    }
}

object Sakerhetskopia {
    fun skriv(
        outputStream: OutputStream,
        bostader: List<Bostad>,
        uppgifter: List<Uppgift>,
        poster: List<Post>,
        matare: List<Matare>,
        avlasningar: List<Avlasning>,
        dokument: List<Dokument>,
        planeradeKostnader: List<PlaneradKostnad>,
        bildLager: BildLager,
        dokumentLager: DokumentLager,
        skapad: String = Instant.now().toString(),
    ) {
        if (bostader.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_BOSTADER ||
            uppgifter.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_UPPGIFTER ||
            poster.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_POSTER ||
            matare.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_MATARE ||
            avlasningar.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_AVLASNINGAR ||
            dokument.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_DOKUMENT ||
            planeradeKostnader.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_PLANERADE_KOSTNADER
        ) {
            error("För många rader för säkerhetskopia")
        }
        val sorteradeBostader = bostader.sortedBy { it.id }
        val bostadIdTillIndex = sorteradeBostader.mapIndexed { index, b -> b.id to index }.toMap()
        val exportBostader = sorteradeBostader.map { bostadTillSakerhetskopia(it) }
        val sorteradeUppgifter = uppgifter.sortedBy { it.id }
        val uppgiftIdTillIndex = sorteradeUppgifter.mapIndexed { index, u -> u.id to index }.toMap()
        val exportUppgifter = sorteradeUppgifter.map { uppgiftTillSakerhetskopia(it, bostadIdTillIndex) }
        val exportPoster =
            poster.sortedBy { it.id }.map { post ->
                val export = postTillSakerhetskopia(post, uppgiftIdTillIndex, bostadIdTillIndex)
                export.copy(
                    bilder = export.bilder.filter { filnamn ->
                        val bytes = bildLager.lasRåBytes(filnamn)
                        bytes != null && bytes.size <= HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES
                    },
                )
            }
        val sorteradeMatare = matare.sortedBy { it.id }
        val matareIdTillIndex = sorteradeMatare.mapIndexed { index, m -> m.id to index }.toMap()
        val exportMatare = sorteradeMatare.map { matareTillSakerhetskopia(it, bostadIdTillIndex) }
        val exportAvlasningar =
            avlasningar
                .sortedBy { it.id }
                .map { avlasningTillSakerhetskopia(it, matareIdTillIndex) }
        val exportDokument = dokument.sortedBy { it.id }.map { dokumentTillSakerhetskopia(it, bostadIdTillIndex) }
        val exportPlanerade =
            planeradeKostnader.sortedBy { it.id }.map {
                planeradKostnadTillSakerhetskopia(it, bostadIdTillIndex)
            }
        val innehall =
            SakerhetskopiaInnehall(
                version = HUSLOGGEN_SAKERHETSKOPIA_VERSION,
                skapad = skapad,
                bostader = exportBostader,
                uppgifter = exportUppgifter,
                poster = exportPoster,
                matare = exportMatare,
                avlasningar = exportAvlasningar,
                dokument = exportDokument,
                planeradeKostnader = exportPlanerade,
            )
        val json = skrivSakerhetskopiaJson(innehall)
        val bilderAttSkriva = exportPoster.flatMap { it.bilder }.distinct()
        val dokumentAttSkriva = exportDokument.map { it.filnamn }.distinct()
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zip ->
            zip.putNextEntry(ZipEntry(HUSLOGGEN_LOGG_JSON))
            zip.write(json.toByteArray(Charsets.UTF_8))
            zip.closeEntry()
            for (filnamn in bilderAttSkriva) {
                val bytes = bildLager.lasRåBytes(filnamn) ?: continue
                if (bytes.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES) continue
                zip.putNextEntry(ZipEntry(HUSLOGGEN_BILDER_MAPP + filnamn))
                zip.write(bytes)
                zip.closeEntry()
            }
            for (filnamn in dokumentAttSkriva) {
                val bytes = dokumentLager.lasRåBytes(filnamn) ?: continue
                if (bytes.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES) continue
                zip.putNextEntry(ZipEntry(HUSLOGGEN_DOKUMENT_MAPP + filnamn))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
    }

    fun lasIn(inputStream: InputStream): SakerhetskopiaLasResult {
        var jsonBytes: ByteArray? = null
        val bildFiler = linkedMapOf<String, ByteArray>()
        val dokumentFiler = linkedMapOf<String, ByteArray>()
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
                    if (entry.size > HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES && entry.size != -1L) {
                        zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                        break
                    }
                    val bytes = lasBegransadeBytes(zip, HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES)
                    when {
                        namn == HUSLOGGEN_LOGG_JSON -> jsonBytes = bytes
                        namn.startsWith(HUSLOGGEN_BILDER_MAPP) -> {
                            val filnamn = namn.removePrefix(HUSLOGGEN_BILDER_MAPP)
                            if (filnamn.isEmpty() || filnamn.contains('/')) {
                                zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                                break
                            }
                            bildFiler[filnamn] = bytes
                        }
                        namn.startsWith(HUSLOGGEN_DOKUMENT_MAPP) -> {
                            val filnamn = namn.removePrefix(HUSLOGGEN_DOKUMENT_MAPP)
                            if (filnamn.isEmpty() || filnamn.contains('/')) {
                                zipFel = SakerhetskopiaLasResult.Fel.TrasigFil
                                break
                            }
                            dokumentFiler[filnamn] = bytes
                        }
                    }
                    zip.closeEntry()
                }
            }
            zipFel?.let { return it }
            val json = jsonBytes ?: return SakerhetskopiaLasResult.Fel.SaknarLoggJson
            when (val jsonResult = lasSakerhetskopiaJson(String(json, Charsets.UTF_8))) {
                is SakerhetskopiaJsonLasResult.Ok ->
                    SakerhetskopiaLasResult.Ok(jsonResult.innehall, bildFiler, dokumentFiler)
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
