package se.tmconnect.rostfangst.paminnelse

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Instant
import se.tmconnect.rostfangst.regler.fordrojning

object PaminnelsePlanerare {
    fun planera(context: Context, fangstId: Long, titel: String, start: Instant, nu: Instant = Instant.now()) {
        val delay = fordrojning(nu, start) ?: return
        val data = Data.Builder()
            .putString(PaminnelseWorker.KEY_TITEL, titel)
            .putInt(PaminnelseWorker.KEY_NOTIS_ID, fangstId.toInt())
            .build()
        val work = OneTimeWorkRequestBuilder<PaminnelseWorker>()
            .setInitialDelay(delay)
            .setInputData(data)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "paminnelse_$fangstId",
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }
}
