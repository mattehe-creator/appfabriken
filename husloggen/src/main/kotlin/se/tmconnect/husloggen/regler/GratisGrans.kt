package se.tmconnect.husloggen.regler

const val GRATIS_MAX_POSTER = 15

/** Om användaren får lägga till ytterligare en loggpost. */
fun kanLaggaTillPost(antal: Int, arPro: Boolean): Boolean =
    arPro || antal < GRATIS_MAX_POSTER
