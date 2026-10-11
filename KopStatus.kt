package se.tmconnect.garantivalvet.regler

import java.time.LocalDate

enum class KopStatus {
    GARANTI_GALLER,
    GARANTI_SNART,
    REKLAMATION_GALLER,
    UTGANGEN
}

fun kopStatus(kopdatum: LocalDate, garantiManader: Int, idag: LocalDate): KopStatus {
    val garantiStatus = garantiStatus(kopdatum, garantiManader, idag)
    
    return when (garantiStatus) {
        GarantiStatus.GALLER -> KopStatus.GARANTI_GALLER
        GarantiStatus.GAR_UT_SNART -> KopStatus.GARANTI_SNART
        GarantiStatus.UTGANGEN -> {
            val reklamationStatus = reklamationStatus(kopdatum, idag)
            when (reklamationStatus) {
                ReklamationStatus.GALLER, 
                ReklamationStatus.FEL_ANTAS_FUNNITS -> KopStatus.REKLAMATION_GALLER
                else -> KopStatus.UTGANGEN
            }
        }
    }
}
