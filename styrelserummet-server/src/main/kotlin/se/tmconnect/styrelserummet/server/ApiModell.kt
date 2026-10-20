package se.tmconnect.styrelserummet.server

import kotlinx.serialization.Serializable
import se.tmconnect.styrelserummet.modell.Komponent
import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.modell.Sakerhet
import se.tmconnect.styrelserummet.regler.stamAv
import se.tmconnect.styrelserummet.underlag.Utkast

@Serializable
data class KomponentDto(
    val namn: String,
    val grupp: String,
    val anskaffningsvarde: Long,
    val anskaffningsar: Int,
    val nyttjandeperiodAr: Int,
    val restvarde: Long = 0,
    val kalla: String,
    val sakerhet: String,
)

@Serializable
data class AvstamningDto(
    val summa: Long,
    val byggnadensVarde: Long,
    val differens: Long,
    val stammer: Boolean,
)

@Serializable
data class UtkastDto(
    val forening: String,
    val arsslut: Int,
    val byggnadensVarde: Long,
    val komponenter: List<KomponentDto>,
    val avstamning: AvstamningDto,
    val anmarkningar: List<String>,
)

fun tillDto(k: Komponent): KomponentDto =
    KomponentDto(
        namn = k.namn,
        grupp = k.grupp.name,
        anskaffningsvarde = k.anskaffningsvarde,
        anskaffningsar = k.anskaffningsar,
        nyttjandeperiodAr = k.nyttjandeperiodAr,
        restvarde = k.restvarde,
        kalla = k.kalla,
        sakerhet = k.sakerhet.name,
    )

fun franDto(d: KomponentDto): Komponent {
    val grupp = runCatching { Komponentgrupp.valueOf(d.grupp) }.getOrDefault(Komponentgrupp.OVRIGT)
    val sakerhet = runCatching { Sakerhet.valueOf(d.sakerhet) }.getOrDefault(Sakerhet.MEDEL)
    return Komponent(
        namn = d.namn,
        grupp = grupp,
        anskaffningsvarde = d.anskaffningsvarde,
        anskaffningsar = d.anskaffningsar,
        nyttjandeperiodAr = d.nyttjandeperiodAr,
        restvarde = d.restvarde,
        kalla = d.kalla,
        sakerhet = sakerhet,
    )
}

fun tillUtkastDto(utkast: Utkast, arsslut: Int): UtkastDto {
    val bygg = utkast.byggnadensVarde ?: 0L
    val avst = utkast.avstamning ?: stamAv(utkast.komponenter, bygg)
    return UtkastDto(
        forening = utkast.forening.namn,
        arsslut = arsslut,
        byggnadensVarde = bygg,
        komponenter = utkast.komponenter.map(::tillDto),
        avstamning = AvstamningDto(
            summa = avst.summa,
            byggnadensVarde = bygg,
            differens = avst.differens,
            stammer = avst.stammer,
        ),
        anmarkningar = utkast.anmarkningar,
    )
}
