package se.tmconnect.garantivalvet.regler

import java.time.LocalDate

/** Ikraftträdande för konsumentköplagen (2022:260). Köp före detta datum omfattas inte av dessa regler i appen. */
val KONSUMENTKOP_IKRAFT: LocalDate = LocalDate.of(2022, 5, 1)

/** 4 kap. 14 § — näringsidkaren svarar för fel som visar sig inom tre år från avlämnandet. */
const val REKLAMATION_AR: Long = 3

/** 4 kap. 17 § — fel som visar sig inom denna tid antas ha funnits vid köpet. */
const val PRESUMTION_AR: Long = 2

/** 5 kap. 2 § — reklamation inom denna tid från att felet märktes anses i rätt tid. */
const val REKLAMATION_INOM_MANADER: Long = 2

enum class ReklamationStatus {
    FEL_ANTAS_FUNNITS,
    GALLER,
    UTGANGEN,
    EJ_TILLAMPLIG,
}

fun reklamationSlut(kopdatum: LocalDate): LocalDate? {
    if (kopdatum.isBefore(KONSUMENTKOP_IKRAFT)) {
        return null
    }
    return kopdatum.plusYears(REKLAMATION_AR)
}

fun presumtionSlut(kopdatum: LocalDate): LocalDate? {
    if (kopdatum.isBefore(KONSUMENTKOP_IKRAFT)) {
        return null
    }
    return kopdatum.plusYears(PRESUMTION_AR)
}

fun reklamationStatus(kopdatum: LocalDate, idag: LocalDate): ReklamationStatus {
    val slut = reklamationSlut(kopdatum) ?: return ReklamationStatus.EJ_TILLAMPLIG
    if (idag.isAfter(slut)) {
        return ReklamationStatus.UTGANGEN
    }
    val presumtion = presumtionSlut(kopdatum)!!
    if (idag.isAfter(presumtion)) {
        return ReklamationStatus.GALLER
    }
    return ReklamationStatus.FEL_ANTAS_FUNNITS
}
