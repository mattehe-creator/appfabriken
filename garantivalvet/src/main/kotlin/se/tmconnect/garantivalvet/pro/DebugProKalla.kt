package se.tmconnect.garantivalvet.pro

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.pro.skapaPlayProKalla

class DebugProKalla(
    private val playProKalla: ProKalla,
    private val testlageLager: DebugProTestlageLager,
    coroutineScope: CoroutineScope,
) : ProKalla {
    override val arPro: StateFlow<Boolean> =
        combine(playProKalla.arPro, testlageLager.testProAktivt) { franPlay, test ->
            franPlay || test
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    override suspend fun kontrollera() {
        playProKalla.kontrollera()
    }

    override fun kop(activity: Activity) {
        playProKalla.kop(activity)
    }

    val testProAktivt: Flow<Boolean> = testlageLager.testProAktivt

    suspend fun sattTestProAktivt(aktivt: Boolean) {
        testlageLager.sattTestProAktivt(aktivt)
    }
}

fun skapaDebugProKalla(
    context: Context,
    produktId: String,
    coroutineScope: CoroutineScope,
): DebugProKalla {
    val play = skapaPlayProKalla(context, produktId, coroutineScope)
    return DebugProKalla(
        playProKalla = play,
        testlageLager = DebugProTestlageLager(context),
        coroutineScope = coroutineScope,
    )
}
