package se.tmconnect.styrelserummet.regler

import java.time.DayOfWeek
import java.time.LocalDate

private fun svenskVeckodag(dag: DayOfWeek): String = when (dag) {
    DayOfWeek.MONDAY -> "måndag"
    DayOfWeek.TUESDAY -> "tisdag"
    DayOfWeek.WEDNESDAY -> "onsdag"
    DayOfWeek.THURSDAY -> "torsdag"
    DayOfWeek.FRIDAY -> "fredag"
    DayOfWeek.SATURDAY -> "lördag"
    DayOfWeek.SUNDAY -> "söndag"
}

fun datumMedVeckodag(d: LocalDate): String {
    return "${d} (${svenskVeckodag(d.dayOfWeek)})"
}

data class StammaTabellRad(val etikett: String, val datum: String, val anmarkning: String, val kalla: String)

const val STAMMA_RADGIVNING_RAD = "Allmän information, inte juridisk rådgivning. Kontrollera era stadgar."

private fun csvFalt(text: String): String {
    if (text.none { it == ';' || it == '"' || it == '\n' || it == '\r' }) {
        return text
    }
    return "\"" + text.replace("\"", "\"\"") + "\""
}

fun tidplanCsv(rader: List<StammaTabellRad>): String {
    val header = "etikett;datum;anmärkning;källa"
    val dataRader = rader.map { rad ->
        listOf(rad.etikett, rad.datum, rad.anmarkning, rad.kalla)
            .joinToString(";") { csvFalt(it) }
    }
    return (listOf(header) + dataRader + STAMMA_RADGIVNING_RAD).joinToString("\n") + "\n"
}

fun tidplanRader(plan: Stammotidplan, kallelseVeckor: Int): List<StammaTabellRad> {
    val efterLagfristRad1 = "Stämman ligger efter lagfrist"
    val efterLagfristRad2 = "Efter lagens senaste datum"
    val kallelseAnmarkning = "Kallelsen ska utfärdas senast $kallelseVeckor veckor före stämman"

    return listOf(
        StammaTabellRad(
            etikett = "Senaste dag för ordinarie stämma",
            datum = datumMedVeckodag(plan.senasteStammodag),
            anmarkning = if (plan.stammaInomLagligFrist) "" else efterLagfristRad1,
            kalla = "EFL 6 kap. 9 §",
        ),
        StammaTabellRad(
            etikett = "Planerat stämmodatum",
            datum = datumMedVeckodag(plan.stammodatum),
            anmarkning = if (plan.stammaInomLagligFrist) "" else efterLagfristRad2,
            kalla = "",
        ),
        StammaTabellRad(
            etikett = "Tidigaste kallelsedag",
            datum = datumMedVeckodag(plan.tidigasteKallelsedag),
            anmarkning = "Ordinarie stämma",
            kalla = "EFL 6 kap. 17 §",
        ),
        StammaTabellRad(
            etikett = "Senaste kallelsedag",
            datum = datumMedVeckodag(plan.senasteKallelsedag),
            anmarkning = kallelseAnmarkning,
            kalla = "EFL 6 kap. 17 §",
        ),
        StammaTabellRad(
            etikett = "Revisionsberättelse till styrelsen senast",
            datum = datumMedVeckodag(plan.senasteRevisionTillStyrelsen),
            anmarkning = "",
            kalla = "EFL 8 kap. 32 §",
        ),
        StammaTabellRad(
            etikett = "Årsredovisning tillgänglig från",
            datum = datumMedVeckodag(plan.forstaDagArsredovisningTillganglig),
            anmarkning = "Minst två veckor före stämman",
            kalla = "EFL 6 kap. 23 §",
        ),
        StammaTabellRad(
            etikett = "Räkenskapsårets slut",
            datum = datumMedVeckodag(plan.arsslut),
            anmarkning = "",
            kalla = "",
        ),
    )
}
