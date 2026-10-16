package se.tmconnect.husloggen.paminnelse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import se.tmconnect.husloggen.MainActivity
import se.tmconnect.husloggen.R

class HusPaminnelseKalla(private val context: Context) {
    companion object {
        private const val KANAL_ID = "paminnelse_kanal"
        private const val NOTIS_ID = 1001
    }

    fun skapaPaminnelse(uppgiftsNamn: String, datum: String) {
        // Skapa en notification med uppgiftens namn och datum
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val builder = NotificationCompat.Builder(context, KANAL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.paminnelse_notis_titel))
            .setContentText(context.getString(R.string.paminnelle_notis_text, uppgiftsNamn, datum)) // Ändrad från paminnelse_notis_text till paminnelle_notis_text
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                KANAL_ID,
                context.getString(R.string.paminnelse_kanal_namn),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIS_ID, builder.build())
    }
}
