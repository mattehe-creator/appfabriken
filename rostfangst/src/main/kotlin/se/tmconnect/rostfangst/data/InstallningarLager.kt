package se.tmconnect.rostfangst.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.installningarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "rostfangst_installningar",
)

class InstallningarLager(private val context: Context) {
    val apiNyckel: Flow<String> = context.installningarDataStore.data.map { it[API_NYCKEL].orEmpty() }
    val modell: Flow<String> = context.installningarDataStore.data.map {
        it[MODELL] ?: DEFAULT_MODELL
    }

    suspend fun sparaApiNyckel(nyckel: String) {
        context.installningarDataStore.edit { it[API_NYCKEL] = nyckel.trim() }
    }

    suspend fun sparaModell(modell: String) {
        context.installningarDataStore.edit { it[MODELL] = modell.trim().ifEmpty { DEFAULT_MODELL } }
    }

    companion object {
        const val DEFAULT_MODELL = "claude-haiku-5-5"
        private val API_NYCKEL = stringPreferencesKey("api_nyckel")
        private val MODELL = stringPreferencesKey("modell")
    }
}
