package se.tmconnect.husloggen.regler

const val DOKUMENT_MAX_FIL_BYTES: Long = 25L * 1024 * 1024

fun dokumentFilStorlekTillaten(storlekBytes: Long): Boolean =
    storlekBytes in 1..DOKUMENT_MAX_FIL_BYTES

fun dokumentMimeTillaten(mimeTyp: String): Boolean =
    mimeTyp == "application/pdf" || mimeTyp.startsWith("image/")
