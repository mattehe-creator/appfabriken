package se.tmconnect.prov.aviseringar.data

import android.content.Context
import android.content.pm.PackageManager
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class AviseringRepository(
    private val dao: AviseringDao,
    private val valdaPaketLager: ValdaPaketLager,
    private val packageManager: PackageManager,
    appScope: CoroutineScope,
) {
    val valdaPaket: StateFlow<Set<String>> =
        valdaPaketLager.valdaPaket.stateIn(appScope, SharingStarted.Eagerly, emptySet())

    val senasteAviseringar: Flow<List<AviseringEntitet>> = dao.senasteFemtio()

    val antalPerApp: Flow<List<AppAntalRad>> = dao.antalSparadePerApp()

    val paketStatistik: Flow<List<PaketStatistik>> = dao.allaPaketStatistik()

    suspend fun behandlaAvisering(sbn: StatusBarNotification) =
        withContext(Dispatchers.IO) {
            val paketNamn = sbn.packageName ?: return@withContext
            val appNamn = hamtaAppNamn(paketNamn)
            dao.registreraPaketAvisering(paketNamn, appNamn)

            if (paketNamn !in valdaPaket.value) {
                return@withContext
            }

            val notification = sbn.notification ?: return@withContext
            val innehall =
                extraheraAviseringInnehall(
                    paketNamn = paketNamn,
                    appNamn = appNamn,
                    nyckel = sbn.key ?: "${paketNamn}_${sbn.id}_${sbn.postTime}",
                    postTime = sbn.postTime,
                    kanalId = notification.channelId,
                    extras = notification.extras,
                )
            dao.sparaAviseringOmNy(innehall.tillEntitet(loggadTid = System.currentTimeMillis()))
        }

    suspend fun uppdateraValtPaket(paketNamn: String, valt: Boolean) {
        valdaPaketLager.uppdateraPaket(paketNamn, valt)
    }

    suspend fun rensaLogg() {
        dao.rensaAllaAviseringar()
    }

    suspend fun hamtaAllaForExport(): List<AviseringEntitet> = dao.allaSparade()

    private fun hamtaAppNamn(paketNamn: String): String =
        runCatching {
            val info = packageManager.getApplicationInfo(paketNamn, 0)
            packageManager.getApplicationLabel(info).toString()
        }.getOrDefault(paketNamn)
}

fun skapaAviseringRepository(context: Context): AviseringRepository {
    val appContext = context.applicationContext
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    return AviseringRepository(
        dao = AviseringDatabas.hamta(appContext).aviseringDao(),
        valdaPaketLager = ValdaPaketLager(appContext),
        packageManager = appContext.packageManager,
        appScope = scope,
    )
}
