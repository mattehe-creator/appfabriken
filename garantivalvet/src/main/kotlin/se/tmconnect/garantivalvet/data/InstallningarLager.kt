package se.tmconnect.garantivalvet.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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

    companion object {
        private val PAMINNELSER_PA = booleanPreferencesKey("paminnelser_pa")
        private val AVISERADE_FRISTER = stringSetPreferencesKey("aviserade_frister")
    }
}
