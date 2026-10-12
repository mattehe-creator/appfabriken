package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.karna.tema.AppfabrikStatus

fun visaMedVersal(text: String): String {
    if (text.isEmpty()) {
        return text
    }
    return text.first().uppercaseChar() + text.drop(1)
}

fun appfabrikStatusFran(kopStatus: KopStatus): AppfabrikStatus =
    when (kopStatus) {
        KopStatus.GARANTI_GALLER -> AppfabrikStatus.GALLER
        KopStatus.GARANTI_SNART -> AppfabrikStatus.SNART
        KopStatus.REKLAMATION_GALLER -> AppfabrikStatus.REKLAMATION
        KopStatus.UTGANGEN -> AppfabrikStatus.UTGANGEN
    }

enum class NarmasteFristTyp {
    GARANTI,
    REKLAMATION,
    OPPET_KOP,
}

data class NarmasteFrist(
    val typ: NarmasteFristTyp,
    val datum: LocalDate,
)

/** Öppet köp som ännu inte gått ut, eller `null`. */
fun aktivtOppetKop(kop: Kop, idag: LocalDate): NarmasteFrist? {
    val dagar = giltigaOppetKopDagar(kop.oppetKopDagar) ?: return null
    val slut = oppetKopSlut(kop.kopdatum, dagar)
    return if (idag.isAfter(slut)) null else NarmasteFrist(NarmasteFristTyp.OPPET_KOP, slut)
}

/** Garantin om den gäller, annars reklamationsrätten om den gäller. Öppet köp räknas inte hit. */
private fun garantiEllerReklamation(kop: Kop, idag: LocalDate): NarmasteFrist? {
    val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
    if (!idag.isAfter(garantiSlutDatum)) {
        return NarmasteFrist(NarmasteFristTyp.GARANTI, garantiSlutDatum)
    }
    val reklamationSlutDatum = reklamationSlut(kop.kopdatum)
    if (reklamationSlutDatum != null && !idag.isAfter(reklamationSlutDatum)) {
        return NarmasteFrist(NarmasteFristTyp.REKLAMATION, reklamationSlutDatum)
    }
    return null
}

/** Den frist som går ut först av öppet köp (om det gäller) och garanti/reklamation. */
fun narmasteFrist(kop: Kop, idag: LocalDate): NarmasteFrist? =
    listOfNotNull(aktivtOppetKop(kop, idag), garantiEllerReklamation(kop, idag)).minByOrNull { it.datum }
