package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
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
    val betald: Boolean = false,
    val kassaId: String? = null,
)

class JobbLagring(
    private val rot: Path,
    private val logg: (String) -> Unit = ::println
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    /**
     * Lås per jobb-id (fördelade på ett fast antal lås, så att minnet inte växer med antalet jobb).
     * Låsen delas mellan alla instanser, eftersom flera instanser kan peka på samma katalog.
     */
    private fun lasFor(id: String): Any = JOBB_LAS[Math.floorMod(id.hashCode(), JOBB_LAS.size)]

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
        val dir = jobbKatalog(id)
        if (!dir.exists()) return null
        val fil = dir / "meta.json"
        if (!fil.exists()) return null
        return try {
            json.decodeFromString<JobbMeta>(fil.readText())
        } catch (e: Exception) {
            logg("Skadad meta.json-fil, jobb hoppas över")
            null
        }
    }

    /**
     * Skriver hela meta.json. Använd [uppdateraMeta] när den nya metan bygger på en läst meta,
     * annars kan en samtidig ändring (till exempel status KLAR från kön) skrivas över.
     */
    fun skrivMeta(meta: JobbMeta) {
        synchronized(lasFor(meta.id)) {
            skrivAtomart(jobbKatalog(meta.id) / "meta.json", json.encodeToString(meta))
        }
    }

    /**
     * Atomär läs-ändra-skriv av meta.json under jobbets lås. Null om jobbet saknas eller
     * meta.json inte går att läsa; då skrivs ingenting.
     */
    fun uppdateraMeta(id: String, andring: (JobbMeta) -> JobbMeta): JobbMeta? =
        synchronized(lasFor(id)) {
            val gammal = lasMeta(id) ?: return null
            val ny = andring(gammal)
            require(ny.id == id) { "Jobb-id får inte ändras" }
            skrivAtomart(jobbKatalog(id) / "meta.json", json.encodeToString(ny))
            ny
        }

    fun sparaUtkast(id: String, utkast: UtkastDto) {
        synchronized(lasFor(id)) {
            val dir = jobbKatalog(id)
            if (!dir.exists()) return
            skrivAtomart(dir / "utkast.json", json.encodeToString(utkast))
        }
    }

    /** Atomär läs-ändra-skriv av utkast.json under jobbets lås. Null om utkastet saknas. */
    fun uppdateraUtkast(id: String, andring: (UtkastDto) -> UtkastDto): UtkastDto? =
        synchronized(lasFor(id)) {
            val gammalt = lasUtkast(id) ?: return null
            val nytt = andring(gammalt)
            skrivAtomart(jobbKatalog(id) / "utkast.json", json.encodeToString(nytt))
            nytt
        }

    /**
     * Skriver till en temporär fil i samma katalog och flyttar den på plats, så att en läsare
     * eller en krasch aldrig ser en halvskriven fil.
     */
    private fun skrivAtomart(fil: Path, text: String) {
        val tmp = fil.resolveSibling("${fil.fileName}.${java.util.UUID.randomUUID()}.tmp")
        try {
            tmp.writeText(text)
            try {
                Files.move(tmp, fil, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(tmp, fil, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            tmp.deleteIfExists()
        }
    }

    fun lasUtkast(id: String): UtkastDto? {
        val dir = jobbKatalog(id)
        if (!dir.exists()) return null
        val fil = dir / "utkast.json"
        if (!fil.exists()) return null
        return json.decodeFromString<UtkastDto>(fil.readText())
    }

    fun sparaFil(id: String, filnamn: String, bytes: ByteArray) {
        val saker = filnamn.replace(Regex("""[^a-zA-Z0-9._-]"""), "_")
            .take(200) // Trunkera till max 200 tecken efter sanering
        val dir = jobbKatalog(id)
        if (!dir.exists()) return
        Files.write(dir / "filer" / saker, bytes)
    }

    fun listaFiler(id: String): List<Path> {
        val dir = jobbKatalog(id)
        if (!dir.exists()) return emptyList()
        val filerDir = dir / "filer"
        if (!filerDir.exists()) return emptyList()
        return filerDir.listDirectoryEntries().sortedBy { it.fileName.toString() }
    }

    fun raderaJobb(id: String) {
        synchronized(lasFor(id)) {
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
    }

    fun allaJobb(): List<String> =
        (rot / "jobb").takeIf { it.exists() }?.listDirectoryEntries()?.map { it.fileName.toString() }.orEmpty()

    /** Sätter senast aktiv till nu, atomärt. Returnerar den nya metan, null om jobbet saknas. */
    fun uppdateraAktivitet(id: String): JobbMeta? =
        uppdateraMeta(id) { it.copy(senastAktivMs = System.currentTimeMillis()) }

    private companion object {
        val JOBB_LAS = Array(64) { Any() }
    }
}

@Serializable
data class JobbResponse(
    val meta: JobbMeta,
    val utkast: UtkastDto? = null,
    val visning: UtkastVisningDto? = null,
    val betalning: BetalStatus? = null,
)

@Serializable
data class RaderRequest(val komponenter: List<KomponentDto>)

@Serializable
data class JamforelseRequest(val csv: String)

fun jobbResponse(meta: JobbMeta, utkast: UtkastDto?, betalning: BetalStatus? = null): JobbResponse =
    JobbResponse(
        meta = meta.copy(kassaId = null),
        utkast = utkast,
        visning = utkast?.let(::tillVisning),
        betalning = betalning,
    )

@Serializable
data class BekraftaRequest(val kassa: String)
