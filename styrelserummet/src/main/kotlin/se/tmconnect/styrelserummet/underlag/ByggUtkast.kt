package se.tmconnect.styrelserummet.underlag

import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.modell.Utkast
import se.tmconnect.styrelserummet.regler.arInomIntervall
import se.tmconnect.styrelserummet.regler.fordela
import se.tmconnect.styrelserummet.regler.forslagNyttjandeperiod
import se.tmconnect.styrelserummet.regler.stamAv
import se.tmconnect.styrelserummet.regler.standardAndelar
import se.tmconnect.styrelserummet.regler.tillAvstamningDto
import se.tmconnect.styrelserummet.regler.validera

fun byggUtkast(
    underhallsplanText: String,
    arsredovisningText: String,
    forening: Forening,
    arsslut: Int,
    tolkare: Tolkare,
    underhallsplanFilnamn: String = "underhallsplan.pdf",
): Utkast {
    val maskadPlan = maskaPersonuppgifter(underhallsplanText)
    val maskadAr = maskaPersonuppgifter(arsredovisningText)
    val byggnad = hittaByggnadensVarde(maskadAr)
    val byggnadensVarde = byggnad?.kronor ?: 0L

    val anmarkningar = mutableListOf<String>()
    if (byggnad == null) {
        anmarkningar += "Kunde inte hitta byggnadens värde i årsredovisningen. Kontrollera att balansräkningen finns med."
    }

    val sidor = when {
        maskadPlan.contains('\u000c') -> maskadPlan.split('\u000c')
        else -> listOf(maskadPlan)
    }
    val delar = delaUpp(sidor, 12_000)
    val allaForslag = mutableListOf<RadForslag>()
    val texter = if (delar.isEmpty()) listOf(maskadPlan) else delar.map { it.text }
    texter.forEachIndexed { idx, text ->
        val fil = if (texter.size == 1) underhallsplanFilnamn else "$underhallsplanFilnamn del ${idx + 1}"
        val svar = tolkare.foreslaKomponenter(text, fil)
        if (svar.isFailure) {
            anmarkningar += svar.exceptionOrNull()?.message ?: "Tolkningen misslyckades."
        } else {
            val f = svar.getOrThrow()
            anmarkningar += f.anmarkningar
            allaForslag += f.komponenter
        }
    }

    var komponenter = byggKomponenter(allaForslag, forening, arsslut, anmarkningar)
    komponenter = fyllUppskattning(komponenter, byggnadensVarde, arsslut, anmarkningar)
    val avst = stamAv(komponenter, byggnadensVarde)
    anmarkningar += anmarkningarFor(avst, komponenter)

    return Utkast(
        forening = forening.namn,
        arsslut = arsslut,
        byggnadensVarde = byggnadensVarde,
        komponenter = komponenter,
        avstamning = tillAvstamningDto(avst),
        anmarkningar = anmarkningar.distinct(),
    )
}

fun uppdateraKomponenter(
    komponenter: List<Komponent>,
    byggnadensVarde: Long,
    forening: String,
    arsslut: Int,
): Utkast {
    val anmarkningar = mutableListOf<String>()
    komponenter.forEach { k ->
        val fel = validera(k, arsslut)
        fel.forEach { anmarkningar += it.meddelande }
    }
    val avst = stamAv(komponenter, byggnadensVarde)
    anmarkningar += anmarkningarFor(avst, komponenter)
    return Utkast(
        forening = forening,
        arsslut = arsslut,
        byggnadensVarde = byggnadensVarde,
        komponenter = komponenter,
        avstamning = tillAvstamningDto(avst),
        anmarkningar = anmarkningar.distinct(),
    )
}

