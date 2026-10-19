package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Utkast
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText

enum class JobbStatus { VANTAR, BEARBETAR, KLAR, FEL }

@Serializable
data class JobbMeta(
    val id: String,
    val forening: String,
    val arsslut: Int,
    val status: String,
    val skapadMs: Long,
    val senastAktivMs: Long,
    val klientIpHash: String,
    val felmeddelande: String? = null,
)

class JobbLagring(private val rot: Path) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    init {
        rot.createDirectories()
        (rot / "jobb").createDirectories()
    }

    fun jobbKatalog(id: String): Path = rot / "jobb" / id

    fun skapa(meta: JobbMeta) {
        val dir = jobbKatalog(meta.id)
        dir.createDirectories()
        (dir / "filer").createDirectories()
        skrivMeta(meta)
    }

    fun lasMeta(id: String): JobbMeta? {
        val fil = jobbKatalog(id) / "meta.json"
        if (!fil.exists()) return null
        return json.decodeFromString<JobbMeta>(fil.readText())
    }

    fun skrivMeta(meta: JobbMeta) {
        (jobbKatalog(meta.id) / "meta.json").writeText(json.encodeToString(meta))
    }

    fun sparaUtkast(id: String, utkast: Utkast) {
        (jobbKatalog(id) / "utkast.json").writeText(json.encodeToString(utkast))
    }

    fun lasUtkast(id: String): Utkast? {
        val fil = jobbKatalog(id) / "utkast.json"
        if (!fil.exists()) return null
        return json.decodeFromString<Utkast>(fil.readText())
    }

    fun sparaFil(id: String, filnamn: String, bytes: ByteArray) {
        val saker = filnamn.replace(Regex("""[^a-zA-Z0-9._-]"""), "_")
        Files.write(jobbKatalog(id) / "filer" / saker, bytes)
    }

    fun listaFiler(id: String): List<Path> =
        (jobbKatalog(id) / "filer").takeIf { it.exists() }?.listDirectoryEntries().orEmpty()

    fun raderaJobb(id: String) {
        val dir = jobbKatalog(id)
        if (!dir.exists()) return
        dir.listDirectoryEntries().forEach { entry ->
            if (entry.isDirectory()) {
                entry.listDirectoryEntries().forEach { it.deleteIfExists() }
            }
            entry.deleteIfExists()
        }
        dir.deleteIfExists()
    }

    fun allaJobb(): List<String> =
        (rot / "jobb").takeIf { it.exists() }?.listDirectoryEntries()?.map { it.fileName.toString() }.orEmpty()

    fun uppdateraAktivitet(id: String) {
        val meta = lasMeta(id) ?: return
        skrivMeta(meta.copy(senastAktivMs = System.currentTimeMillis()))
    }
}

@Serializable
data class RaderRequest(val komponenter: List<Komponent>)

@Serializable
data class JobbResponse(val meta: JobbMeta, val utkast: Utkast? = null)
