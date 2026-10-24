package se.tmconnect.karna.paminnelse

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
import java.time.LocalDate
import kotlinx.coroutines.flow.first

/**
 * Dagligt påminnelsejobb. Appmodulen implementerar [PaminnelseKalla] och skickar in den här.
 */
abstract class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    protected abstract fun paminnelseKalla(): PaminnelseKalla

    override suspend fun doWork(): Result {
        val kalla = paminnelseKalla()
        val installningar = kalla.installningarLager
        val paminnelserPa = installningar.paminnelserPa.first()
        if (!paminnelserPa) {
            return Result.success()
        }
        if (!harNotisBehorighet()) {
            return Result.success()
        }

        val idag = LocalDate.now()
        val frister = kalla.hamtaFrister(idag)
        val redanAviserade = installningar.hamtaAviseradeNycklar()
        val attAvisera = fristerSomSkaAviseras(
            frister,
            idag,
            redanAviserade,
            kalla.dagarInnan,
        )
        val aktuellaNycklar = frister.map { it.nyckel }.toSet()
        if (attAvisera.isEmpty()) {
            if ((redanAviserade - aktuellaNycklar).isNotEmpty()) {
                installningar.uppdateraAviseradeNycklar(emptySet(), aktuellaNycklar)
            }
            return Result.success()
        }

        skapaKanalOmBehovs(kalla)
        val innehall = if (attAvisera.size == 1) {
            kalla.notisForEnFrist(attAvisera.single())
        } else {
            kalla.notisSamlad(attAvisera.size)
        }
        visaNotis(kalla, innehall, kalla.skapaAppIntent(attAvisera.singleOrNull()))
        installningar.uppdateraAviseradeNycklar(attAvisera.map { it.nyckel }.toSet(), aktuellaNycklar)
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

    private fun skapaKanalOmBehovs(kalla: PaminnelseKalla) {
        val kanal = NotificationChannel(
            kalla.kanalId(),
            kalla.kanalNamn(applicationContext),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(kanal)
    }

    private fun visaNotis(kalla: PaminnelseKalla, innehall: PaminnelseNotisInnehall, intent: Intent) {
        if (!harNotisBehorighet()) {
            return
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            innehall.notisId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notis = NotificationCompat.Builder(applicationContext, kalla.kanalId())
            .setSmallIcon(kalla.notisIkon())
            .setContentTitle(innehall.titel)
            .setContentText(innehall.text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(innehall.notisId, notis)
    }
}

interface PaminnelseKalla {
    val installningarLager: PaminnelseInstallningarLager
    val dagarInnan: Int
    suspend fun hamtaFrister(idag: LocalDate): List<PaminnelseFrist>
    suspend fun notisForEnFrist(frist: PaminnelseFrist): PaminnelseNotisInnehall
    suspend fun notisSamlad(antal: Int): PaminnelseNotisInnehall
    fun skapaAppIntent(frist: PaminnelseFrist?): Intent
    fun kanalId(): String
    fun kanalNamn(context: Context): String
    fun notisIkon(): Int
}
