package se.tmconnect.rostfangst.wear

import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class ForslagMeddelande(
    val id: Long,
    val typ: String,
    val rubrik: String
)

fun kodaForslag(id: Long, typ: String, rubrik: String): ByteArray {
    val trimmedRubrik = rubrik.trim()
    if (trimmedRubrik.isEmpty()) return ByteArray(0)
    
    val truncatedRubrik = if (trimmedRubrik.length > 120) {
        trimmedRubrik.substring(0, 119) + "…"
    } else {
        trimmedRubrik
    }
    
    val jsonObject = JSONObject()
    jsonObject.put("id", id)
    jsonObject.put("typ", typ)
    jsonObject.put("rubrik", truncatedRubrik)
    
    return jsonObject.toString().toByteArray(StandardCharsets.UTF_8)
}

fun avkodaForslag(data: ByteArray): ForslagMeddelande? {
    if (data.isEmpty()) return null
    
    val jsonString = String(data, StandardCharsets.UTF_8)
    
    return try {
        val jsonObject = JSONObject(jsonString)
        
        val id = jsonObject.getLong("id")
        if (id < 0) return null
        
        val typ = jsonObject.getString("typ")
        if (typ.isBlank()) return null
        
        val rubrik = jsonObject.getString("rubrik")
        if (rubrik.isBlank()) return null
        
        ForslagMeddelande(id, typ, rubrik)
    } catch (e: Exception) {
        null
    }
}
