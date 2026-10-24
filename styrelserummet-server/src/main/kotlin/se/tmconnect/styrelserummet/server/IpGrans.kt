package se.tmconnect.styrelserummet.server

import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/** En reserverad plats i IP-gränsen. Lämnas tillbaka med [IpGrans.angra] om uppladdningen avvisas. */
data class IpReservation(val ip: String, val tidMs: Long)

class IpGrans(private val rot: Path) {
    init {
        (rot / "ip-grans").createDirectories()
    }

    private val timmeMs = 60L * 60 * 1000

    fun hashIp(ip: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(ip.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun fil(ip: String): Path = rot / "ip-grans" / hashIp(ip)

    private fun lasTider(ip: String): List<Long> {
        val fil = fil(ip)
        return if (fil.exists()) fil.readText().lines().mapNotNull { it.toLongOrNull() } else emptyList()
    }

    /**
     * Kontrollerar gränsen och registrerar jobbet i ett steg, så att samtidiga uppladdningar från
     * samma IP inte kan passera gränsen. Null om gränsen är nådd.
     */
    fun reservera(ip: String, nuMs: Long = System.currentTimeMillis()): IpReservation? =
        synchronized(LAS) {
            val inomTimme = lasTider(ip).filter { nuMs - it < timmeMs }
            if (inomTimme.size >= MAX_JOBB_PER_IP_TIMME) return null
            fil(ip).writeText((inomTimme + nuMs).joinToString("\n"))
            IpReservation(ip, nuMs)
        }

    /** Lämnar tillbaka en reserverad plats (en registrering med reservationens tid tas bort). */
    fun angra(reservation: IpReservation) {
        synchronized(LAS) {
            val tider = lasTider(reservation.ip).toMutableList()
            if (tider.remove(reservation.tidMs)) {
                fil(reservation.ip).writeText(tider.joinToString("\n"))
            }
        }
    }

    fun farSkapaJobb(ip: String, nuMs: Long = System.currentTimeMillis()): Boolean =
        synchronized(LAS) {
            lasTider(ip).count { nuMs - it < timmeMs } < MAX_JOBB_PER_IP_TIMME
        }

    fun registreraJobb(ip: String, nuMs: Long = System.currentTimeMillis()) {
        synchronized(LAS) {
            val spara = (lasTider(ip).filter { nuMs - it < timmeMs } + nuMs).joinToString("\n")
            fil(ip).writeText(spara)
        }
    }

    fun nastaForsokEpochMs(ip: String, nuMs: Long): Long {
        val inomTimme = synchronized(LAS) { lasTider(ip) }.filter { nuMs - it < timmeMs }
        if (inomTimme.isEmpty()) return nuMs
        return inomTimme.min() + timmeMs
    }

    private companion object {
        /** Delas mellan instanser som pekar på samma katalog. Filerna är små, så ett lås räcker. */
        val LAS = Any()
    }
}
