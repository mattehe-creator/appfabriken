package se.tmconnect.flextid.paminnelse

import android.content.Context
import androidx.work.WorkerParameters
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseWorker as KarnaPaminnelseWorker

class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : KarnaPaminnelseWorker(appContext, params) {

    override fun paminnelseKalla(): PaminnelseKalla =
        FlextidPaminnelseKalla(
            applicationContext,
            FlextidPaminnelseInstallningarLager(applicationContext),
        )

    companion object {
        const val EXTRA_OPPNA_IDAG = "se.tmconnect.flextid.extra.IDAG"
    }
}
