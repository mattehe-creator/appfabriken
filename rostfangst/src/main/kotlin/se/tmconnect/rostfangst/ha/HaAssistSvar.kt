package se.tmconnect.rostfangst.ha

import org.json.JSONObject

fun lasAssistSvar(json: String): String? {
    return try {
        val obj = JSONObject(json.trim())
        val response = obj.optJSONObject("response")
            ?: return null
        val speech = response.optJSONObject("speech")
            ?: return null
        val plain = speech.optJSONObject("plain")
            ?: return null
        val speechText = plain.optString("speech", "").trim()
        if (speechText.isEmpty()) null else speechText
    } catch (_: Exception) {
        null
    }
}
