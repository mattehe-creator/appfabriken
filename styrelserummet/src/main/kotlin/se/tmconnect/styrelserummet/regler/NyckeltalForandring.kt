package se.tmconnect.styrelserummet.regler

fun forandringTiondelsProcent(nu: Long?, foregaende: Long?): Long? {
    if (nu == null || foregaende == null || foregaende <= 0) {
        return null
    }
    return avrundaKvot((nu - foregaende) * 1000, foregaende)
}

fun formateraForandring(tiondelar: Long?): String {
    if (tiondelar == null) {
        return "\u2013"
    }

    val tecken =
        when {
            tiondelar > 0 -> "+"
            tiondelar < 0 -> "\u2212"
            else -> ""
        }
    val absolut = kotlin.math.abs(tiondelar)
    val heltal = absolut / 10
    val decimal = absolut % 10
    return "$tecken$heltal,$decimal %"
}
