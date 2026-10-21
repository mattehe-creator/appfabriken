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

fun procent(del: Long, helhet: Long): String {
    if (helhet == 0L) {
        return "\u2013"
    }

    val resultat = (del.toDouble() / helhet.toDouble()) * 100
    return String.format(java.util.Locale.US, "%.1f", resultat).replace(".", ",") + " %"
}

fun arsintervall(fran: Int, till: Int): String {
    return "$fran\u2013$till"
}
