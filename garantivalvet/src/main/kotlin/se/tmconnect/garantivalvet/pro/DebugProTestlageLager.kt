package se.tmconnect.garantivalvet.pro

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.debugProDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "debug_pro_testlage",
)

class DebugProTestlageLager(
    private val context: Context,
) {
    val testProAktivt: Flow<Boolean> =
        context.debugProDataStore.data.map { prefs ->
            prefs[TEST_PRO_AKTIVT] ?: false
        }

    suspend fun sattTestProAktivt(aktivt: Boolean) {
        context.debugProDataStore.edit { prefs ->
            prefs[TEST_PRO_AKTIVT] = aktivt
        }
    }

    companion object {
        private val TEST_PRO_AKTIVT = booleanPreferencesKey("test_pro_aktivt")
    }
}
