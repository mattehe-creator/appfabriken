package se.tmconnect.styrelserummet.server

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class ServerFel {
    data object IngenLasbarText : ServerFel()

    data object KrypteradPdf : ServerFel()

    data object ForStorFil : ServerFel()

    data object IntePdf : ServerFel()

    data object AiTimeout : ServerFel()

    data object AiOgiltigtSvar : ServerFel()

    data class ForMangaJobb(val kanForsokaIgenEpochMs: Long) : ServerFel()

    data object Okant : ServerFel()
}

private val maxMbPerJobb: Int = MAX_BYTES_PER_JOBB / (1024 * 1024)

fun felTillMeddelande(fel: ServerFel): String =
    when (fel) {
        ServerFel.IngenLasbarText ->
            "PDF:en innehåller ingen läsbar text. Ladda upp en textbaserad PDF där du kan markera texten."
        ServerFel.KrypteradPdf ->
            "Filen är lösenordsskyddad. Ladda upp PDF:en olåst, utan lösenord."
        ServerFel.ForStorFil ->
            "Filerna är tillsammans för stora. Total uppladdning får vara högst cirka $maxMbPerJobb MB. " +
                "Prova att ladda upp färre filer eller mindre filer."
        ServerFel.IntePdf ->
            "Bara PDF-filer accepteras. Ladda upp en PDF-fil."
        ServerFel.AiTimeout ->
            "Tjänsten hann inte svara i tid. Försök igen om en liten stund."
        ServerFel.AiOgiltigtSvar ->
            "Komponentförslagen kunde inte tas fram. Kontrollera underhållsplanen och försök igen."
        is ServerFel.ForMangaJobb -> {
            val tid = LocalDateTime.ofInstant(Instant.ofEpochMilli(fel.kanForsokaIgenEpochMs), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            "Du har skickat för många jobb. Gränsen är fem jobb per timme. Försök igen klockan ${tid.format(formatter)}."
        }
        ServerFel.Okant ->
            "Ett okänt fel uppstod. Försök igen och kontakta support om felet kvarstår."
    }

fun loggJobbFel(jobbId: String, fel: ServerFel, tidMs: Long): String {
    val felKod =
        when (fel) {
            ServerFel.IngenLasbarText -> "IngenLasbarText"
            ServerFel.KrypteradPdf -> "KrypteradPdf"
            ServerFel.ForStorFil -> "ForStorFil"
            ServerFel.IntePdf -> "IntePdf"
            ServerFel.AiTimeout -> "AiTimeout"
            ServerFel.AiOgiltigtSvar -> "AiOgiltigtSvar"
            is ServerFel.ForMangaJobb -> "ForMangaJobb"
            ServerFel.Okant -> "Okant"
        }
    return "$jobbId $felKod $tidMs"
}
