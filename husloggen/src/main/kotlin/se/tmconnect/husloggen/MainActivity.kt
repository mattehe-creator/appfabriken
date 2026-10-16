package se.tmconnect.husloggen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.HusUiMeddelande
import se.tmconnect.husloggen.ui.HusViewModel
import se.tmconnect.husloggen.ui.HusViewModelFactory
import se.tmconnect.husloggen.ui.InstallningarSkarm
import se.tmconnect.husloggen.ui.UppgiftFormularSkarm
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val viewModel: HusViewModel by viewModels {
        val databas = HusDatabas.hamta(applicationContext)
        HusViewModelFactory(HusRepository(databas.husDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppfabrikTema(morkt = isSystemInDarkTheme()) {
                var visaInstallningar by rememberSaveable { mutableStateOf(false) }
                val uppgifter by viewModel.uppgifter.collectAsState()
                val poster by viewModel.poster.collectAsState()
                val formular by viewModel.formular.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val markeradGjordText = stringResource(R.string.uppgift_markerad_gjord)

                LaunchedEffect(viewModel) {
                    viewModel.meddelanden.collect { meddelande ->
                        when (meddelande) {
                            HusUiMeddelande.UppgiftMarkeradGjord -> {
                                snackbarHostState.showSnackbar(markeradGjordText)
                            }
                        }
                    }
                }

                androidx.compose.material3.Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                ) { _ ->
                    when {
                        formular != null -> {
                            val tillstand = formular!!
                            UppgiftFormularSkarm(
                                tillstand = tillstand,
                                onNamnAndras = viewModel::uppdateraFormularNamn,
                                onIntervallAndras = viewModel::uppdateraFormularIntervall,
                                onStartdatumAndras = viewModel::uppdateraFormularStartdatum,
                                onPaminnelseAndras = viewModel::uppdateraFormularPaminnelse,
                                onAnteckningAndras = viewModel::uppdateraFormularAnteckning,
                                onValjForslag = viewModel::appliceraForslag,
                                onSpara = viewModel::sparaFormular,
                                onTaBort = viewModel::taBortFormularUppgift,
                                onStang = viewModel::stangFormular,
                            )
                        }
                        visaInstallningar -> {
                            InstallningarSkarm(onTillbaka = { visaInstallningar = false })
                        }
                        else -> {
                            HusStartSkarm(
                                uppgifter = uppgifter,
                                poster = poster,
                                onInstallningar = { visaInstallningar = true },
                                onNyUppgift = viewModel::visaNyUppgift,
                                onOppnaUppgift = viewModel::visaAndraUppgift,
                                onMarkeraGjord = viewModel::markeraSomGjord,
                            )
                        }
                    }
                }
            }
        }
    }
}
