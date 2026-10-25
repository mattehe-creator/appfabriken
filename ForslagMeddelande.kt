package se.tmconnect.rostfangst.wear

import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class ForslagMeddelande(
    val id: Long,
    val typ: String,
    val rubrik: String
)

fun kodaForslag(id: Long, typ: String, rubrik: String): ByteArray {
    val json = JSONObject()
    json.put("id", id)
    json.put("typ", typ)
    
    val trimmadRubrik = rubrik.trim()
    val avkortadRubrik = if (trimmadRubrik.length > 120) {
        trimmadRubrik.substring(0, 119) + "…"
    } else {
        trimmadRubrik
    }
    
    json.put("rubrik", avkortadRubrik)
    
    return json.toString().toByteArray(StandardCharsets.UTF_8)
}

fun avkodaForslag(data: ByteArray): ForslagMeddelande? {
    if (data.isEmpty()) return null
    
    val jsonString = try {
        data.toString(StandardCharsets.UTF_8)
    } catch (e: Exception) {
        return null
    }
    
    if (jsonString.isBlank()) return null
    
    return try {
        val json = JSONObject(jsonString)
        
        val id = json.getLong("id")
        if (id < 0) return null
        
        val typ = json.getString("typ")
        if (typ.isBlank()) return null
        
        val rubrik = json.getString("rubrik")
        if (rubrik.isBlank()) return null
        
        ForslagMeddelande(id, typ, rubrik)
    } catch (e: Exception) {
        null
    }
}
