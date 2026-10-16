package se.tmconnect.husloggen

import android.net.Uri
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.time.LocalDate
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.HusUiMeddelande
import se.tmconnect.husloggen.ui.HusViewModel
import se.tmconnect.husloggen.ui.HusViewModelFactory
import se.tmconnect.husloggen.ui.InstallningarSkarm
import se.tmconnect.husloggen.ui.PostDetaljSkarm
import se.tmconnect.husloggen.ui.PostFormularSkarm
import se.tmconnect.husloggen.ui.UppgiftFormularSkarm
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val viewModel: HusViewModel by viewModels {
        val databas = HusDatabas.hamta(applicationContext)
        val bildLager = skapaHusBildLager(applicationContext)
        HusViewModelFactory(HusRepository(databas.husDao(), bildLager))
    }

    // Result contracts for image picking and camera
    private val pickImageContract = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { viewModel.laggTillBildUri(it) }
    }

    private val takePictureContract = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            viewModel.skapaKameraFotoUri()?.let { uri ->
                viewModel.laggTillBildUri(uri)
            }
        }
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
                val postFormular by viewModel.postFormular.collectAsState()
                val postDetaljId by viewModel.postDetaljId.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val markeradGjordText = stringResource(R.string.uppgift_markerad_gjord)
                val idag = LocalDate.now()

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
                        postFormular != null -> {
                            val tillstand = postFormular!!
                            PostFormularSkarm(
                                tillstand = tillstand,
                                uppgifter = uppgifter,
                                idag = idag,
                                onVadAndras = viewModel::uppdateraPostVad,
                                onDatumAndras = viewModel::uppdateraPostDatum,
                                onTypAndras = viewModel::uppdateraPostTyp,
                                onBeloppAndras = viewModel::uppdateraPostBelopp,
                                onUtforareAndras = viewModel::uppdateraPostUtforare,
                                onUppgiftAndras = viewModel::uppdateraPostUppgift,
                                onAnteckningAndras = viewModel::uppdateraPostAnteckning,
                                onBildValj = {
                                    pickImageContract.launch(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                },
                                onTaFoto = {
                                    takePictureContract.launch(viewModel.skapaKameraFotoUri())
                                },
                                onTaBortBild = viewModel::taBortBildFranFormular,
                                onSpara = viewModel::sparaPostFormular,
                                onTaBort = viewModel::taBortPostFormularPost,
                                onStang = viewModel::stangPostFormular,
                            )
                        }
                        postDetaljId != null -> {
                            val post = poster.find { it.id == postDetaljId }
                            if (post != null) {
                                val uppgiftNamn = post.uppgiftId?.let { id ->
                                    uppgifter.find { it.id == id }?.namn
                                }
                                PostDetaljSkarm(
                                    post = post,
                                    uppgiftNamn = uppgiftNamn,
                                    onAndra = { viewModel.visaAndraPost(post.id) },
                                    onTaBort = { viewModel.taBortPost(post.id) },
                                    onTillbaka = viewModel::stangPostDetalj,
                                )
                            } else {
                                LaunchedEffect(postDetaljId) {
                                    viewModel.stangPostDetalj()
                                }
                            }
                        }
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
                                onNyPost = viewModel::visaNyPost,
                                onOppnaUppgift = viewModel::visaAndraUppgift,
                                onOppnaPost = viewModel::visaPostDetalj,
                                onMarkeraGjord = viewModel::markeraSomGjord,
                            )
                        }
                    }
                }
            }
        }
    }
}
