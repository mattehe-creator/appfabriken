package se.tmconnect.styrelserummet.regler

/** UTF-8 BOM, så att svensk Excel läser å, ä och ö rätt. */
const val CSV_BOM = "\uFEFF"

private val TAL = Regex("""-?\d+([.,]\d+)?""")

/**
 * Cell i semikolonseparerad CSV. Text som börjar med `=`, `+`, `-`, `@`, tab eller CR får ett
 * inledande `'`, så att Excel inte tolkar den som formel (namn och källcitat kommer ur uppladdade
 * handlingar). Tal, även negativa, lämnas som de är. Celler med `;`, `"` eller radbrytning citeras.
 */
fun csvCell(text: String): String {
    val saker =
        if (text.isNotEmpty() && text[0] in "=+-@\t\r" && !TAL.matches(text)) "'$text" else text
    return if (saker.any { it == ';' || it == '"' || it == '\n' || it == '\r' }) {
        "\"" + saker.replace("\"", "\"\"") + "\""
    } else {
        saker
    }
}

/** Tar bort skyddet från [csvCell] vid import av en fil som appen själv skrivit. */
fun csvCellTillbaka(text: String): String =
    if (text.length >= 2 && text[0] == '\'' && text[1] in "=+-@\t\r") text.substring(1) else text