private fun byggKomponenter(
    forslag: List<RadForslag>,
    forening: Forening,
    arsslut: Int,
    anmarkningar: MutableList<String>,
): List<Komponent> {
    val nuAr = arsslut
    return forslag.mapNotNull { rad ->
        val grupp = Komponentgrupp.franNamn(rad.grupp) ?: Komponentgrupp.OVRIGT
        val varde = rad.anskaffningsvarde
        val ar = rad.anskaffningsar ?: forening.byggnadsar ?: (arsslut - 10)
        val period = rad.nyttjandeperiodAr ?: forslagNyttjandeperiod(grupp) ?: 40
        if (varde == null) {
            return@mapNotNull null
        }
        val sakerhet = runCatching { Sakerhet.valueOf(rad.sakerhet) }.getOrDefault(Sakerhet.LAG)
        Komponent(
            namn = rad.namn,
            grupp = grupp.name,
            anskaffningsvarde = varde,
            anskaffningsar = ar,
            nyttjandeperiodAr = period,
            kalla = rad.kalla,
            sakerhet = sakerhet.name,
        ).also {
            val fel = validera(it, nuAr)
            fel.forEach { f -> anmarkningar += f.meddelande }
        }
    }
}

private fun fyllUppskattning(
    komponenter: List<Komponent>,
    byggnadensVarde: Long,
    arsslut: Int,
    anmarkningar: MutableList<String>,
): List<Komponent> {
    val lista = komponenter.toMutableList()
    if (byggnadensVarde <= 0) return lista
    val summa = lista.sumOf { it.anskaffningsvarde }
    val kvar = byggnadensVarde - summa
    if (kvar <= 0) return lista
    val andelar = standardAndelar().filter { (g, p) -> p > 0 && lista.none { it.gruppEnum() == g } }
    if (andelar.isEmpty()) {
        anmarkningar += "Byggnadens värde är inte fullt fördelat ($kvar kr kvar)."
        return lista
    }
    val summaProcent = andelar.sumOf { it.second }
    val normaliserade = andelar.map { (g, p) -> g to (p * 100 / summaProcent) }
    val fix = normaliserade.toMutableList()
    val diff = 100 - fix.sumOf { it.second }
    if (diff != 0 && fix.isNotEmpty()) {
        val storsta = fix.maxBy { it.second }
        fix[fix.indexOf(storsta)] = storsta.first to (storsta.second + diff)
    }
    val ford = fordela(kvar, fix)
    ford.forEach { (grupp, belopp) ->
        if (belopp <= 0) return@forEach
        lista += Komponent(
            namn = grupp.svensktNamn,
            grupp = grupp.name,
            anskaffningsvarde = belopp,
            anskaffningsar = arsslut - (forslagNyttjandeperiod(grupp) ?: 40) + 1,
            nyttjandeperiodAr = forslagNyttjandeperiod(grupp) ?: 40,
            kalla = "Standardfördelning enligt tabell",
            sakerhet = Sakerhet.UPPSKATTNING.name,
        )
        anmarkningar += "${grupp.svensktNamn} har uppskattats enligt standardtabellen."
    }
    return lista
}

fun anmarkningarFor(
    avst: se.tmconnect.styrelserummet.modell.Avstamning,
    komponenter: List<Komponent>,
): List<String> {
    val list = mutableListOf<String>()
    if (!avst.stammer && avst.byggnadensVarde > 0) {
        list += "Summan av komponenterna skiljer sig från byggnadens värde med ${avst.differens} kr."
    }
    komponenter.filter { it.sakerhetEnum() == Sakerhet.LAG }.forEach {
        list += "${it.namn}: osäker rad, kontrollera mot underlaget."
    }
    komponenter.filter { it.sakerhetEnum() == Sakerhet.UPPSKATTNING }.forEach {
        list += "${it.namn}: värdet är uppskattat enligt standardtabellen."
    }
    komponenter.forEach { k ->
        if (!arInomIntervall(k.gruppEnum(), k.nyttjandeperiodAr)) {
            list += "${k.namn}: nyttjandeperioden ligger utanför typintervallet för ${k.gruppEnum().svensktNamn}."
        }
    }
    return list
}
