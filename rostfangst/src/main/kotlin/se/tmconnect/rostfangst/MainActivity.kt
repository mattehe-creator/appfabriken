package se.tmconnect.rostfangst

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import se.tmconnect.karna.export.skrivCsvMedBom
import se.tmconnect.karna.tema.AppfabrikTema
import se.tmconnect.rostfangst.data.InstallningarLager
import se.tmconnect.rostfangst.data.skapaRostfangstRepository
import se.tmconnect.rostfangst.rost.RostLyssnare
import se.tmconnect.rostfangst.ui.RostfangstApp
import se.tmconnect.rostfangst.ui.RostfangstViewModel
import se.tmconnect.rostfangst.ui.RostfangstViewModelFactory

class MainActivity : ComponentActivity() {
    private var lyssnare: RostLyssnare? = null
    private var startInspelningVidStart = false

    private val installningarLager by lazy { InstallningarLager(applicationContext) }

    private val viewModel: RostfangstViewModel by viewModels {
        RostfangstViewModelFactory(
            skapaRostfangstRepository(applicationContext),
            installningarLager,
            applicationContext,
        )
    }

    private val requestAudio =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startaInspelning()
            }
        }

    private val requestNotis =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Behörigheten behövs bara när påminnelser schemaläggs.
        }

    private val skapaCsv =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri == null) return@registerForActivityResult
            lifecycleScope.launch {
                val csv = viewModel.hamtaHistorikCsv()
                contentResolver.openOutputStream(uri)?.use { skrivCsvMedBom(it, csv) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startInspelningVidStart = intent.getBooleanExtra(EXTRA_START_INSPELNING, false) ||
            intent.action == ACTION_SAG_DET
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotis.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent {
            AppfabrikTema {
                val snackbar = remember { SnackbarHostState() }
                val meddelande by viewModel.meddelande.collectAsStateWithLifecycle()
                LaunchedEffect(meddelande) {
                    val key = meddelande ?: return@LaunchedEffect
                    val text = when (key) {
                        "tom" -> getString(R.string.fel_tom_inspelning)
                        "klar" -> getString(R.string.utfor_klar)
                        "kalender" -> getString(R.string.utfor_kalender)
                        "saknar_tid" -> getString(R.string.fel_saknar_tid)
                        "sparad" -> getString(R.string.installningar_sparade)
                        "ha_adress" -> getString(R.string.fel_ha_adress)
                        "ha_saknas" -> getString(R.string.fel_ha_saknas)
                        else -> key
                    }
                    snackbar.showSnackbar(text)
                    viewModel.rensaMeddelande()
                }
                Box(Modifier) {
                    RostfangstApp(
                        viewModel = viewModel,
                        onStartInspelning = ::beOmLjudOchStarta,
                        onExporteraCsv = {
                            skapaCsv.launch("rostfangst-historik.csv")
                        },
                        onUtforKalender = { intent ->
                            startActivity(intent)
                        },
                    )
                    SnackbarHost(hostState = snackbar)
                }
            }
        }
        if (startInspelningVidStart) {
            beOmLjudOchStarta()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_START_INSPELNING, false) || intent.action == ACTION_SAG_DET) {
            beOmLjudOchStarta()
        }
    }

    override fun onDestroy() {
        lyssnare?.stopp()
        lyssnare = null
        super.onDestroy()
    }

    private fun beOmLjudOchStarta() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED -> startaInspelning()
            else -> requestAudio.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startaInspelning() {
        viewModel.borjaLyssna()
        lyssnare?.stopp()
        lyssnare = RostLyssnare(
            context = this,
            onDelresultat = viewModel::uppdateraDeltext,
            onFardig = { text ->
                viewModel.avslutaLyssning(text)
                lyssnare?.stopp()
            },
            onFel = {
                viewModel.avbrytLyssning()
                lyssnare?.stopp()
            },
        ).also { it.starta() }
    }

    companion object {
        const val EXTRA_START_INSPELNING = "se.tmconnect.rostfangst.extra.START_INSPELNING"
        const val ACTION_SAG_DET = "se.tmconnect.rostfangst.action.SAG_DET"
    }
}
