package se.tmconnect.garantivalvet.paminnelse

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
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.garantivalvet.MainActivity
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.Frist
import se.tmconnect.garantivalvet.regler.FristTyp
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.fristNyckel
import se.tmconnect.garantivalvet.regler.fristerSomSkaAviseras
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.reklamationSlut

class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val installningar = InstallningarLager(applicationContext)
        val paminnelserPa = installningar.paminnelserPa.first()
        if (!paminnelserPa) {
            return Result.success()
        }
        if (!harNotisBehorighet()) {
            return Result.success()
        }

        val dao = GarantiDatabas.hamta(applicationContext).kopDao()
        val kopLista = dao.alla()
        val idag = LocalDate.now()
        val frister = kopLista.flatMap { kop -> fristerFranKop(kop) }
        val redanAviserade = installningar.hamtaAviseradeFrister()
        val attAvisera = fristerSomSkaAviseras(frister, idag, redanAviserade)
        if (attAvisera.isEmpty()) {
            return Result.success()
        }

        skapaKanalOmBehovs()
        val locale = Locale.getDefault()
        val kopEfterId = kopLista.associateBy { it.id }

        if (attAvisera.size == 1) {
            val frist = attAvisera.single()
            val kop = kopEfterId[frist.kopId] ?: return Result.success()
            val (titelRes, textRes) = when (frist.typ) {
                FristTyp.GARANTI -> R.string.paminnelse_notis_titel to R.string.paminnelse_notis_text
                FristTyp.REKLAMATION -> R.string.paminnelse_notis_reklamation_titel to
                    R.string.paminnelse_notis_reklamation_text
            }
            visaNotis(
                notisId = frist.kopId.toInt(),
                titel = applicationContext.getString(titelRes),
                text = applicationContext.getString(
                    textRes,
                    kop.vad,
                    formateraDatum(frist.slutdatum, locale),
                ),
                kopId = kop.id,
            )
        } else {
            visaNotis(
                notisId = SAMMANSATT_NOTIS_ID,
                titel = applicationContext.getString(R.string.paminnelse_notis_samlad_titel),
                text = applicationContext.getString(
                    R.string.paminnelse_notis_samlad_text,
                    attAvisera.size,
                ),
                kopId = null,
            )
        }

        installningar.laggTillAviseradeFrister(attAvisera.map(::fristNyckel).toSet())
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

    private fun skapaKanalOmBehovs() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val kanal = NotificationChannel(
            KANAL_ID,
            applicationContext.getString(R.string.paminnelse_kanal_namn),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(kanal)
    }

    private fun visaNotis(
        notisId: Int,
        titel: String,
        text: String,
        kopId: Long?,
    ) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (kopId != null) {
                putExtra(EXTRA_KOP_ID, kopId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notisId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notis = NotificationCompat.Builder(applicationContext, KANAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titel)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(notisId, notis)
    }

    private fun fristerFranKop(kop: Kop): List<Frist> {
        val garanti = Frist(
            kopId = kop.id,
            typ = FristTyp.GARANTI,
            slutdatum = garantiSlut(kop.kopdatum, kop.garantiManader),
        )
        val reklamation = reklamationSlut(kop.kopdatum)?.let { slut ->
            Frist(kopId = kop.id, typ = FristTyp.REKLAMATION, slutdatum = slut)
        }
        return listOfNotNull(garanti, reklamation)
    }

    companion object {
        const val EXTRA_KOP_ID = "se.tmconnect.garantivalvet.extra.KOP_ID"
        private const val KANAL_ID = "paminnelser"
        private const val SAMMANSATT_NOTIS_ID = 1
    }
}
