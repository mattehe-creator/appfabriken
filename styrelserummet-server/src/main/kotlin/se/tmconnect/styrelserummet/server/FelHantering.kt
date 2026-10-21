package se.tmconnect.styrelserummet.server

sealed class ServerFel {
    data object IngenLasbarText : ServerFel()

    data object KrypteradPdf : ServerFel()
}

fun felTillMeddelande(fel: ServerFel): String =
    when (fel) {
        ServerFel.IngenLasbarText ->
            "PDF:en innehåller ingen läsbar text. Ladda upp en textbaserad PDF där du kan markera texten."
        ServerFel.KrypteradPdf ->
            "Filen är lösenordsskyddad. Ladda upp PDF:en olåst, utan lösenord."
    }

fun loggJobbFel(jobbId: String, fel: ServerFel, tidMs: Long): String {
    val felKod =
        when (fel) {
            ServerFel.IngenLasbarText -> "IngenLasbarText"
            ServerFel.KrypteradPdf -> "KrypteradPdf"
        }
    return "$jobbId $felKod $tidMs"
}
