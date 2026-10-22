package se.tmconnect.flextid.ruta

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import se.tmconnect.flextid.R

/** Android 13 och senare kan be användaren lägga till rutan direkt. Äldre versioner: lägg till den själv. */
fun kanBeOmRuta(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun beOmAttLaggaTillRuta(context: Context) {
    val sbm = context.getSystemService(StatusBarManager::class.java) ?: return
    sbm.requestAddTileService(
        ComponentName(context, StamplaRuta::class.java),
        context.getString(R.string.ruta_namn),
        Icon.createWithResource(context, R.drawable.ic_ruta_stampla),
        context.mainExecutor,
    ) { }
}
