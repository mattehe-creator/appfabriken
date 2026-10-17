package se.tmconnect.garantivalvet.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.installningarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "installningar",
)

class InstallningarLager(
    private val context: Context,
) {
    val paminnelserPa: Flow<Boolean> =
        context.installningarDataStore.data.map { prefs ->
            prefs[PAMINNELSER_PA] ?: false
        }

    suspend fun sattPaminnelserPa(pa: Boolean) {
        context.installningarDataStore.edit { prefs ->
            prefs[PAMINNELSER_PA] = pa
        }
    }

    /** Namnet på [se.tmconnect.karna.tema.TemaLage], eller `null` (följ telefonen). */
    val temaLage: Flow<String?> =
        context.installningarDataStore.data.map { prefs -> prefs[TEMA_LAGE] }

    suspend fun sattTemaLage(namn: String) {
        context.installningarDataStore.edit { prefs -> prefs[TEMA_LAGE] = namn }
    }

    /** Om välkomstskärmen redan visats (eller behövdes inte, för en som redan har köp). */
    val valkommenVisad: Flow<Boolean> =
        context.installningarDataStore.data.map { prefs -> prefs[VALKOMMEN_VISAD] ?: false }

    suspend fun sattValkommenVisad() {
        context.installningarDataStore.edit { prefs -> prefs[VALKOMMEN_VISAD] = true }
    }

    suspend fun hamtaAviseradeFrister(): Set<String> =
        context.installningarDataStore.data.first()[AVISERADE_FRISTER] ?: emptySet()

    suspend fun laggTillAviseradeFrister(nycklar: Set<String>) {
        if (nycklar.isEmpty()) {
            return
        }
        context.installningarDataStore.edit { prefs ->
            val befintliga = prefs[AVISERADE_FRISTER] ?: emptySet()
            prefs[AVISERADE_FRISTER] = befintliga + nycklar
        }
    }

    /** Mappen (SAF-träd-URI) som automatisk säkerhetskopia skriver till, eller `null` om funktionen är av. */
    val autoBackupMapp: Flow<String?> =
        context.installningarDataStore.data.map { prefs -> prefs[AUTO_MAPP] }

    val autoBackupMappNamn: Flow<String?> =
        context.installningarDataStore.data.map { prefs -> prefs[AUTO_MAPP_NAMN] }

    val autoBackupIntervallDagar: Flow<Int> =
        context.installningarDataStore.data.map { prefs -> prefs[AUTO_INTERVALL] ?: 7 }

    val autoBackupSenaste: Flow<Long?> =
        context.installningarDataStore.data.map { prefs -> prefs[AUTO_SENASTE] }

    val autoBackupFel: Flow<Boolean> =
        context.installningarDataStore.data.map { prefs -> prefs[AUTO_FEL] ?: false }

    /** Väljer mapp för automatisk säkerhetskopia, eller stänger av den med `null`. */
    suspend fun sattAutoBackupMapp(uri: String?, namn: String?) {
        context.installningarDataStore.edit { prefs ->
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
            Unit
        }
    }

    suspend fun sattAutoBackupIntervall(dagar: Int) {
        context.installningarDataStore.edit { prefs -> prefs[AUTO_INTERVALL] = dagar }
    }

    suspend fun sattAutoBackupResultat(lyckades: Boolean, nuMillis: Long) {
        context.installningarDataStore.edit { prefs ->
            if (lyckades) {
                prefs[AUTO_SENASTE] = nuMillis
                prefs[AUTO_FEL] = false
            } else {
                prefs[AUTO_FEL] = true
            }
        }
    }

    /** Debug-testläge: Pro utan Play (används bara av [DebugProKalla] i debug-bygget). */
    val debugProTestlagePa: Flow<Boolean> =
        context.installningarDataStore.data.map { prefs ->
            prefs[DEBUG_PRO_TESTLAGE] ?: false
        }

    suspend fun sattDebugProTestlagePa(pa: Boolean) {
        context.installningarDataStore.edit { prefs ->
            prefs[DEBUG_PRO_TESTLAGE] = pa
        }
    }

    companion object {
        private val VALKOMMEN_VISAD = booleanPreferencesKey("valkommen_visad")
        private val TEMA_LAGE = stringPreferencesKey("tema_lage")
        private val PAMINNELSER_PA = booleanPreferencesKey("paminnelser_pa")
        private val AVISERADE_FRISTER = stringSetPreferencesKey("aviserade_frister")
        private val DEBUG_PRO_TESTLAGE = booleanPreferencesKey("debug_pro_testlage")
        private val AUTO_MAPP = stringPreferencesKey("auto_backup_mapp")
        private val AUTO_MAPP_NAMN = stringPreferencesKey("auto_backup_mapp_namn")
        private val AUTO_INTERVALL = intPreferencesKey("auto_backup_intervall_dagar")
        private val AUTO_SENASTE = longPreferencesKey("auto_backup_senaste")
        private val AUTO_FEL = booleanPreferencesKey("auto_backup_fel")
    }
}
