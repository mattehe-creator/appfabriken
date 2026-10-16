package se.tmconnect.husloggen.regler

import androidx.annotation.StringRes
import se.tmconnect.husloggen.R

enum class IntervallBestamdAv {
    INGEN,
    KOMMUNEN,
    TILLVERKAREN,
}

data class UppgiftsForslag(
    val id: String,
    @StringRes val namnResId: Int,
    val intervallManader: Int,
    val bestamdAv: IntervallBestamdAv,
)

val UPPGIFTSFORSLAG: List<UppgiftsForslag> =
    listOf(
        UppgiftsForslag("rensa_hangrannor", R.string.forslag_rensa_hangrannor, 12, IntervallBestamdAv.INGEN),
        UppgiftsForslag("kontrollera_brandvarnare", R.string.forslag_kontrollera_brandvarnare, 12, IntervallBestamdAv.INGEN),
        UppgiftsForslag("byt_filter_ventilation", R.string.forslag_byt_filter_ventilation, 6, IntervallBestamdAv.INGEN),
        UppgiftsForslag("rensa_golvbrunn", R.string.forslag_rensa_golvbrunn, 12, IntervallBestamdAv.INGEN),
        UppgiftsForslag("kontrollera_tak", R.string.forslag_kontrollera_tak, 12, IntervallBestamdAv.INGEN),
        UppgiftsForslag("sotning", R.string.forslag_sotning, 12, IntervallBestamdAv.KOMMUNEN),
        UppgiftsForslag("service_varmepump", R.string.forslag_service_varmepump, 12, IntervallBestamdAv.TILLVERKAREN),
        UppgiftsForslag("service_ventilation", R.string.forslag_service_ventilation, 24, IntervallBestamdAv.TILLVERKAREN),
    )
