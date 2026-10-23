package se.tmconnect.flextid.paminnelse

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
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.NotisAtgardTextNyckel
import se.tmconnect.flextid.regler.NotisAtgardUtfall
import se.tmconnect.flextid.regler.formateraTid
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseNotisInnehall

object FlextidPaminnelseNotiser {
    fun skapaKanalOmBehovs(context: Context, kalla: PaminnelseKalla) {
        val kanal =
            NotificationChannel(
                kalla.kanalId(),
                kalla.kanalNamn(context),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(kanal)
    }

    fun visaPaminnelse(
        context: Context,
        kalla: PaminnelseKalla,
        innehall: PaminnelseNotisInnehall,
        appIntent: Intent,
        medStamplaInKnapp: Boolean,
        medStamplaUtKnapp: Boolean,
    ) {
        if (!harNotisBehorighet(context)) {
            return
        }
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val contentPending =
            PendingIntent.getActivity(
                context,
                innehall.notisId,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val builder =
            NotificationCompat.Builder(context, kalla.kanalId())
                .setSmallIcon(kalla.notisIkon())
                .setContentTitle(innehall.titel)
                .setContentText(innehall.text)
                .setContentIntent(contentPending)
                .setAutoCancel(true)
        if (medStamplaInKnapp) {
            builder.addAction(stamplaInAction(context, innehall.notisId))
        }
        if (medStamplaUtKnapp) {
            builder.addAction(stamplaUtAction(context, innehall.notisId))
        }
        NotificationManagerCompat.from(context).notify(innehall.notisId, builder.build())
    }

    fun taBort(context: Context, notisId: Int) {
        NotificationManagerCompat.from(context).cancel(notisId)
    }

    fun visaAtgardResultat(
        context: Context,
        notisId: Int,
        titel: String,
        utfall: NotisAtgardUtfall.VisaText,
    ) {
        if (!harNotisBehorighet(context)) {
            return
        }
        val text = textForNyckel(context, utfall)
        val appIntent =
            Intent(context, se.tmconnect.flextid.MainActivity::class.java).apply {
                putExtra(PaminnelseWorker.EXTRA_OPPNA_IDAG, true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val contentPending =
            PendingIntent.getActivity(
                context,
                notisId,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        val notis =
            NotificationCompat.Builder(context, FlextidPaminnelseKalla.KANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titel)
                .setContentText(text)
                .setContentIntent(contentPending)
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(context).notify(notisId, notis)
    }

    private fun stamplaInAction(context: Context, notisId: Int): NotificationCompat.Action {
        val intent =
            Intent(context, PaminnelseAtgardReceiver::class.java).apply {
                action = PaminnelseAtgardIntent.ACTION_STAMPLA_IN
                putExtra(PaminnelseAtgardIntent.EXTRA_NOTIS_ID, notisId)
            }
        val pending =
            PendingIntent.getBroadcast(
                context,
                notisId * 10 + 1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat.Action.Builder(0, context.getString(R.string.stampla_in), pending).build()
    }

    private fun stamplaUtAction(context: Context, notisId: Int): NotificationCompat.Action {
        val intent =
            Intent(context, PaminnelseAtgardReceiver::class.java).apply {
                action = PaminnelseAtgardIntent.ACTION_STAMPLA_UT
                putExtra(PaminnelseAtgardIntent.EXTRA_NOTIS_ID, notisId)
            }
        val pending =
            PendingIntent.getBroadcast(
                context,
                notisId * 10 + 2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        return NotificationCompat.Action.Builder(0, context.getString(R.string.stampla_ut), pending).build()
    }

    private fun textForNyckel(context: Context, utfall: NotisAtgardUtfall.VisaText): String =
        when (utfall.nyckel) {
            NotisAtgardTextNyckel.GENVAG_REDAN_INSTAMPLAD ->
                context.getString(
                    R.string.genvag_redan_instamplad,
                    formateraTid(checkNotNull(utfall.tidForFormat)),
                )
            NotisAtgardTextNyckel.GENVAG_INGEN_INSTEMPLING ->
                context.getString(R.string.genvag_ingen_instempling)
            NotisAtgardTextNyckel.FEL_SLUT_FORE_START ->
                context.getString(R.string.fel_slut_fore_start)
            NotisAtgardTextNyckel.PRO_GRANS_NADD ->
                context.getString(R.string.pro_grans_nadd)
        }

    private fun harNotisBehorighet(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
