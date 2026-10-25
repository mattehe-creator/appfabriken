package se.tmconnect.rostfangst.ha

fun normaliseraHaAdress(text: String): String? {
    val trimmed = text.trim()
    
    // Avvisa tom text
    if (trimmed.isEmpty()) {
        return null
    }
    
    // Kontrollera att texten börjar med http:// eller https:// (gemener)
    val lowerCased = trimmed.lowercase()
    if (!lowerCased.startsWith("http://") && !lowerCased.startsWith("https://")) {
        return null
    }
    
    // Hitta prefixet och resten
    val prefix = if (lowerCased.startsWith("http://")) "http://" else "https://"
    val rest = trimmed.substring(prefix.length)
    
    // Kontrollera att det finns en värdnamn del efter prefixet
    if (rest.isEmpty()) {
        return null
    }
    
    // Kontrollera att det inte finns blanksteg i mitten
    if (rest.contains(" ")) {
        return null
    }
    
    // Ta bort avslutande snedstreck
    val noTrailingSlashes = rest.trimEnd('/')
    
    // Om värdnamnet är tomt efter borttagning av snedstreck, avvisa
    if (noTrailingSlashes.isEmpty()) {
        return null
    }
    
    // Returnera prefixet i gemener + resten utan avslutande snedstreck
    return prefix + noTrailingSlashes
}
