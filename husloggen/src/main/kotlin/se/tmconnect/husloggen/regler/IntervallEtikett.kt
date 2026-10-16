package se.tmconnect.husloggen.regler

enum class IntervallEnhet {
    MANAD,
    AR,
}

fun intervallEtikett(manader: Int): Pair<IntervallEnhet, Int> {
    if (manader > 0 && manader % 12 == 0) {
        return IntervallEnhet.AR to (manader / 12)
    }
    return IntervallEnhet.MANAD to manader
}
