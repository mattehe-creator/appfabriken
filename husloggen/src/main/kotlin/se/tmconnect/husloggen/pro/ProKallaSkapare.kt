package se.tmconnect.husloggen.pro

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.pro.skapaPlayProKalla

object ProKallaSkapare {
    fun skapa(
        context: Context,
        produktId: String,
        coroutineScope: CoroutineScope,
    ): ProKalla =
        skapaPlayProKalla(
            context = context,
            produktId = produktId,
            coroutineScope = coroutineScope,
        )
}
