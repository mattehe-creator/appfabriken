package se.tmconnect.flextid.export

import android.content.Context
import android.net.Uri
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import se.tmconnect.flextid.data.AutoBackupInstallningarLager
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.paminnelse.FlextidPaminnelseInstallningarLager
import se.tmconnect.karna.sakerhetskopia.arSakerhetskopiaDags
import se.tmconnect.karna.sakerhetskopia.kopiera

class AutoSakerhetskopiaWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): androidx.work.ListenableWorker.Result = korLas.withLock {
        korAutoSakerhetskopiaJobb(
            context = applicationContext,
            tvinga = inputData.getBoolean(TVINGA, false),
        )
    }

    companion object {
        const val TVINGA = "tvinga"

        private val korLas = Mutex()

        @Volatile
        var testMappLagring: se.tmconnect.karna.sakerhetskopia.MappLagring? = null
    }
}

internal suspend fun korAutoSakerhetskopiaJobb(
    context: Context,
    tvinga: Boolean,
    nu: Long = System.currentTimeMillis(),
    idag: LocalDate = LocalDate.now(),
): androidx.work.ListenableWorker.Result {
    val installningar = AutoBackupInstallningarLager(context)
    val mapp = installningar.autoBackupMapp.first() ?: return androidx.work.ListenableWorker.Result.success()
    if (!tvinga &&
        !arSakerhetskopiaDags(
            installningar.autoBackupSenaste.first(),
            nu,
            installningar.autoBackupIntervallDagar.first(),
        )
    ) {
        return androidx.work.ListenableWorker.Result.success()
    }
    val ok =
        withContext(Dispatchers.IO) {
            try {
                val repository = skapaFlextidRepository(context)
                val uiInstallningar = UiInstallningarLager(context)
                val paminnelseInstallningar = FlextidPaminnelseInstallningarLager(context)
                val json =
                    skapaFlextidSakerhetskopiaJson(
                        repository,
                        uiInstallningar,
                        paminnelseInstallningar,
                    )
                val lagring =
                    AutoSakerhetskopiaWorker.testMappLagring
                        ?: SafMapp(context, Uri.parse(mapp))
                kopiera(
                    lagring = lagring,
                    filPrefix = FLEXTID_AUTO_FILPREFIX,
                    skrivZip = { ut -> ut.write(json.toByteArray(Charsets.UTF_8)) },
                    idag = idag,
                    filAndelse = FLEXTID_AUTO_FILANDELSE,
                )
            } catch (_: Exception) {
                false
            }
        }
    installningar.sattAutoBackupResultat(ok, nu)
    return androidx.work.ListenableWorker.Result.success()
}

object AutoSakerhetskopiaPlanerare {
    private const val UNIKT_NAMN = "flextid_sakerhetskopia_auto"
    private const val UNIKT_NAMN_NU = "flextid_sakerhetskopia_nu"

    fun synka(context: Context, pa: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (!pa) {
            workManager.cancelUniqueWork(UNIKT_NAMN)
            return
        }
        val request =
            PeriodicWorkRequestBuilder<AutoSakerhetskopiaWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(24, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(true)
                        .build(),
                )
                .build()
        workManager.enqueueUniquePeriodicWork(UNIKT_NAMN, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun korNu(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIKT_NAMN_NU,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<AutoSakerhetskopiaWorker>()
                .setInputData(workDataOf(AutoSakerhetskopiaWorker.TVINGA to true))
                .build(),
        )
    }
}
