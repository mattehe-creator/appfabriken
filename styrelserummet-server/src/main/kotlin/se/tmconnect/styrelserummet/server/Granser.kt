package se.tmconnect.styrelserummet.server

const val MAX_FILER = 10
const val MAX_BYTES_PER_JOBB = 25 * 1024 * 1024
const val MAX_JOBB_PER_IP_TIMME = 5
const val MAX_VERKTYG_ANROP_PER_IP_TIMME = 60
const val MAX_VERKTYG_KROPP_BYTES = 64 * 1024
const val RENSA_EFTER_DAGAR = 30
const val MAX_TECKEN_TILL_TOLKNING = 400_000
const val MAX_KOMPONENTER = 500
const val MAX_TEXT_PER_FALT = 300
const val MAX_NYTTJANDEPERIOD_AR = 150
const val MIN_ANSKAFFNINGSAR = 1800
const val MAX_BELOPP_KR = 1_000_000_000_000L
const val MAX_KROPP_RADER_BYTES = 2 * 1024 * 1024

/**
 * Kontroll av rader som kommer utifrån (PUT /rader). Utan den kunde ett anskaffningsår som
 * -2000000000 få bokfört värde att räkna i miljarder varv och låsa servern.
 *
 * Övre gränsen för anskaffningsår är nästa kalenderår, samma som tolkningen tillåter
 * (ByggUtkast). Räknades den från räkenskapsårets slut gick ett utkast med en komponent
 * anskaffad efter räkenskapsåret inte att spara alls, inte ens oförändrat.
 */
fun raderInomGranser(
    komponenter: List<KomponentDto>,
    arsslut: Int,
    nuAr: Int = java.time.Year.now().value,
): Boolean =
    komponenter.size <= MAX_KOMPONENTER &&
        komponenter.all { k ->
            k.namn.length <= MAX_TEXT_PER_FALT &&
                k.kalla.length <= MAX_TEXT_PER_FALT &&
                k.anskaffningsar in MIN_ANSKAFFNINGSAR..(maxOf(arsslut, nuAr) + 1) &&
                k.nyttjandeperiodAr in 1..MAX_NYTTJANDEPERIOD_AR &&
                k.anskaffningsvarde in 0..MAX_BELOPP_KR &&
                k.restvarde in 0..k.anskaffningsvarde
        }

fun arGiltigtJobbId(id: String): Boolean =
    id.length == 32 && id.all { it in 'a'..'f' || it in '0'..'9' }

fun skaRensas(sistaAktivitetEpochMs: Long, nuMs: Long = System.currentTimeMillis()): Boolean {
    val gransMs = RENSA_EFTER_DAGAR * 24L * 60 * 60 * 1000
    return nuMs - sistaAktivitetEpochMs > gransMs
}

fun skattaTecken(sidor: List<String>): Int = sidor.sumOf { it.length }

/**
 * Läser högst [max] byte. Null om strömmen innehåller mer än så.
 */
fun lasHogst(strom: java.io.InputStream, max: Long): ByteArray? {
    if (max < 0) return null
    val ut = java.io.ByteArrayOutputStream()
    val buffert = ByteArray(64 * 1024)
    var totalt = 0L
    while (true) {
        val n = strom.read(buffert)
        if (n < 0) break
        totalt += n
        if (totalt > max) return null
        ut.write(buffert, 0, n)
    }
    return ut.toByteArray()
}
