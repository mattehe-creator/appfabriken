package se.tmconnect.husloggen.regler

const val MAX_BILDER_PER_POST = 10

fun kanLäggaTillBild(antal: Int): Boolean = antal < MAX_BILDER_PER_POST
