package se.tmconnect.rostfangst.wear

import java.nio.charset.StandardCharsets

data class ForslagMeddelande(
    val id: Long,
    val typ: String,
    val rubrik: String
)

fun kodaForslag(id: Long, typ: String, rubrik: String): ByteArray {
    // Validera inparametrar
    if (id < 0) return ByteArray(0) // Returnerar tom byte-array för ogiltigt id
    
    val trimmadRubrik = rubrik.trim()
    if (trimmadRubrik.isBlank()) return ByteArray(0) // Returnerar tom byte-array för tom rubrik
    
    // Avkorta rubriken om den är för lång
    val avkortadRubrik = if (trimmadRubrik.length > 120) {
        trimmadRubrik.substring(0, 119) + "…"
    } else {
        trimmadRubrik
    }
    
    // Skapa JSON-sträng manuellt istället för att använda org.json
    val json = StringBuilder()
    json.append("{")
    json.append("\"id\":$id,")
    json.append("\"typ\":\"$typ\",")
    json.append("\"rubrik\":\"$avkortadRubrik\"")
    json.append("}")
    
    return json.toString().toByteArray(StandardCharsets.UTF_8)
}

fun avkodaForslag(data: ByteArray): ForslagMeddelande? {
    val jsonString = data.toString(StandardCharsets.UTF_8)
    
    // Kontrollera att JSON-strängen är giltig
    if (jsonString.isBlank() || !jsonString.startsWith("{") || !jsonString.endsWith("}")) {
        return null
    }
    
    try {
        // Parsa manuellt istället för att använda org.json
        val idMatch = Regex("\"id\":(-?\\d+)").find(jsonString)
        val typMatch = Regex("\"typ\":\"([^\"]*)\"").find(jsonString)
        val rubrikMatch = Regex("\"rubrik\":\"([^\"]*)\"").find(jsonString)
        
        val id = idMatch?.groups?.get(1)?.value?.toLongOrNull() ?: return null
        if (id < 0) return null
        
        val typ = typMatch?.groups?.get(1)?.value ?: return null
        if (typ.isBlank()) return null
        
        val rubrik = rubrikMatch?.groups?.get(1)?.value ?: return null
        if (rubrik.isBlank()) return null
        
        return ForslagMeddelande(id, typ, rubrik)
    } catch (e: Exception) {
        return null
    }
}
