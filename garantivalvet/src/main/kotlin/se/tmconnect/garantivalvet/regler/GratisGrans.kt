package se.tmconnect.garantivalvet.regler

const val GRATIS_MAX_KOP = 15
const val GRATIS_VARNING_FRAN_ANTAL = 12

/** Om användaren får lägga till ytterligare ett köp. */
fun kanLaggaTill(antal: Int, arPro: Boolean): Boolean =
    arPro || antal < GRATIS_MAX_KOP

/** Om varningskortet för gratisgränsen ska visas. */
fun skaVisaGratisGransVarning(antal: Int, arPro: Boolean): Boolean =
    !arPro && antal >= GRATIS_VARNING_FRAN_ANTAL && antal < GRATIS_MAX_KOP

/** Antal kvitton som återstår innan gratisgränsen är nådd. */
fun kvittonKvarGratis(antal: Int): Int =
    (GRATIS_MAX_KOP - antal).coerceAtLeast(0)
