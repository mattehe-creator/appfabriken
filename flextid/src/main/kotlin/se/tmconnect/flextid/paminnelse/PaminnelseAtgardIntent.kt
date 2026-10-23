package se.tmconnect.flextid.paminnelse

import android.content.Intent

object PaminnelseAtgardIntent {
    const val ACTION_STAMPLA_IN = "se.tmconnect.flextid.action.PAMINNELSE_STAMPLA_IN"
    const val ACTION_STAMPLA_UT = "se.tmconnect.flextid.action.PAMINNELSE_STAMPLA_UT"
    const val EXTRA_NOTIS_ID = "se.tmconnect.flextid.extra.PAMINNELSE_NOTIS_ID"

    fun arStamplaIn(intent: Intent?): Boolean = intent?.action == ACTION_STAMPLA_IN

    fun arStamplaUt(intent: Intent?): Boolean = intent?.action == ACTION_STAMPLA_UT

    fun arKandAtgard(intent: Intent?): Boolean = arStamplaIn(intent) || arStamplaUt(intent)
}
