package se.tmconnect.garantivalvet.paminnelse

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PaminnelsePlanerare {
    private const val UNIKT_NAMN = "paminnelse_daglig"

    fun synka(context: Context, paminnelserPa: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (!paminnelserPa) {
            workManager.cancelUniqueWork(UNIKT_NAMN)
            return
        }
        val request =
            PeriodicWorkRequestBuilder<PaminnelseWorker>(24, TimeUnit.HOURS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            UNIKT_NAMN,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
