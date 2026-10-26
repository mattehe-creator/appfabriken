package se.tmconnect.rostfangst.lokal

fun giltigtModellnamn(text: String): Boolean {
    val trimmad = text.trim()
    if (trimmad.isEmpty()) return false
    if (trimmad.length > 100) return false
    
    val giltigaTecken = Regex("^[a-zA-Z0-9._\\-:/]+$")
    return giltigaTecken.matches(trimmad)
}
