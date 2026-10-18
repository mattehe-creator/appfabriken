package se.tmconnect.rostfangst.export

import se.tmconnect.rostfangst.data.FangstRad

fun historikSomCsv(rader: List<FangstRad>): String {
    val header = listOf("tid", "typ", "titel", "original", "plats", "svar")
    val body = rader.map { rad ->
        listOf(
            rad.skapadIso,
            rad.typ.name,
            rad.titel,
            rad.originalText,
            rad.plats.orEmpty(),
            rad.svar.orEmpty(),
        )
    }
    return (listOf(header) + body).joinToString("\n") { rad -> rad.joinToString(",") { csvFalt(it) } } + "\n"
}

internal fun csvFalt(text: String): String {
    if (text.none { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
        return text
    }
    return "\"" + text.replace("\"", "\"\"") + "\""
}
