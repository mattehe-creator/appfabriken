package se.tmconnect.garantivalvet.regler

/** Butikens kontaktuppgifter ur kvittot, till nytta när man ska reklamera. Alla fält är valfria. */
data class ButikKontakt(
    val epost: String? = null,
    val telefon: String? = null,
    val orgnr: String? = null,
) {
    val arTom: Boolean get() = epost.isNullOrBlank() && telefon.isNullOrBlank() && orgnr.isNullOrBlank()
}

private val EPOST = Regex("""[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}""")
private val TELEFON_ORD = Regex("""tel|telefon|phone|tfn|kundtj[äa]nst|kundservice""", RegexOption.IGNORE_CASE)
private val TELEFON = Regex("""(?<!\d)(?:\+46|0)[\s-]?\d{1,3}(?:[\s-]?\d{2,3}){2,3}(?!\d)""")
private val ORG_ORD = Regex("""\borg""", RegexOption.IGNORE_CASE)
private val ORGNR = Regex("""(?<!\d)(\d{6})\s?-\s?(\d{4})(?!\d)""")

fun hittaButikKontakt(text: String): ButikKontakt {
    val rader = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
    return ButikKontakt(
        epost = EPOST.find(text)?.value?.lowercase(),
        telefon = hittaTelefon(rader),
        orgnr = hittaOrgnr(rader),
    )
}

/** Raden med nyckelordet, eller raden efter om numret står på en egen rad (kvitton fotade på sidan). */
private fun <T : Any> forstaTraffEfterNyckelord(rader: List<String>, ord: Regex, hitta: (String) -> T?): T? {
    rader.forEachIndexed { i, rad ->
        if (ord.containsMatchIn(rad)) {
            hitta(rad)?.let { return it }
            rader.getOrNull(i + 1)?.let(hitta)?.let { return it }
        }
    }
    return null
}

private fun hittaTelefon(rader: List<String>): String? =
    forstaTraffEfterNyckelord(rader, TELEFON_ORD) { rad ->
        TELEFON.find(rad)?.value?.replace(Regex("""\s+"""), " ")?.trim()
    }

/**
 * Organisationsnummer med nyckelordet "org" på raden. Tredje och fjärde siffran är minst 20 för organisationsnummer,
 * men en månad (01–12) för personnummer. Enskilda firmor kan ha personnummer som organisationsnummer: sådana sparas
 * aldrig.
 */
private fun hittaOrgnr(rader: List<String>): String? =
    forstaTraffEfterNyckelord(rader, ORG_ORD) { rad ->
        ORGNR.find(rad)?.let { m ->
            val (forsta, andra) = m.destructured
            if (forsta.substring(2, 4).toInt() >= 20) "$forsta-$andra" else null
        }
    }
