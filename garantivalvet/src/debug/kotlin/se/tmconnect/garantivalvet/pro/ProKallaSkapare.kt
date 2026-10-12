package se.tmconnect.garantivalvet.pro

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.karna.pro.ProKalla

object ProKallaSkapare {
    fun skapa(
        context: Context,
        produktId: String,
        coroutineScope: CoroutineScope,
        installningarLager: InstallningarLager,
    ): ProKalla =
        DebugProKalla(
            installningarLager = installningarLager,
            coroutineScope = coroutineScope,
        )
}
