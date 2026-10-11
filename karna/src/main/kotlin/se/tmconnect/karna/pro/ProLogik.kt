package se.tmconnect.karna.pro

/** Förenklad köpstatus för ren logik utan Play Billing-typer. */
enum class KopTillstand {
    PURCHASED,
    PENDING,
}

data class KopInfo(
    val produktId: String,
    val tillstand: KopTillstand,
)

/**
 * Avgör om användaren ska räknas som Pro utifrån aktiva köp.
 * Endast [KopTillstand.PURCHASED] för [produktId] ger Pro.
 */
fun arProEfter(kop: List<KopInfo>, produktId: String): Boolean =
    kop.any { it.produktId == produktId && it.tillstand == KopTillstand.PURCHASED }
