package se.tmconnect.garantivalvet.export

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val OTILATNA_FILNAMNSTEGEN = Regex("""[/\\:*?"<>|]""")

fun reklamationsPdfFilnamn(vad: String, kopdatum: LocalDate): String {
    val datum = kopdatum.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val sakertVad = vad.trim()
        .replace(OTILATNA_FILNAMNSTEGEN, "-")
        .replace(Regex("""\s+"""), " ")
        .replace(Regex("""-+"""), "-")
        .trim('-')
        .ifEmpty { "kop" }
    return "reklamation-$sakertVad-$datum.pdf"
}
