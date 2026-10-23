package se.tmconnect.flextid

/** Tolkning av app-genvägars intent-åtgärder (ingen Android-beroende). */
object GenvagIntent {
    const val ACTION_STAMPLA_IN = "se.tmconnect.flextid.action.STAMPLA_IN"
    const val ACTION_STAMPLA_UT = "se.tmconnect.flextid.action.STAMPLA_UT"

    enum class Atgard {
        STAMPLA_IN,
        STAMPLA_UT,
    }

    fun atgardFranAction(action: String?): Atgard? =
        when (action) {
            ACTION_STAMPLA_IN -> Atgard.STAMPLA_IN
            ACTION_STAMPLA_UT -> Atgard.STAMPLA_UT
            else -> null
        }
}
