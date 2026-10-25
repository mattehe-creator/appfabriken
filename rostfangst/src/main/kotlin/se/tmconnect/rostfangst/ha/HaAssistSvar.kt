package se.tmconnect.rostfangst.ha

import org.json.JSONObject

fun lasAssistSvar(json: String): String? {
    return try {
        val obj = JSONObject(json.trim())
        
        // Direct access to nested objects with proper null checks
        val response = obj.optJSONObject("response")
        if (response == null) return null
        
        val speech = response.optJSONObject("speech")
        if (speech == null) return null
        
        val plain = speech.optJSONObject("plain")
        if (plain == null) return null
        
        val speechText = plain.optString("speech", "").trim()
        if (speechText.isEmpty()) null else speechText
    } catch (_: Exception) {
        null
    }
}
