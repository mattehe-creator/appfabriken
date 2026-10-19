package se.tmconnect.styrelserummet.server

import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.stamAv
import se.tmconnect.styrelserummet.regler.validera

fun uppdateraKomponenter(
    komponenter: List<KomponentDto>,
    gammalt: UtkastDto,
): UtkastDto {
    val modell = komponenter.map(::franDto)
    val anmarkningar = mutableListOf<String>()
    modell.forEach { k ->
        val fel = validera(k, gammalt.arsslut)
        fel.forEach { anmarkningar += "${k.namn}: ${it.meddelande}" }
    }
    val avst = stamAv(modell, gammalt.byggnadensVarde)
    if (!avst.stammer && gammalt.byggnadensVarde > 0) {
        anmarkningar +=
            "Summan av komponenterna skiljer sig från byggnadens värde med ${avst.differens} kr."
    }
    modell.filter { it.sakerhet == Sakerhet.LAG }.forEach {
        anmarkningar += "${it.namn}: osäker rad, kontrollera mot underlaget."
    }
    modell.filter { it.sakerhet == Sakerhet.UPPSKATTNING }.forEach {
        anmarkningar += "${it.namn}: värdet är uppskattat enligt standardtabellen."
    }
    return gammalt.copy(
        komponenter = modell.map(::tillDto),
        avstamning = AvstamningDto(
            summa = avst.summa,
            byggnadensVarde = gammalt.byggnadensVarde,
            differens = avst.differens,
            stammer = avst.stammer,
        ),
        anmarkningar = anmarkningar.distinct(),
    )
}
