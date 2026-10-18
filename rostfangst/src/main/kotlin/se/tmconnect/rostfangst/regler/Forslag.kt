package se.tmconnect.rostfangst.regler

import java.time.Instant

data class Forslag(
    val typ: ForslagTyp,
    val titel: String,
    val start: Instant? = null,
    val slut: Instant? = null,
    val plats: String? = null,
    val svar: String? = null,
)
