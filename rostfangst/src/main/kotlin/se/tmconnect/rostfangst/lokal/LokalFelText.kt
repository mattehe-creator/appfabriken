package se.tmconnect.rostfangst.lokal

enum class Kalla { TOLKNING, TALIGENKANNING }

fun lokalFelText(fel: LokalFel, kalla: Kalla): String {
    return when (kalla) {
        Kalla.TOLKNING -> when (fel) {
            LokalFel.NATFEL -> "Datorn svarade inte. Kontrollera att den är på och att adressen stämmer."
            LokalFel.TIMEOUT -> "Datorn tog för lång tid på sig att svara."
            LokalFel.OGILTIGT_SVAR -> "Datorn gav ett svar som inte gick att tolka."
        }
        Kalla.TALIGENKANNING -> when (fel) {
            LokalFel.NATFEL -> "Taligenkänningen på datorn svarade inte. Kontrollera att den är på och att adressen stämmer."
            LokalFel.TIMEOUT -> "Taligenkänningen på datorn tog för lång tid på sig."
            LokalFel.OGILTIGT_SVAR -> "Taligenkänningen på datorn gav inget läsbart svar."
        }
    }
}
