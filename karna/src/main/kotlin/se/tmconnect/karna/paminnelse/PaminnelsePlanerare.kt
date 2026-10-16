package se.tmconnect.karna.paminnelse

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PaminnelsePlanerare {
    inline fun <reified W : CoroutineWorker> synka(
        context: Context,
        paminnelserPa: Boolean,
        uniktNamn: String,
    ) {
        val workManager = WorkManager.getInstance(context)
        if (!paminnelserPa) {
            workManager.cancelUniqueWork(uniktNamn)
            return
        }
        val request =
            PeriodicWorkRequestBuilder<W>(24, TimeUnit.HOURS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            uniktNamn,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
