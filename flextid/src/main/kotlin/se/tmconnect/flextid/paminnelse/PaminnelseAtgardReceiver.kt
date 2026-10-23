package se.tmconnect.flextid.paminnelse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import se.tmconnect.flextid.R
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.regler.NotisAtgardUtfall
import se.tmconnect.flextid.regler.notisAtgardEfterStamplaIn
import se.tmconnect.flextid.regler.notisAtgardEfterStamplaUt
import se.tmconnect.karna.pro.ProStatusLager

class PaminnelseAtgardReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || !PaminnelseAtgardIntent.arKandAtgard(intent)) {
            return
        }
        val notisId = intent.getIntExtra(PaminnelseAtgardIntent.EXTRA_NOTIS_ID, -1)
        if (notisId < 0) {
            return
        }
        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            try {
                withContext(Dispatchers.IO) {
                    korAtgard(context.applicationContext, intent, notisId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun korAtgard(context: Context, intent: Intent, notisId: Int) {
        val repo = skapaFlextidRepository(context)
        val arPro = ProStatusLager(context).hamtaArPro()
        val nu = LocalTime.now()
        val utfall =
            when {
                PaminnelseAtgardIntent.arStamplaIn(intent) -> {
                    val inst = repo.hamtaInstallning()
                    notisAtgardEfterStamplaIn(
                        repo.genvagStamplaIn(LocalDate.now(), nu, inst.standardRastMin, arPro),
                    )
                }
                PaminnelseAtgardIntent.arStamplaUt(intent) ->
                    notisAtgardEfterStamplaUt(repo.genvagStamplaUt(LocalDate.now(), nu, arPro))
                else -> return
            }
        when (utfall) {
            NotisAtgardUtfall.TaBort -> FlextidPaminnelseNotiser.taBort(context, notisId)
            is NotisAtgardUtfall.VisaText -> {
                val titel =
                    when (notisId) {
                        FlextidPaminnelseKalla.NOTIS_ID_STAMPLA_IN ->
                            context.getString(R.string.paminnelse_stampla_in_titel)
                        FlextidPaminnelseKalla.NOTIS_ID_STAMPLA_UT ->
                            context.getString(R.string.paminnelse_stampla_ut_titel)
                        else -> context.getString(R.string.paminnelse_samlad_titel)
                    }
                FlextidPaminnelseNotiser.visaAtgardResultat(context, notisId, titel, utfall)
            }
        }
    }
}
