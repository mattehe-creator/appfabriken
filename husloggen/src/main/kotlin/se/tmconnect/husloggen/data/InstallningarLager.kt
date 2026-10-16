package se.tmconnect.husloggen.data

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

private val Context.husInstallningarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "husloggen_installningar",
)

class InstallningarLager(
    private val context: Context,
) {
    val autoBackupMapp: Flow<String?> =
        context.husInstallningarDataStore.data.map { prefs -> prefs[AUTO_MAPP] }

    val autoBackupMappNamn: Flow<String?> =
        context.husInstallningarDataStore.data.map { prefs -> prefs[AUTO_MAPP_NAMN] }

    val autoBackupIntervallDagar: Flow<Int> =
        context.husInstallningarDataStore.data.map { prefs -> prefs[AUTO_INTERVALL] ?: 7 }

    val autoBackupSenaste: Flow<Long?> =
        context.husInstallningarDataStore.data.map { prefs -> prefs[AUTO_SENASTE] }

    val autoBackupFel: Flow<Boolean> =
        context.husInstallningarDataStore.data.map { prefs -> prefs[AUTO_FEL] ?: false }

    suspend fun sattAutoBackupMapp(uri: String?, namn: String?) {
        context.husInstallningarDataStore.edit { prefs ->
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
        context.husInstallningarDataStore.edit { prefs -> prefs[AUTO_INTERVALL] = dagar }
    }

    suspend fun sattAutoBackupResultat(lyckades: Boolean, nuMillis: Long) {
        context.husInstallningarDataStore.edit { prefs ->
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
