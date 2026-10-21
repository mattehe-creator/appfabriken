package se.tmconnect.styrelserummet.server

fun serverFelTillKod(fel: ServerFel): String =
    when (fel) {
        ServerFel.IngenLasbarText -> "IngenLasbarText"
        ServerFel.KrypteradPdf -> "KrypteradPdf"
        ServerFel.ForStorFil -> "ForStorFil"
        ServerFel.IntePdf -> "IntePdf"
        ServerFel.AiTimeout -> "AiTimeout"
        ServerFel.AiOgiltigtSvar -> "AiOgiltigtSvar"
        is ServerFel.ForMangaJobb -> "ForMangaJobb"
        ServerFel.ForStorHandling -> "ForStorHandling"
        ServerFel.Okant -> "Okant"
    }
