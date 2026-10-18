package se.tmconnect.husloggen.regler

const val GRATIS_MAX_BOSTADER = 3

/** Om användaren får lägga till ytterligare en bostad utan Pro. */
fun kanLaggaTillBostad(antal: Int, arPro: Boolean): Boolean =
    arPro || antal < GRATIS_MAX_BOSTADER
