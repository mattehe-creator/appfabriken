package se.tmconnect.flextid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.tmconnect.flextid.regler.SEMESTERAR_STARTMANAD_STANDARD
import se.tmconnect.flextid.regler.SEMESTERDAGAR_STANDARD
import se.tmconnect.flextid.regler.normaliseraVanligRastMin
import se.tmconnect.flextid.regler.normaliseraVanligSlutMin
import se.tmconnect.flextid.regler.normaliseraVanligStartMin

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

    /** Midsommar-, jul- och nyårsafton räknas som lediga (påminnelser och visning). Standard: ja. */
    val aftnarLediga: Flow<Boolean> =
        context.flextidUiDataStore.data.map { prefs -> prefs[AFTNAR_LEDIGA] ?: true }

    suspend fun sattAftnarLediga(lediga: Boolean) {
        context.flextidUiDataStore.edit { prefs -> prefs[AFTNAR_LEDIGA] = lediga }
    }

    val semesterdagarPerAr: Flow<Int> =
        context.flextidUiDataStore.data.map { prefs ->
            (prefs[SEMESTERDAGAR_PER_AR] ?: SEMESTERDAGAR_STANDARD).coerceIn(0, MAX_SEMESTERDAGAR)
        }

    suspend fun sattSemesterdagarPerAr(dagar: Int) {
        context.flextidUiDataStore.edit { prefs ->
            prefs[SEMESTERDAGAR_PER_AR] = dagar.coerceIn(0, MAX_SEMESTERDAGAR)
        }
    }

    /** Semesterårets första månad: 4 (april, semesterlagen) eller 1 (kalenderår). */
    val semesterarStartmanad: Flow<Int> =
        context.flextidUiDataStore.data.map { prefs ->
            if (prefs[SEMESTERAR_STARTMANAD] == 1) 1 else SEMESTERAR_STARTMANAD_STANDARD
        }

    suspend fun sattSemesterarStartmanad(manad: Int) {
        context.flextidUiDataStore.edit { prefs ->
            prefs[SEMESTERAR_STARTMANAD] = if (manad == 1) 1 else SEMESTERAR_STARTMANAD_STANDARD
        }
    }

    /** Tak för plussaldo och golv för minussaldo i minuter (golvet lagras som positivt tal). Null = ingen gräns. */
    val saldoTakMin: Flow<Int?> = context.flextidUiDataStore.data.map { prefs -> prefs[SALDO_TAK_MIN]?.takeIf { it > 0 } }
    val saldoGolvMin: Flow<Int?> = context.flextidUiDataStore.data.map { prefs -> prefs[SALDO_GOLV_MIN]?.takeIf { it > 0 }?.let { -it } }

    suspend fun sattSaldoGranser(takMin: Int?, golvMin: Int?) {
        context.flextidUiDataStore.edit { prefs ->
            if (takMin != null && takMin > 0) prefs[SALDO_TAK_MIN] = takMin.coerceAtMost(MAX_GRANS_MIN) else prefs.remove(SALDO_TAK_MIN)
            val golv = golvMin?.let { kotlin.math.abs(it) }
            if (golv != null && golv > 0) prefs[SALDO_GOLV_MIN] = golv.coerceAtMost(MAX_GRANS_MIN) else prefs.remove(SALDO_GOLV_MIN)
        }
    }

    val vanligStartMin: Flow<Int> =
        context.flextidUiDataStore.data.map { prefs ->
            normaliseraVanligStartMin(prefs[VANLIG_START_MIN])
        }

    val vanligSlutMin: Flow<Int> =
        context.flextidUiDataStore.data.map { prefs ->
            normaliseraVanligSlutMin(prefs[VANLIG_SLUT_MIN])
        }

    val vanligRastMin: Flow<Int> =
        context.flextidUiDataStore.data.map { prefs ->
            normaliseraVanligRastMin(prefs[VANLIG_RAST_MIN])
        }

    suspend fun sattVanligaTider(startMin: Int, slutMin: Int, rastMin: Int) {
        context.flextidUiDataStore.edit { prefs ->
            prefs[VANLIG_START_MIN] = normaliseraVanligStartMin(startMin)
            prefs[VANLIG_SLUT_MIN] = normaliseraVanligSlutMin(slutMin)
            prefs[VANLIG_RAST_MIN] = normaliseraVanligRastMin(rastMin)
        }
    }

    companion object {
        const val MAX_GRANS_MIN = 500 * 60
        private val SALDO_TAK_MIN = intPreferencesKey("saldo_tak_min")
        private val SALDO_GOLV_MIN = intPreferencesKey("saldo_golv_min")
        const val MAX_SEMESTERDAGAR = 60
        private val AFTNAR_LEDIGA = booleanPreferencesKey("aftnar_lediga")
        private val SEMESTERDAGAR_PER_AR = intPreferencesKey("semesterdagar_per_ar")
        private val SEMESTERAR_STARTMANAD = intPreferencesKey("semesterar_startmanad")
        private val VALKOMMEN_VISAD = booleanPreferencesKey("valkommen_visad")
        private val TEMA_LAGE = stringPreferencesKey("tema_lage")
        private val EXPORT_AR_MANAD = stringPreferencesKey("export_ar_manad")
        private val VANLIG_START_MIN = intPreferencesKey("vanlig_start_min")
        private val VANLIG_SLUT_MIN = intPreferencesKey("vanlig_slut_min")
        private val VANLIG_RAST_MIN = intPreferencesKey("vanlig_rast_min")
    }
}
