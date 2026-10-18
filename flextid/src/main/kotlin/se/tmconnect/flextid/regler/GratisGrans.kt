package se.tmconnect.flextid.regler

const val GRATIS_MAX_DAGPOSTER = 15

/** Om användaren får lägga till ytterligare en dagpost (rad i `dag`). */
fun kanLaggaTillDagPost(antal: Int, arPro: Boolean): Boolean =
    arPro || antal < GRATIS_MAX_DAGPOSTER
