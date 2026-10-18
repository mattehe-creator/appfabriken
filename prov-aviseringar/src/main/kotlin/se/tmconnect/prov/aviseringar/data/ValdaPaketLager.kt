package se.tmconnect.prov.aviseringar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.valdaPaketDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "valda_paket",
)

class ValdaPaketLager(private val context: Context) {
    val valdaPaket: Flow<Set<String>> =
        context.valdaPaketDataStore.data.map { prefs ->
            prefs[VALDA_PAKET] ?: emptySet()
        }

    suspend fun sattValda(paket: Set<String>) {
        context.valdaPaketDataStore.edit { prefs ->
            prefs[VALDA_PAKET] = paket
        }
    }

    suspend fun uppdateraPaket(paketNamn: String, valt: Boolean) {
        context.valdaPaketDataStore.edit { prefs ->
            val nu = prefs[VALDA_PAKET]?.toMutableSet() ?: mutableSetOf()
            if (valt) {
                nu.add(paketNamn)
            } else {
                nu.remove(paketNamn)
            }
            prefs[VALDA_PAKET] = nu
        }
    }

    companion object {
        private val VALDA_PAKET = stringSetPreferencesKey("valda_paket")
    }
}
