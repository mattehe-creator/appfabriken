package se.tmconnect.styrelserummet.modell

enum class Sakerhet {
    /** Värde eller period hämtad tydligt från underhållsplan eller årsredovisning. */
    HOG,

    /** Rimlig tolkning av handlingarna. */
    MEDEL,

    /** Svagt underlag; bör kontrolleras. */
    LAG,

    /** Värde fördelat av tjänsten enligt standardtabell eller fördelningsregler. */
    UPPSKATTNING,
}
