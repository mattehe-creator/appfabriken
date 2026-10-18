package se.tmconnect.rostfangst.paminnelse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import se.tmconnect.rostfangst.MainActivity
import se.tmconnect.rostfangst.R

class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (!harNotisBehorighet()) {
            return Result.success()
        }
        val titel = inputData.getString(KEY_TITEL) ?: return Result.failure()
        val notisId = inputData.getInt(KEY_NOTIS_ID, titel.hashCode())
        skapaKanal()
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            applicationContext,
            notisId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notis = NotificationCompat.Builder(applicationContext, KANAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.paminnelse_notis_titel))
            .setContentText(titel)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(notisId, notis)
        return Result.success()
    }

    private fun harNotisBehorighet(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun skapaKanal() {
        val kanal = NotificationChannel(
            KANAL_ID,
            applicationContext.getString(R.string.paminnelse_kanal_namn),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(kanal)
    }

    companion object {
        const val KEY_TITEL = "titel"
        const val KEY_NOTIS_ID = "notis_id"
        const val KANAL_ID = "rostfangst_paminnelse"
    }
}
