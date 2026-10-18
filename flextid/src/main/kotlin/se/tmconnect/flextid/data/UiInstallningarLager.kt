package se.tmconnect.flextid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.flextidUiDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "flextid_ui",
)

class UiInstallningarLager(private val context: Context) {
    val valkommenVisad: Flow<Boolean> =
        context.flextidUiDataStore.data.map { prefs -> prefs[VALKOMMEN_VISAD] ?: false }

    suspend fun sattValkommenVisad() {
        context.flextidUiDataStore.edit { prefs -> prefs[VALKOMMEN_VISAD] = true }
    }

    val temaLage: Flow<String?> =
        context.flextidUiDataStore.data.map { prefs -> prefs[TEMA_LAGE] }

    suspend fun sattTemaLage(namn: String) {
        context.flextidUiDataStore.edit { prefs -> prefs[TEMA_LAGE] = namn }
    }

    val exportArManad: Flow<YearMonth> =
        context.flextidUiDataStore.data.map { prefs ->
            prefs[EXPORT_AR_MANAD]?.let { runCatching { YearMonth.parse(it) }.getOrNull() }
                ?: YearMonth.now()
        }

    suspend fun sattExportArManad(arManad: YearMonth) {
        context.flextidUiDataStore.edit { prefs ->
            prefs[EXPORT_AR_MANAD] = arManad.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        }
    }

    companion object {
        private val VALKOMMEN_VISAD = booleanPreferencesKey("valkommen_visad")
        private val TEMA_LAGE = stringPreferencesKey("tema_lage")
        private val EXPORT_AR_MANAD = stringPreferencesKey("export_ar_manad")
    }
}
