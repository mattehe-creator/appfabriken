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
