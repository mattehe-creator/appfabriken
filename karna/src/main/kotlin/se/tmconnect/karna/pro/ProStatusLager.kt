package se.tmconnect.karna.pro

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.proStatusDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "karna_pro_status",
)

class ProStatusLager(
    private val context: Context,
) {
    val arPro: Flow<Boolean> =
        context.proStatusDataStore.data.map { prefs ->
            prefs[AR_PRO] ?: false
        }

    suspend fun hamtaArPro(): Boolean =
        context.proStatusDataStore.data.first()[AR_PRO] ?: false

    suspend fun sattArPro(varde: Boolean) {
        context.proStatusDataStore.edit { prefs ->
            prefs[AR_PRO] = varde
        }
    }

    companion object {
        private val AR_PRO = booleanPreferencesKey("ar_pro")
    }
}
