package se.tmconnect.flextid

import android.content.Intent

object GenvagIntent {
    const val ACTION_STAMPLA_IN = "se.tmconnect.flextid.action.STAMPLA_IN"
    const val ACTION_STAMPLA_UT = "se.tmconnect.flextid.action.STAMPLA_UT"

    const val SHORTCUT_ID_STAMPLA_IN = "stampla_in"
    const val SHORTCUT_ID_STAMPLA_UT = "stampla_ut"

    fun arStamplaIn(intent: Intent?): Boolean = intent?.action == ACTION_STAMPLA_IN

    fun arStamplaUt(intent: Intent?): Boolean = intent?.action == ACTION_STAMPLA_UT

    fun arGenvag(intent: Intent?): Boolean = arStamplaIn(intent) || arStamplaUt(intent)
}
