package se.tmconnect.garantivalvet.pro

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.karna.pro.ProKalla

/** Debug: Pro styrs av testlägesflagga i DataStore, utan Play Billing. */
class DebugProKalla(
    private val installningarLager: InstallningarLager,
    private val coroutineScope: CoroutineScope,
) : ProKalla {
    override val arPro: StateFlow<Boolean> =
        installningarLager.debugProTestlagePa.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    override suspend fun kontrollera() {
        // Status kommer från DataStore-flödet.
    }

    override fun kop(activity: Activity) {
        coroutineScope.launch {
            installningarLager.sattDebugProTestlagePa(true)
        }
    }
}
