package se.tmconnect.husloggen.regler

enum class UppgiftFel {
    NAMN_SAKNAS,
    INTERVALL_SAKNAS,
    INTERVALL_UTANFOR,
}

fun valideraUppgift(namn: String, intervallManader: Int?): UppgiftFel? {
    if (namn.isBlank()) return UppgiftFel.NAMN_SAKNAS
    if (intervallManader == null) return UppgiftFel.INTERVALL_SAKNAS
    if (intervallManader !in 1..120) return UppgiftFel.INTERVALL_UTANFOR
    return null
}
