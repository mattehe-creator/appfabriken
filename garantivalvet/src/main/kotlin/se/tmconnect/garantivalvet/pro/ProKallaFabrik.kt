package se.tmconnect.garantivalvet.pro

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import se.tmconnect.garantivalvet.BuildConfig
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.pro.skapaPlayProKalla

fun skapaAppProKalla(
    context: Context,
    produktId: String,
    coroutineScope: CoroutineScope,
): ProKalla =
    if (BuildConfig.DEBUG) {
        skapaDebugProKalla(context, produktId, coroutineScope)
    } else {
        skapaPlayProKalla(context, produktId, coroutineScope)
    }
