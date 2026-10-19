package se.tmconnect.styrelserummet.underlag

data class Del(
    val sidnummer: List<Int>,
    val text: String,
)

fun delaUpp(sidor: List<String>, maxTecken: Int): List<Del> {
    require(maxTecken > 0) { "maxTecken måste vara positivt" }
    if (sidor.isEmpty()) return emptyList()
    val resultat = mutableListOf<Del>()
    var aktuellaSidor = mutableListOf<Int>()
    var aktuellText = StringBuilder()

    fun spara() {
        if (aktuellaSidor.isEmpty()) return
        resultat += Del(aktuellaSidor.toList(), aktuellText.toString())
        aktuellaSidor = mutableListOf()
        aktuellText = StringBuilder()
    }

    sidor.forEachIndexed { index, sida ->
        val sidnr = index + 1
        if (sida.length > maxTecken) {
            spara()
            resultat += Del(listOf(sidnr), sida)
            return@forEachIndexed
        }
        val separator = if (aktuellText.isEmpty()) "" else "\n\n"
        val nyLangd = aktuellText.length + separator.length + sida.length
        if (nyLangd > maxTecken && aktuellaSidor.isNotEmpty()) {
            spara()
        }
        if (aktuellText.isNotEmpty()) aktuellText.append("\n\n")
        aktuellaSidor += sidnr
        aktuellText.append(sida)
    }
    spara()
    return resultat
}
