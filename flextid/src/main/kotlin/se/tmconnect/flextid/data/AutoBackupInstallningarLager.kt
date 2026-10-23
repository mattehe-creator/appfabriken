package se.tmconnect.flextid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.tmconnect.karna.sakerhetskopia.AUTO_INTERVALL_VECKOVIS

private val Context.flextidAutoBackupDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "flextid_auto_backup",
)

class AutoBackupInstallningarLager(
    private val context: Context,
) {
    val autoBackupMapp: Flow<String?> =
        context.flextidAutoBackupDataStore.data.map { prefs -> prefs[AUTO_MAPP] }

    val autoBackupMappNamn: Flow<String?> =
        context.flextidAutoBackupDataStore.data.map { prefs -> prefs[AUTO_MAPP_NAMN] }

    val autoBackupIntervallDagar: Flow<Int> =
        context.flextidAutoBackupDataStore.data.map { prefs -> prefs[AUTO_INTERVALL] ?: AUTO_INTERVALL_VECKOVIS }

    val autoBackupSenaste: Flow<Long?> =
        context.flextidAutoBackupDataStore.data.map { prefs -> prefs[AUTO_SENASTE] }

    val autoBackupFel: Flow<Boolean> =
        context.flextidAutoBackupDataStore.data.map { prefs -> prefs[AUTO_FEL] ?: false }

    suspend fun sattAutoBackupMapp(uri: String?, namn: String?) {
        context.flextidAutoBackupDataStore.edit { prefs ->
            if (uri == null) {
                prefs.remove(AUTO_MAPP)
                prefs.remove(AUTO_MAPP_NAMN)
                prefs.remove(AUTO_SENASTE)
                prefs.remove(AUTO_FEL)
            } else {
                prefs[AUTO_MAPP] = uri
                if (namn != null) {
                    prefs[AUTO_MAPP_NAMN] = namn
                } else {
                    prefs.remove(AUTO_MAPP_NAMN)
                }
                prefs[AUTO_FEL] = false
            }
        }
    }

    suspend fun sattAutoBackupIntervall(dagar: Int) {
        context.flextidAutoBackupDataStore.edit { prefs -> prefs[AUTO_INTERVALL] = dagar }
    }

    suspend fun sattAutoBackupResultat(lyckades: Boolean, nuMillis: Long) {
        context.flextidAutoBackupDataStore.edit { prefs ->
            if (lyckades) {
                prefs[AUTO_SENASTE] = nuMillis
                prefs[AUTO_FEL] = false
            } else {
                prefs[AUTO_FEL] = true
            }
        }
    }

    companion object {
        private val AUTO_MAPP = stringPreferencesKey("auto_backup_mapp")
        private val AUTO_MAPP_NAMN = stringPreferencesKey("auto_backup_mapp_namn")
        private val AUTO_INTERVALL = intPreferencesKey("auto_backup_intervall_dagar")
        private val AUTO_SENASTE = longPreferencesKey("auto_backup_senaste")
        private val AUTO_FEL = booleanPreferencesKey("auto_backup_fel")
    }
}
