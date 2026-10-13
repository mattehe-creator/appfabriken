package se.tmconnect.garantivalvet.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val OTILATNA_FILNAMNSTEGEN = Regex("""[/\\:*?"<>|]""")

private fun sakertNamn(vad: String): String =
    vad.trim()
        .replace(OTILATNA_FILNAMNSTEGEN, "-")
        .replace(Regex("""\s+"""), " ")
        .replace(Regex("""-+"""), "-")
        .trim('-')
        .ifEmpty { "kop" }

fun reklamationsPdfFilnamn(vad: String, kopdatum: LocalDate): String =
    "reklamation-${sakertNamn(vad)}-${kopdatum.format(DateTimeFormatter.ISO_LOCAL_DATE)}.pdf"

/** Namnet på kvittobilden som bifogas reklamationsmejlet, i stället för ett slumpat filnamn. */
fun kvittoBilagaFilnamn(vad: String, kopdatum: LocalDate): String =
    "kvitto-${sakertNamn(vad)}-${kopdatum.format(DateTimeFormatter.ISO_LOCAL_DATE)}.jpg"
