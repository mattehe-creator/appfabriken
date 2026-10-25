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
    
    var trimmadRubrik = rubrik.trim()
    if (trimmadRubrik.length > 120) {
        trimmadRubrik = trimmadRubrik.substring(0, 119) + "…"
    }
    json.put("rubrik", trimmadRubrik)
    
    return json.toString().toByteArray(StandardCharsets.UTF_8)
}

fun avkodaForslag(data: ByteArray): ForslagMeddelande? {
    val jsonString = data.toString(StandardCharsets.UTF_8)
    if (jsonString.isBlank()) return null
    
    return try {
        val json = JSONObject(jsonString)
        val id = json.getLong("id")
        val typ = json.getString("typ")
        val rubrik = json.getString("rubrik")
        
        if (id < 0 || typ.isBlank() || rubrik.isBlank()) {
            return null
        }
        
        ForslagMeddelande(id, typ, rubrik)
    } catch (e: Exception) {
        null
    }
}
