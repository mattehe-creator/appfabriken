package se.tmconnect.styrelserummet.server

/**
 * Klientens IP för gränsräkning. Bakom proxy (när [proxyPaa]) används första adressen i
 * `X-Forwarded-For`; annars anslutningens adress och huvudet ignoreras.
 */
fun klientAdress(forwardedFor: String?, anslutning: String, proxyPaa: Boolean): String {
    if (proxyPaa && !forwardedFor.isNullOrBlank()) {
        return forwardedFor.split(',').first().trim()
    }
    return anslutning
}

fun proxyPaaFranMiljo(): Boolean = System.getenv("STYRELSERUMMET_PROXY") == "1"
