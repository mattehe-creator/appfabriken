package se.tmconnect.rostfangst.data

import se.tmconnect.rostfangst.regler.ForslagTyp

data class FangstRad(
    val id: Long,
    val originalText: String,
    val typ: ForslagTyp,
    val titel: String,
    val plats: String?,
    val svar: String?,
    val skapadIso: String,
)
