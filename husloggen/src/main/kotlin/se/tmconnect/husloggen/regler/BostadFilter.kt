package se.tmconnect.husloggen.regler

/** Listor som hör till en bostad; tom lista om ingen bostad är vald. */
fun <T> filtreraPaBostad(
    lista: List<T>,
    bostadId: Long?,
    bostadIdFor: (T) -> Long,
): List<T> =
    if (bostadId == null) {
        emptyList()
    } else {
        lista.filter { bostadIdFor(it) == bostadId }
    }
