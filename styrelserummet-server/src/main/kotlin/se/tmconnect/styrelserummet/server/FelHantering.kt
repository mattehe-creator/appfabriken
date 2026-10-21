package se.tmconnect.styrelserummet.server

sealed class ServerFel {
    data object IngenLasbarText : ServerFel()

    data object KrypteradPdf : ServerFel()

    data object ForStorFil : ServerFel()

    data object IntePdf : ServerFel()

    data object AiTimeout : ServerFel()

    data object AiOgiltigtSvar : ServerFel()
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
        }
    return "$jobbId $felKod $tidMs"
}
