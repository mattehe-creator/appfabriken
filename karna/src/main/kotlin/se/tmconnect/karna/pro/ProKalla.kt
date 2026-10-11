package se.tmconnect.karna.pro

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ProKalla {
    val arPro: StateFlow<Boolean>

    /** Synkar Pro-status mot Play (t.ex. vid appstart). */
    suspend fun kontrollera()

    /** Startar Play-köpflödet för Pro. */
    fun kop(activity: Activity)
}

/** Testbar fake utan Play Billing. */
class FakeProKalla(
    initialArPro: Boolean = false,
) : ProKalla {
    private val _arPro = MutableStateFlow(initialArPro)
    override val arPro: StateFlow<Boolean> = _arPro.asStateFlow()

    var kopAnropad: Boolean = false
        private set

    override suspend fun kontrollera() {
        // Ingen Play i tester.
    }

    override fun kop(activity: Activity) {
        kopAnropad = true
    }

    fun sattArPro(varde: Boolean) {
        _arPro.value = varde
    }
}

/** Skapar Play Billing-implementation av [ProKalla] (produkt-id från appmodulen). */
fun skapaPlayProKalla(
    context: Context,
    produktId: String,
    coroutineScope: CoroutineScope,
): ProKalla =
    PlayProKalla(
        context = context,
        produktId = produktId,
        proStatusLager = ProStatusLager(context),
        coroutineScope = coroutineScope,
    )
