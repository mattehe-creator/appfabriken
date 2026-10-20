package se.tmconnect.styrelserummet.server

/**
 * Klientens IP för gränsräkning. Bakom proxy (när [proxyPaa]) används den sista icke-tomma adressen i
 * `X-Forwarded-For`; annars anslutningens adress och huvudet ignoreras.
 * Detta gör att besökare inte kan förfalska sin IP-adress genom att skicka ett eget huvud,
 * eftersom värdens proxy lägger till den verkliga adressen sist i listan.
 */
fun klientAdress(forwardedFor: String?, anslutning: String, proxyPaa: Boolean): String {
    if (proxyPaa && !forwardedFor.isNullOrBlank()) {
        // Dela upp strängen och hitta den sista icke-tomma adressen
        val addresses = forwardedFor.split(',')
        for (i in addresses.size - 1 downTo 0) {
            val addr = addresses[i].trim()
            if (addr.isNotEmpty()) {
                return addr
            }
        }
    }
    return anslutning
}

fun proxyPaaFranMiljo(): Boolean = System.getenv("STYRELSERUMMET_PROXY") == "1"
