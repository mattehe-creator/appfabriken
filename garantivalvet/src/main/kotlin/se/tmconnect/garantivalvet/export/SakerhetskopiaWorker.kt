package se.tmconnect.garantivalvet.export

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
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.KvittoLager

/** Körs varje dygn och gör en kopia först när intervallet gått, eller direkt när användaren ber om det. */
class SakerhetskopiaWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): androidx.work.ListenableWorker.Result = korLas.withLock {
        korSakerhetskopiaJobb(
            context = applicationContext,
            tvinga = inputData.getBoolean(TVINGA, false),
        )
    }

    companion object {
        const val TVINGA = "tvinga"

        /** Det dagliga jobbet och "Kopiera nu" får aldrig skriva till mappen samtidigt. */
        private val korLas = Mutex()

        /**
         * Ersätter SAF-mappen i enhetstester (Robolectric). Sätts till `null` efter varje test.
         */
        @Volatile
        var testMappLagring: MappLagring? = null
    }
}

/** Kör automatisk säkerhetskopia enligt sparade inställningar. */
internal suspend fun korSakerhetskopiaJobb(
    context: Context,
    tvinga: Boolean,
    nu: Long = System.currentTimeMillis(),
    idag: LocalDate = LocalDate.now(),
): androidx.work.ListenableWorker.Result {
    val installningar = InstallningarLager(context)
    val mapp = installningar.autoBackupMapp.first() ?: return androidx.work.ListenableWorker.Result.success()
    if (!tvinga && !arSakerhetskopiaDags(installningar.autoBackupSenaste.first(), nu, installningar.autoBackupIntervallDagar.first())) {
        return androidx.work.ListenableWorker.Result.success()
    }
    val ok = withContext(Dispatchers.IO) {
        try {
            val kop = GarantiDatabas.hamta(context).kopDao().alla()
            val kvittoLager = KvittoLager(context)
            val lagring = SakerhetskopiaWorker.testMappLagring
                ?: SafMapp(context, Uri.parse(mapp))
            kopiera(
                lagring = lagring,
                skrivZip = { ut -> Sakerhetskopia.skriv(ut, kop, kvittoLager) },
                idag = idag,
            )
        } catch (_: Exception) {
            false
        }
    }
    installningar.sattAutoBackupResultat(ok, nu)
    return androidx.work.ListenableWorker.Result.success()
}

object SakerhetskopiaPlanerare {
    private const val UNIKT_NAMN = "sakerhetskopia_auto"
    private const val UNIKT_NAMN_NU = "sakerhetskopia_nu"

    /** Slår på eller av det dagliga jobbet. Jobbet gör bara en kopia när intervallet gått. */
    fun synka(context: Context, pa: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (!pa) {
            workManager.cancelUniqueWork(UNIKT_NAMN)
            return
        }
        val request = PeriodicWorkRequestBuilder<SakerhetskopiaWorker>(24, TimeUnit.HOURS)
            // Första dygnsjobbet efter ett dygn; den första kopian görs direkt av korNu när mappen väljs.
            .setInitialDelay(24, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder().setRequiresBatteryNotLow(true).setRequiresStorageNotLow(true).build(),
            )
            .build()
        workManager.enqueueUniquePeriodicWork(UNIKT_NAMN, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** Gör en kopia direkt, oavsett intervall. */
    fun korNu(context: Context) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIKT_NAMN_NU,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SakerhetskopiaWorker>()
                .setInputData(workDataOf(SakerhetskopiaWorker.TVINGA to true))
                .build(),
        )
    }
}
