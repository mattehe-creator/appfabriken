package se.tmconnect.styrelserummet.underlag

data class Del(val sidor: List<Int>, val text: String)

fun delaUpp(sidor: List<String>, maxTecken: Int): List<Del> {
    if (sidor.isEmpty()) return emptyList()
    val resultat = mutableListOf<Del>()
    var buf = StringBuilder()
    var sidLista = mutableListOf<Int>()
    sidor.forEachIndexed { idx, sidaText ->
        val sidnr = idx + 1
        if (sidaText.length > maxTecken) {
            if (buf.isNotEmpty()) {
                resultat += Del(sidLista.toList(), buf.toString())
                buf = StringBuilder()
                sidLista = mutableListOf()
            }
            resultat += Del(listOf(sidnr), sidaText)
            return@forEachIndexed
        }
        val behoverNy = buf.isNotEmpty() && buf.length + sidaText.length + 2 > maxTecken
        if (behoverNy) {
            resultat += Del(sidLista.toList(), buf.toString())
            buf = StringBuilder()
            sidLista = mutableListOf()
        }
        if (buf.isNotEmpty()) buf.append("\n\n")
        buf.append(sidaText)
        sidLista += sidnr
    }
    if (buf.isNotEmpty()) {
        resultat += Del(sidLista.toList(), buf.toString())
    }
    return resultat
}
