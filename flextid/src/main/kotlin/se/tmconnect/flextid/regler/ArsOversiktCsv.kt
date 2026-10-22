package se.tmconnect.flextid.regler

import java.util.Locale

fun arsOversiktCsv(
    rader: List<ManadsRad>,
    rubriker: List<String>,
    manadsnamn: List<String>,
    locale: Locale,
): String {
    if (rubriker.size != 4 || manadsnamn.size != 12) {
        throw IllegalArgumentException()
    }
    return buildString {
        append(rubriker.joinToString(";"))
        append('\n')
        for (rad in rader) {
            append(manadsnamn[rad.manad - 1])
            append(';')
            append(formateraMinuterSomTid(rad.arbetadMin, locale))
            append(';')
            append(formateraSaldoMinuter(rad.utfallMin, locale))
            append(';')
            append(formateraSaldoMinuter(rad.saldoVidSlutMin, locale))
            append('\n')
        }
    }
}
