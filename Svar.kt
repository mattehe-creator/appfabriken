package se.tmconnect.rostfangst.lokal

fun lasTranskription(json: String): String? {
    val text = lasJsonStrang(json, "text") ?: return null
    return text.takeIf { it.isNotBlank() }
}

fun lasOllamaSvar(json: String): String? {
    val content = lasJsonStrang(json, "content") ?: return null
    return content.takeIf { it.isNotBlank() }
}
