package se.tmconnect.styrelserummet.server

/**
 * Klientens IP för gränsräkning. Bakom proxy (när [proxyPaa]) används den sista adressen i
 * `X-Forwarded-For` (den som värdens proxy lade till); annars anslutningens adress och huvudet ignoreras.
 * Detta för att förhindra att användare kan förfalska sin IP-adress och kringgå gränsen på 5 jobb per timme.
 */
fun klientAdress(forwardedFor: String?, anslutning: String, proxyPaa: Boolean): String {
    if (proxyPaa && !forwardedFor.isNullOrBlank()) {
        // Dela upp strängen och hitta den sista icke-tomma adressen
        val adresser = forwardedFor.split(',')
        for (i in adresser.size - 1 downTo 0) {
            val trimmadAdress = adresser[i].trim()
            if (trimmadAdress.isNotEmpty()) {
                return trimmadAdress
            }
        }
    }
    return anslutning
}

fun proxyPaaFranMiljo(): Boolean = System.getenv("STYRELSERUMMET_PROXY") == "1"
