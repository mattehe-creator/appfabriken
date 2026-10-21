package se.tmconnect.styrelserummet.server

object Felkoder {
    const val FOR_MANGA_JOBB = "FOR_MANGA_JOBB"
    const val FOR_MANGA_FILER = "FOR_MANGA_FILER"
    const val FOR_STORA_FILER = "FOR_STORA_FILER"
    const val INTE_PDF = "INTE_PDF"
    const val FORENING_SAKNAS = "FORENING_SAKNAS"
    const val FORENING_FOR_LANG = "FORENING_FOR_LANG"
    const val FORENING_OGILTIGA_TECKEN = "FORENING_OGILTIGA_TECKEN"
    const val ARSSLUT_SAKNAS = "ARSSLUT_SAKNAS"
    const val INGA_PDF = "INGA_PDF"
    const val JOBB_SAKNAS = "JOBB_SAKNAS"
    const val JOBB_EJ_KLART = "JOBB_EJ_KLART"
    const val SAKNAR_UTKAST = "SAKNAR_UTKAST"
    const val SAKNAR_JOBB_ID = "SAKNAR_JOBB_ID"
    const val OGILIG_LANK = "OGILIG_LANK"
    const val INGA_FILER = "INGA_FILER"
    const val PDF_EJ_LASBAR = "PDF_EJ_LASBAR"
    const val PDF_LOSENORD = "PDF_LOSENORD"
    const val INGEN_PLAN = "INGEN_PLAN"
    const val AI_TIDSGRANS = "AI_TIDSGRANS"
    const val AI_OGILTIGT_SVAR = "AI_OGILTIGT_SVAR"
    const val BEARBETNING_MISSLYCKADES = "BEARBETNING_MISSLYCKADES"
    const val INTERNT_FEL = "INTERNT_FEL"

    fun allaKoder(): Set<String> = setOf(
        FOR_MANGA_JOBB,
        FOR_MANGA_FILER,
        FOR_STORA_FILER,
        INTE_PDF,
        FORENING_SAKNAS,
        FORENING_FOR_LANG,
        FORENING_OGILTIGA_TECKEN,
        ARSSLUT_SAKNAS,
        INGA_PDF,
        JOBB_SAKNAS,
        JOBB_EJ_KLART,
        SAKNAR_UTKAST,
        SAKNAR_JOBB_ID,
        OGILIG_LANK,
        INGA_FILER,
        PDF_EJ_LASBAR,
        PDF_LOSENORD,
        INGEN_PLAN,
        AI_TIDSGRANS,
        AI_OGILTIGT_SVAR,
        BEARBETNING_MISSLYCKADES,
        INTERNT_FEL,
    )
}
