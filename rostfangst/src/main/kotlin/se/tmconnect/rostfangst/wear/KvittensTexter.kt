package se.tmconnect.rostfangst.wear

enum class Overforing {
    VANTAR,
    SKICKAD,
    INGEN_TELEFON,
    FEL
}

fun kvittensText(tillstand: Overforing): String {
    return when (tillstand) {
        Overforing.VANTAR -> "Skickar…"
        Overforing.SKICKAD -> "Skickat till telefonen"
        Overforing.INGEN_TELEFON -> "Ingen telefon i närheten"
        Overforing.FEL -> "Kunde inte skicka"
    }
}
