package se.tmconnect.flextid.regler

import java.time.LocalTime
import java.util.Locale

fun minuterTillLocalTime(minuter: Int): LocalTime =
    LocalTime.of(minuter / 60, minuter % 60)

fun localTimeTillMinuter(tid: LocalTime): Int = tid.hour * 60 + tid.minute

fun formateraTid(tid: LocalTime?, locale: Locale = Locale.getDefault()): String {
    if (tid == null) return ""
    return String.format(locale, "%02d:%02d", tid.hour, tid.minute)
}

fun formateraMinuterSomTid(minuter: Int, locale: Locale = Locale.getDefault()): String {
    val tecken = if (minuter < 0) "-" else ""
    val absolut = kotlin.math.abs(minuter)
    val h = absolut / 60
    val m = absolut % 60
    return tecken + String.format(locale, "%d:%02d", h, m)
}

fun formateraSaldoMinuter(minuter: Int, locale: Locale = Locale.getDefault()): String {
    val tecken = when {
        minuter > 0 -> "+"
        minuter < 0 -> "-"
        else -> ""
    }
    return tecken + formateraMinuterSomTid(kotlin.math.abs(minuter), locale)
}

fun tolkaTidText(text: String): LocalTime? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val delar = trimmed.split(':')
    if (delar.size != 2) return null
    val h = delar[0].toIntOrNull() ?: return null
    val m = delar[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return LocalTime.of(h, m)
}
