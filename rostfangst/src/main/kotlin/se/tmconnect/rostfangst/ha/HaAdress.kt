package se.tmconnect.rostfangst.ha

fun normaliseraHaAdress(text: String): String? {
    val trimmad = text.trim()
    
    if (trimmad.isEmpty()) {
        return null
    }
    
    // Kontrollera prefix och behåll originalformatet
    val prefix = when {
        trimmad.startsWith("http://", ignoreCase = true) -> "http://"
        trimmad.startsWith("https://", ignoreCase = true) -> "https://"
        else -> return null
    }
    
    // Hämta resten efter prefixet
    val rest = trimmad.substring(prefix.length)
    
    if (rest.isEmpty()) {
        return null
    }
    
    if (rest.contains(" ")) {
        return null
    }
    
    // Ta bort avslutande snedstreck
    val utanSlutandeSnedstreck = rest.removeSuffix("/")
    
    // Kontrollera att det finns ett värdnamn
    val vardenamn = when {
        utanSlutandeSnedstreck.contains(":") -> {
            val delar = utanSlutandeSnedstreck.split(":")
            if (delar.size < 2) {
                return null
            }
            delar[0]
        }
        else -> utanSlutandeSnedstreck
    }
    
    if (vardenamn.isEmpty()) {
        return null
    }
    
    // Returnera prefixet i originalformat och resten (konverterad till gemener)
    return prefix + utanSlutandeSnedstreck.lowercase()
}
