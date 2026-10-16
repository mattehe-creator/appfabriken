package se.tmconnect.husloggen.paminnelse

import android.content.Context
import androidx.work.WorkerParameters
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseWorker as KarnaPaminnelseWorker

class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : KarnaPaminnelseWorker(appContext, params) {

    override fun paminnelseKalla(): PaminnelseKalla = HusPaminnelseKalla(applicationContext)

    companion object {
        const val EXTRA_OPPNA_ATT_GORA = "se.tmconnect.husloggen.extra.ATT_GORA"
    }
}
