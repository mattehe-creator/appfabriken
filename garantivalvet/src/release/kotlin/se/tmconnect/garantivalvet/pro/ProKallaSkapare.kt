package se.tmconnect.garantivalvet.pro

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.pro.skapaPlayProKalla

object ProKallaSkapare {
    fun skapa(
        context: Context,
        produktId: String,
        coroutineScope: CoroutineScope,
        @Suppress("UNUSED_PARAMETER") installningarLager: InstallningarLager,
    ): ProKalla =
        skapaPlayProKalla(
            context = context,
            produktId = produktId,
            coroutineScope = coroutineScope,
        )
}
