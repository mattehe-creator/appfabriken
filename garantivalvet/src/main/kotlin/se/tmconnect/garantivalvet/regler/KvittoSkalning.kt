package se.tmconnect.garantivalvet.regler

import kotlin.math.roundToInt

/**
 * Beräknar bredd och höjd efter nedskalning så att längsta sidan högst blir [maxLangstaSida].
 *
 * Om bilden redan är mindre ändras inget. Ogiltiga mått (≤ 0) returneras oförändrade.
 */
fun beraknaNedskaladStorlek(
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

/**
 * Beräknar inSampleSize för att undvika att ladda stora bilder i full storlek.
 *
 * Returnerar den största tvåpotensen n (1, 2, 4, 8 ...) sådan att max(bredd, hojd) / n >= maxSida.
 * Är bilden redan högst maxSida, returneras 1.
 */
fun beraknaInSampleSize(bredd: Int, hojd: Int, maxSida: Int = 2000): Int {
    if (bredd <= maxSida || hojd <= maxSida) {
        return 1
    }
    
    val langsta = maxOf(bredd, hojd)
    var inSampleSize = 1
    
    while (langsta / (inSampleSize * 2) >= maxSida) {
        inSampleSize *= 2
    }
    
    return inSampleSize
}
