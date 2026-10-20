package se.tmconnect.styrelserummet.server

import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class IpGrans(private val rot: Path) {
    init {
        (rot / "ip-grans").createDirectories()
    }

    fun hashIp(ip: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(ip.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun farSkapaJobb(ip: String, nuMs: Long = System.currentTimeMillis()): Boolean {
        val fil = rot / "ip-grans" / hashIp(ip)
        val timmeMs = 60L * 60 * 1000
        val tider = if (fil.exists()) {
            fil.readText().lines().mapNotNull { it.toLongOrNull() }
        } else {
            emptyList()
        }
        val inomTimme = tider.filter { nuMs - it < timmeMs }
        return inomTimme.size < MAX_JOBB_PER_IP_TIMME
    }

    fun registreraJobb(ip: String, nuMs: Long = System.currentTimeMillis()) {
        val fil = rot / "ip-grans" / hashIp(ip)
        val timmeMs = 60L * 60 * 1000
        val tider = if (fil.exists()) {
            fil.readText().lines().mapNotNull { it.toLongOrNull() }
        } else {
            emptyList()
        }
        val spara = (tider.filter { nuMs - it < timmeMs } + nuMs).joinToString("\n")
        fil.writeText(spara)
    }
}
