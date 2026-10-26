package se.tmconnect.rostfangst.lokal

fun lasTranskription(json: String): String? {
    val text = lasJsonStrang(json, "text") ?: return null
    val trimmad = text.trim()
    return if (trimmad.isEmpty()) null else trimmad
}

fun lasOllamaSvar(json: String): String? {
    val content = lasJsonStrang(json, "content") ?: return null
    val trimmad = content.trim()
    return if (trimmad.isEmpty()) null else trimmad
}
