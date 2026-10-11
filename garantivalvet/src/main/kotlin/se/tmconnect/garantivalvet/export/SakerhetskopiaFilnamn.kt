package se.tmconnect.garantivalvet.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun sakerhetskopiaFilnamn(datum: LocalDate = LocalDate.now()): String {
    val datumStr = datum.format(DateTimeFormatter.ISO_LOCAL_DATE)
    return "garantivalvet-sakerhetskopia-$datumStr.zip"
}
