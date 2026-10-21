package se.tmconnect.styrelserummet.underlag

private val personnummer = Regex(
    """(?<!\d)(?:\d{6}|\d{8})[\s-]?\d{4}\+?(?!\d)""",
)
private val ePost = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")
private val telefon = Regex(
    """(?:\+46\s?(?:\d[\s-]?){8,11}|0\d{1,3}[\s-]?(?:\d[\s-]?){5,10})""",
)
private val isoDatum = Regex("""\d{4}-\d{2}-\d{2}""")
private val tvaRapporteringsar = Regex("""(?<!\d)\d{4}\s+\d{4}(?!\d)""")
private val belopMedMellanslag = Regex("""\d{1,3}(?: \d{3}){2,}""")
private val belopMedPunkt = Regex("""\d{1,3}(?:\.\d{3}){2,}""")

fun maskaPersonuppgifter(text: String): String {
    var resultat = text
    resultat = personnummer.replace(resultat) { match ->
        if (serUtSomPersonnummer(match.value)) "[personnummer]" else match.value
    }
    resultat = ePost.replace(resultat, "[e-post]")
    resultat = telefon.replace(resultat) { match ->
        if (serUtSomTelefon(resultat, match.range)) "[telefon]" else match.value
    }
    return resultat
}

internal fun serUtSomPersonnummer(text: String): Boolean {
    val digits = text.filter { it.isDigit() }
    return when (digits.length) {
        10 -> arGiltigtPersonDatum(digits.substring(0, 6))
        12 -> arGiltigtPersonDatum(digits.substring(0, 8))
        else -> false
    }
}

internal fun serUtSomTelefon(fullText: String, range: IntRange): Boolean {
    if (overlapparIsoDatum(fullText, range)) return false
    if (overlapparRegex(fullText, range, tvaRapporteringsar)) return false
    if (overlapparRegex(fullText, range, belopMedMellanslag)) return false
    if (overlapparRegex(fullText, range, belopMedPunkt)) return false
    return true
}

private fun overlapparRegex(fullText: String, range: IntRange, regex: Regex): Boolean {
    for (match in regex.findAll(fullText)) {
        if (range.first <= match.range.last && range.last >= match.range.first) {
            return true
        }
    }
    return false
}

private fun overlapparIsoDatum(fullText: String, range: IntRange): Boolean {
    for (match in isoDatum.findAll(fullText)) {
        if (range.first <= match.range.last && range.last >= match.range.first) {
            return true
        }
    }
    return false
}

private fun arGiltigtPersonDatum(ymd: String): Boolean {
    val (mm, dd) = when (ymd.length) {
        6 -> ymd.substring(2, 4) to ymd.substring(4, 6)
        8 -> ymd.substring(4, 6) to ymd.substring(6, 8)
        else -> return false
    }
    val manad = mm.toIntOrNull() ?: return false
    val dag = dd.toIntOrNull() ?: return false
    if (manad !in 1..12) return false
    return dag in 1..31 || dag in 61..91
}
