package se.tmconnect.karna.paminnelse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Påminnelse-inställning och sparade aviseringsnycklar i DataStore.
 * [dataStoreNamn] skiljer appar och instanser åt (t.ex. `"husloggen_paminnelser"`).
 */
class PaminnelseInstallningarLager(
    context: Context,
    dataStoreNamn: String,
) {
    private val appContext = context.applicationContext
    private val dataStore = hamtaDataStore(appContext, dataStoreNamn)

    val paminnelserPa: Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[PAMINNELSER_PA] ?: false }

    suspend fun sattPaminnelserPa(pa: Boolean) {
        dataStore.edit { prefs -> prefs[PAMINNELSER_PA] = pa }
    }

    suspend fun hamtaAviseradeNycklar(): Set<String> =
        dataStore.data.first()[AVISERADE_NYCKLAR] ?: emptySet()

    suspend fun laggTillAviseradeNycklar(nycklar: Set<String>) {
        if (nycklar.isEmpty()) {
            return
        }
        dataStore.edit { prefs ->
            val befintliga = prefs[AVISERADE_NYCKLAR] ?: emptySet()
            prefs[AVISERADE_NYCKLAR] = befintliga + nycklar
        }
    }

    companion object {
        private val PAMINNELSER_PA = booleanPreferencesKey("paminnelser_pa")
        private val AVISERADE_NYCKLAR = stringSetPreferencesKey("aviserade_nycklar")

        private val dataStores = mutableMapOf<String, DataStore<Preferences>>()

        @Synchronized
        private fun hamtaDataStore(context: Context, namn: String): DataStore<Preferences> =
            dataStores.getOrPut(namn) {
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
                    produceFile = { context.filesDir.resolve("$namn.preferences_pb") },
                )
            }
    }
}
