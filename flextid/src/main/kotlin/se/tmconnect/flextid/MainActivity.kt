package se.tmconnect.flextid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.export.ManadsCsvTexter
import se.tmconnect.flextid.export.manadsCsvFilnamn
import se.tmconnect.flextid.ui.FlextidMeddelande
import se.tmconnect.flextid.ui.FlextidStartSkarm
import se.tmconnect.flextid.ui.FlextidViewModel
import se.tmconnect.flextid.ui.FlextidViewModelFactory
import se.tmconnect.karna.export.skrivCsvMedBom
import se.tmconnect.karna.tema.AppfabrikTema
import se.tmconnect.karna.tema.TemaLage

class MainActivity : ComponentActivity() {
    private val uiInstallningarLager by lazy { UiInstallningarLager(applicationContext) }

    private val viewModel: FlextidViewModel by viewModels {
        FlextidViewModelFactory(
            skapaFlextidRepository(applicationContext),
            uiInstallningarLager,
            applicationContext,
        )
    }

    private val skapaCsvContract =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri != null) {
                viewModel.skrivTillUri(uri) { out ->
                    val locale = Locale.getDefault()
                    val texter =
                        ManadsCsvTexter(
                            rubriker =
                                listOf(
                                    getString(R.string.csv_datum),
                                    getString(R.string.csv_start),
                                    getString(R.string.csv_slut),
                                    getString(R.string.csv_rast),
                                    getString(R.string.csv_arbetad),
                                    getString(R.string.csv_utfall),
                                    getString(R.string.csv_typ),
                                ),
                        )
                    val csv = viewModel.skapaManadsCsv(texter, locale)
                    skrivCsvMedBom(out, csv)
                }
            }
        }

    private val skapaJsonContract =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri != null) {
                viewModel.skrivTillUri(uri) { out ->
                    val json = viewModel.skapaSakerhetskopiaJson()
                    out.write(json.toByteArray(Charsets.UTF_8))
                }
            }
        }

    private val oppnaJsonContract =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                viewModel.lasFranUri(uri) { input ->
                    input.bufferedReader().readText()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val temaLage by viewModel.temaLage.collectAsStateWithLifecycle()
            val morkt =
                when (temaLage) {
                    TemaLage.MORKT -> true
                    TemaLage.LJUST -> false
                    TemaLage.SYSTEM -> isSystemInDarkTheme()
                }
            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                viewModel.meddelanden.collect { meddelande ->
                    val text =
                        when (meddelande) {
                            FlextidMeddelande.Sparat -> getString(R.string.meddelande_sparat)
                            FlextidMeddelande.Borttaget -> getString(R.string.meddelande_borttaget)
                            FlextidMeddelande.Exporterat -> getString(R.string.meddelande_exporterat)
                            FlextidMeddelande.Importerat -> getString(R.string.meddelande_importerat)
                            FlextidMeddelande.SlutForeStart -> getString(R.string.fel_slut_fore_start)
                            FlextidMeddelande.ImportFel -> getString(R.string.meddelande_import_fel)
                        }
                    snackbarHostState.showSnackbar(text)
                }
            }

            AppfabrikTema(morkt = morkt) {
                FlextidStartSkarm(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier.fillMaxSize(),
                    onExporteraCsv = {
                        val arManad = viewModel.exportArManad.value
                        skapaCsvContract.launch(manadsCsvFilnamn(arManad))
                    },
                    onExporteraSakerhetskopia = {
                        skapaJsonContract.launch("flextid-sakerhetskopia.json")
                    },
                    onImporteraSakerhetskopia = {
                        oppnaJsonContract.launch(arrayOf("application/json", "text/plain", "*/*"))
                    },
                    locale = Locale.getDefault(),
                )
            }
        }
    }
}
