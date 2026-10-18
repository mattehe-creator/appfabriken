package se.tmconnect.husloggen.export

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
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.skapaHusRepository
import se.tmconnect.karna.sakerhetskopia.arSakerhetskopiaDags
import se.tmconnect.karna.sakerhetskopia.kopiera

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

        private val korLas = Mutex()

        @Volatile
        var testMappLagring: se.tmconnect.karna.sakerhetskopia.MappLagring? = null
    }
}

internal suspend fun korSakerhetskopiaJobb(
    context: Context,
    tvinga: Boolean,
    nu: Long = System.currentTimeMillis(),
    idag: LocalDate = LocalDate.now(),
): androidx.work.ListenableWorker.Result {
    val installningar = InstallningarLager(context)
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
                val databas = HusDatabas.hamta(context)
                val dao = databas.husDao()
                val repository = skapaHusRepository(context, databas)
                val uppgifter = repository.allaUppgifter()
                val poster = repository.allaPoster()
                val matare = repository.allaMatare()
                val avlasningar = repository.allaAvlasningar()
                val dokument = repository.allaDokument()
                val planerade = repository.allaPlaneradeKostnader()
                val bildLager = se.tmconnect.husloggen.data.skapaHusBildLager(context)
                val dokumentLager = se.tmconnect.husloggen.data.skapaHusDokumentLager(context)
                val lagring =
                    SakerhetskopiaWorker.testMappLagring
                        ?: SafMapp(context, Uri.parse(mapp))
                kopiera(
                    lagring = lagring,
                    filPrefix = HUSLOGGEN_AUTO_FILPREFIX,
                    skrivZip = {
                            ut ->
                        Sakerhetskopia.skriv(
                            ut,
                            uppgifter,
                            poster,
                            matare,
                            avlasningar,
                            dokument,
                            bildLager,
                            dokumentLager,
                            planerade,
                        )
                    },
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
    private const val UNIKT_NAMN = "husloggen_sakerhetskopia_auto"
    private const val UNIKT_NAMN_NU = "husloggen_sakerhetskopia_nu"

    fun synka(context: Context, pa: Boolean) {
        val workManager = WorkManager.getInstance(context)
        if (!pa) {
            workManager.cancelUniqueWork(UNIKT_NAMN)
            return
        }
        val request =
            PeriodicWorkRequestBuilder<SakerhetskopiaWorker>(24, TimeUnit.HOURS)
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
            OneTimeWorkRequestBuilder<SakerhetskopiaWorker>()
                .setInputData(workDataOf(SakerhetskopiaWorker.TVINGA to true))
                .build(),
        )
    }
}
