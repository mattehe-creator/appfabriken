package se.tmconnect.styrelserummet.regler

fun kronor(belopp: Long): String {
    val negativt = belopp < 0
    val absolut = kotlin.math.abs(belopp)
    val siffror = absolut.toString()
    val grupperad = siffror
        .reversed()
        .chunked(3)
        .joinToString("\u00A0")
        .reversed()
    val tecken = if (negativt) "\u2212" else ""
    return "$tecken$grupperad kr"
}
