package se.tmconnect.styrelserummet.server

object Felmeddelanden {
    fun forklara(kod: String): String =
        when (kod) {
            Felkoder.FOR_MANGA_JOBB ->
                "För många jobb från samma adress. Vänta en timme och försök igen."
            Felkoder.FOR_MANGA_FILER -> "Högst 10 filer per jobb."
            Felkoder.FOR_STORA_FILER -> "Filerna får vara högst 25 MB tillsammans."
            Felkoder.INTE_PDF -> "Bara PDF-filer kan laddas upp."
            Felkoder.FORENING_SAKNAS -> "Ange föreningens namn."
            Felkoder.FORENING_FOR_LANG -> "Föreningens namn får vara högst 120 tecken."
            Felkoder.FORENING_OGILTIGA_TECKEN -> "Föreningens namn innehåller ogiltiga tecken."
            Felkoder.ARSSLUT_SAKNAS -> "Ange räkenskapsårets slut (år)."
            Felkoder.INGA_PDF -> "Ladda upp minst en PDF."
            Felkoder.JOBB_SAKNAS -> "Jobbet finns inte."
            Felkoder.JOBB_EJ_KLART -> "Jobbet är inte klart än."
            Felkoder.SAKNAR_UTKAST -> "Saknar utkast."
            Felkoder.SAKNAR_JOBB_ID -> "Saknar jobb-id."
            Felkoder.OGILIG_LANK -> "Ogiltig länk."
            Felkoder.INGA_FILER -> "Inga filer hittades."
            Felkoder.PDF_EJ_LASBAR ->
                "Filen innehåller ingen läsbar text. Ladda upp en PDF som går att markera text i."
            Felkoder.PDF_LOSENORD ->
                "PDF:en är lösenordsskyddad. Spara en kopia utan lösenord och ladda upp den."
            Felkoder.INGEN_PLAN ->
                "Kunde inte läsa underhållsplanen. Ladda upp minst en PDF med text."
            Felkoder.AI_TIDSGRANS ->
                "Tjänsten hann inte svara i tid. Försök igen om en liten stund."
            Felkoder.AI_OGILTIGT_SVAR ->
                "Komponentförslagen kunde inte tas fram. Försök igen eller ladda upp en tydligare handling."
            Felkoder.BEARBETNING_MISSLYCKADES -> "Bearbetningen misslyckades."
            Felkoder.INTERNT_FEL -> "Något gick fel. Försök igen."
            else -> "Något gick fel. Försök igen."
        }
}
