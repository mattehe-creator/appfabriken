package se.tmconnect.styrelserummet.server

const val MAX_FILER = 10
const val MAX_BYTES_PER_JOBB = 25 * 1024 * 1024
const val MAX_JOBB_PER_IP_TIMME = 5
const val RENSA_EFTER_DAGAR = 30

fun arGiltigtJobbId(id: String): Boolean =
    id.length == 32 && id.all { it in 'a'..'f' || it in '0'..'9' }

fun skaRensas(sistaAktivitetEpochMs: Long, nuMs: Long = System.currentTimeMillis()): Boolean {
    val gransMs = RENSA_EFTER_DAGAR * 24L * 60 * 60 * 1000
    return nuMs - sistaAktivitetEpochMs > gransMs
}
