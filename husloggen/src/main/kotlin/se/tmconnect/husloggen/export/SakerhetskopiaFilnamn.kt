package se.tmconnect.husloggen.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun sakerhetskopiaFilnamn(datum: LocalDate = LocalDate.now()): String {
    val datumStr = datum.format(DateTimeFormatter.ISO_LOCAL_DATE)
    return "husloggen-sakerhetskopia-$datumStr.zip"
}
