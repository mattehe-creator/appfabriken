package se.tmconnect.styrelserummet.server

import java.util.concurrent.ConcurrentHashMap

/**
 * Gräns för gratisverktygens API: räknar anrop per IP i minnet (ingen disk, ingen loggning).
 */
class VerktygAnropsGrans {
    private val anrop = ConcurrentHashMap<String, MutableList<Long>>()

    fun farAnropa(ip: String, nuMs: Long = System.currentTimeMillis()): Boolean {
        val antal = antalInomTimme(ip, nuMs)
        return antal < MAX_VERKTYG_ANROP_PER_IP_TIMME
    }

    fun registreraAnrop(ip: String, nuMs: Long = System.currentTimeMillis()) {
        val lista = anrop.computeIfAbsent(ip) { mutableListOf() }
        synchronized(lista) {
            behallInomTimme(lista, nuMs)
            lista.add(nuMs)
        }
    }

    private fun antalInomTimme(ip: String, nuMs: Long): Int {
        val lista = anrop[ip] ?: return 0
        synchronized(lista) {
            behallInomTimme(lista, nuMs)
            return lista.size
        }
    }

    private fun behallInomTimme(lista: MutableList<Long>, nuMs: Long) {
        val timmeMs = 60L * 60 * 1000
        lista.removeAll { nuMs - it >= timmeMs }
    }
}
