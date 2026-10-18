package se.tmconnect.flextid.paminnelse

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager

private val Context.flextidPaminnelseDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "flextid_paminnelse_val",
)

/**
 * Påminnelsetyper för Flextid plus synk mot [PaminnelseInstallningarLager] (WorkManager på/av).
 */
class FlextidPaminnelseInstallningarLager(context: Context) {
    private val appContext = context.applicationContext
    val karnaInstallningar: PaminnelseInstallningarLager =
        PaminnelseInstallningarLager(appContext, DATASTORE_NAMN)

    val stamplaUtPa: Flow<Boolean> =
        appContext.flextidPaminnelseDataStore.data.map { prefs ->
            prefs[STAMPLA_UT_PA] ?: false
        }

    val stamplaInPa: Flow<Boolean> =
        appContext.flextidPaminnelseDataStore.data.map { prefs ->
            prefs[STAMPLA_IN_PA] ?: false
        }

    val nagonPaminnelsePa: Flow<Boolean> =
        combine(stamplaUtPa, stamplaInPa) { ut, inPa -> ut || inPa }

    suspend fun sattStamplaUtPa(pa: Boolean) {
        appContext.flextidPaminnelseDataStore.edit { prefs -> prefs[STAMPLA_UT_PA] = pa }
        synkaKarnaPaminnelserPa()
    }

    suspend fun sattStamplaInPa(pa: Boolean) {
        appContext.flextidPaminnelseDataStore.edit { prefs -> prefs[STAMPLA_IN_PA] = pa }
        synkaKarnaPaminnelserPa()
    }

    private suspend fun synkaKarnaPaminnelserPa() {
        val ut = stamplaUtPa.first()
        val inPa = stamplaInPa.first()
        karnaInstallningar.sattPaminnelserPa(ut || inPa)
    }

    companion object {
        const val DATASTORE_NAMN = "flextid_paminnelser"
        private val STAMPLA_UT_PA = booleanPreferencesKey("stampla_ut_pa")
        private val STAMPLA_IN_PA = booleanPreferencesKey("stampla_in_pa")
    }
}
