package se.tmconnect.husloggen.regler

import java.time.LocalDate

enum class PostFel {
    VAD_SAKNAS,
    DATUM_I_FRAMTIDEN,
    BELOPP_OGILTIGT,
    AVDRAG_OGILTIGT,
    AVDRAG_FOR_STORT,
}

fun valideraPost(
    vad: String,
    datum: LocalDate,
    idag: LocalDate,
    beloppText: String,
    rotRutAvdragText: String = "",
): PostFel? {
    if (vad.isBlank()) return PostFel.VAD_SAKNAS
    if (datum.isAfter(idag)) return PostFel.DATUM_I_FRAMTIDEN
    if (beloppText.isNotBlank() && tolkaBeloppTillOre(beloppText) == null) {
        return PostFel.BELOPP_OGILTIGT
    }
    if (rotRutAvdragText.isNotBlank()) {
        val avdragOre = tolkaBeloppTillOre(rotRutAvdragText)
            ?: return PostFel.AVDRAG_OGILTIGT
        val beloppOre = beloppText.trim().takeIf { it.isNotEmpty() }?.let { tolkaBeloppTillOre(it) }
            ?: return PostFel.AVDRAG_OGILTIGT
        if (avdragOre > beloppOre) return PostFel.AVDRAG_FOR_STORT
    }
    return null
}
