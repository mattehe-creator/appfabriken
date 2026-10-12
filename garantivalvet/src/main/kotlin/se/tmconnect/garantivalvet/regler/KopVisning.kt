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
}

data class NarmasteFrist(
    val typ: NarmasteFristTyp,
    val datum: LocalDate,
)

fun narmasteFrist(kop: Kop, idag: LocalDate): NarmasteFrist? {
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
