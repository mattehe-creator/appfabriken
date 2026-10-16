package se.tmconnect.husloggen.regler

import java.time.LocalDate

enum class PostFel {
    VAD_SAKNAS,
    DATUM_I_FRAMTIDEN,
    BELOPP_OGILTIGT,
}

fun valideraPost(
    vad: String,
    datum: LocalDate,
    idag: LocalDate,
    beloppText: String,
): PostFel? {
    if (vad.isBlank()) return PostFel.VAD_SAKNAS
    if (datum.isAfter(idag)) return PostFel.DATUM_I_FRAMTIDEN
    if (beloppText.isNotBlank() && tolkaBeloppTillOre(beloppText) == null) {
        return PostFel.BELOPP_OGILTIGT
    }
    return null
}
