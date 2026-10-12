package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

enum class ListaGrupp {
    GAR_UT_SNART,
    GALLER,
    UTGANGNA,
}

fun listaGrupp(kop: Kop, idag: LocalDate): ListaGrupp {
    return when (statusForKop(kop, idag)) {
        KopStatus.GARANTI_SNART -> ListaGrupp.GAR_UT_SNART
        KopStatus.FARSKVARA ->
            if (aktivtOppetKop(kop, idag) != null) ListaGrupp.GALLER else ListaGrupp.UTGANGNA
        KopStatus.GARANTI_GALLER,
        KopStatus.REKLAMATION_GALLER,
        -> ListaGrupp.GALLER
        KopStatus.UTGANGEN -> ListaGrupp.UTGANGNA
    }
}
