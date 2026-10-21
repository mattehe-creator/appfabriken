package se.tmconnect.styrelserummet.server

fun serverFelTillKod(fel: ServerFel): String =
    when (fel) {
        ServerFel.IngenLasbarText -> Felkoder.PDF_EJ_LASBAR
        ServerFel.KrypteradPdf -> Felkoder.PDF_LOSENORD
        ServerFel.ForStorFil -> Felkoder.FOR_STORA_FILER
        ServerFel.IntePdf -> Felkoder.INTE_PDF
        ServerFel.AiTimeout -> Felkoder.AI_TIDSGRANS
        ServerFel.AiOgiltigtSvar -> Felkoder.AI_OGILTIGT_SVAR
        is ServerFel.ForMangaJobb -> Felkoder.FOR_MANGA_JOBB
        ServerFel.Okant -> Felkoder.INTERNT_FEL
    }

fun felText(kod: String): String = Felmeddelanden.forklara(kod)

fun foreningFelKod(namn: String): String? =
    when (valideraForeningsnamn(namn)) {
        null -> null
        "Ange föreningens namn." -> Felkoder.FORENING_SAKNAS
        "Föreningens namn får vara högst 120 tecken." -> Felkoder.FORENING_FOR_LANG
        "Föreningens namn innehåller ogiltiga tecken." -> Felkoder.FORENING_OGILTIGA_TECKEN
        else -> Felkoder.FORENING_SAKNAS
    }
