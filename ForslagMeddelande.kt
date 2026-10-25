package se.tmconnect.rostfangst.wear

import java.nio.charset.StandardCharsets

data class ForslagMeddelande(
    val id: Long,
    val typ: String,
    val rubrik: String
)

fun kodaForslag(id: Long, typ: String, rubrik: String): ByteArray {
    // Manually create JSON string to avoid dependency on org.json in tests
    val trimmadRubrik = rubrik.trim()
    val avkortadRubrik = if (trimmadRubrik.length > 120) {
        trimmadRubrik.substring(0, 119) + "…"
    } else {
        trimmadRubrik
    }
    
    // Escape quotes and backslashes in the rubrik for JSON
    val escapedRubrik = avkortadRubrik.replace("\\", "\\\\").replace("\"", "\\\"")
    
    val jsonString = """{"id":$id,"typ":"$typ","rubrik":"$escapedRubrik"}"""
    return jsonString.toByteArray(StandardCharsets.UTF_8)
}

fun avkodaForslag(data: ByteArray): ForslagMeddelande? {
    if (data.isEmpty()) return null
    
    val jsonString = try {
        data.toString(StandardCharsets.UTF_8)
    } catch (e: Exception) {
        return null
    }
    
    if (jsonString.isBlank()) return null
    
    // Simple manual parsing instead of using org.json
    try {
        // Extract id
        val idMatch = Regex("\"id\":(-?\\d+)").find(jsonString)
        val id = idMatch?.groups?.get(1)?.value?.toLongOrNull() ?: return null
        if (id < 0) return null
        
        // Extract typ
        val typMatch = Regex("\"typ\":\"([^\"]*)\"").find(jsonString)
        val typ = typMatch?.groups?.get(1)?.value ?: return null
        if (typ.isBlank()) return null
        
        // Extract rubrik
        val rubrikMatch = Regex("\"rubrik\":\"([^\"]*)\"").find(jsonString)
        val rubrik = rubrikMatch?.groups?.get(1)?.value ?: return null
        if (rubrik.isBlank()) return null
        
        return ForslagMeddelande(id, typ, rubrik)
    } catch (e: Exception) {
        return null
    }
}
