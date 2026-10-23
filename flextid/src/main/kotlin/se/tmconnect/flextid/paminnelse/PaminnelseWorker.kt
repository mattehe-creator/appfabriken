package se.tmconnect.flextid.paminnelse

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.WorkerParameters
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import se.tmconnect.karna.paminnelse.PaminnelseKalla
import se.tmconnect.karna.paminnelse.PaminnelseWorker as KarnaPaminnelseWorker
import se.tmconnect.karna.paminnelse.fristerSomSkaAviseras

class PaminnelseWorker(
    appContext: Context,
    params: WorkerParameters,
) : KarnaPaminnelseWorker(appContext, params) {

    override fun paminnelseKalla(): PaminnelseKalla =
        FlextidPaminnelseKalla(
            applicationContext,
            FlextidPaminnelseInstallningarLager(applicationContext),
        )

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
        val attAvisera =
            fristerSomSkaAviseras(
                frister,
                idag,
                redanAviserade,
                kalla.dagarInnan,
            )
        if (attAvisera.isEmpty()) {
            return Result.success()
        }

        FlextidPaminnelseNotiser.skapaKanalOmBehovs(applicationContext, kalla)
        val innehall =
            if (attAvisera.size == 1) {
                kalla.notisForEnFrist(attAvisera.single())
            } else {
                kalla.notisSamlad(attAvisera.size)
            }
        val medStamplaIn = innehall.notisId == FlextidPaminnelseKalla.NOTIS_ID_STAMPLA_IN
        val medStamplaUt = innehall.notisId == FlextidPaminnelseKalla.NOTIS_ID_STAMPLA_UT
        FlextidPaminnelseNotiser.visaPaminnelse(
            applicationContext,
            kalla,
            innehall,
            kalla.skapaAppIntent(attAvisera.singleOrNull()),
            medStamplaInKnapp = medStamplaIn,
            medStamplaUtKnapp = medStamplaUt,
        )
        installningar.laggTillAviseradeNycklar(attAvisera.map { it.nyckel }.toSet())
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

    companion object {
        const val EXTRA_OPPNA_IDAG = "se.tmconnect.flextid.extra.IDAG"
    }
}
