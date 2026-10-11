package se.tmconnect.garantivalvet.regler

const val GRATIS_MAX_KOP = 15

/** Om användaren får lägga till ytterligare ett köp. */
fun kanLaggaTill(antal: Int, arPro: Boolean): Boolean =
    arPro || antal < GRATIS_MAX_KOP
