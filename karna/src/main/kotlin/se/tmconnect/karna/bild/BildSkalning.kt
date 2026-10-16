package se.tmconnect.karna.bild

import kotlin.math.roundToInt

internal fun beraknaNedskaladStorlek(
    bredd: Int,
    hojd: Int,
    maxLangstaSida: Int = 2000,
): Pair<Int, Int> {
    if (bredd <= 0 || hojd <= 0) {
        return bredd to hojd
    }
    val langsta = maxOf(bredd, hojd)
    if (langsta <= maxLangstaSida) {
        return bredd to hojd
    }
    val skala = maxLangstaSida.toFloat() / langsta
    return (bredd * skala).roundToInt() to (hojd * skala).roundToInt()
}

internal fun beraknaInSampleSize(bredd: Int, hojd: Int, maxSida: Int = 2000): Int {
    if (maxOf(bredd, hojd) <= maxSida) {
        return 1
    }
    val langsta = maxOf(bredd, hojd)
    var inSampleSize = 1
    while (langsta / (inSampleSize * 2) >= maxSida) {
        inSampleSize *= 2
    }
    return inSampleSize
}
