package se.tmconnect.husloggen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
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
import se.tmconnect.husloggen.ProKonfig
import se.tmconnect.husloggen.data.HusDatabas
import se.tmconnect.husloggen.pro.ProKallaSkapare
import se.tmconnect.husloggen.data.HusRepository
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.husloggen.paminnelse.PaminnelseWorker
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.husloggen.ui.BildHelskarm
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.HusUiMeddelande
import se.tmconnect.husloggen.ui.HusViewModel
import se.tmconnect.husloggen.ui.HusViewModelFactory
import se.tmconnect.husloggen.ui.InstallningarSkarm
import se.tmconnect.husloggen.ui.PostDetaljSkarm
import se.tmconnect.husloggen.ui.PostFormularSkarm
import se.tmconnect.husloggen.ui.ProDialog
import se.tmconnect.husloggen.ui.UppgiftFormularSkarm
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private var skaOppnaAttGora = false

    private val proKalla by lazy {
        ProKallaSkapare.skapa(
            applicationContext,
            ProKonfig.PRODUKT_ID,
            lifecycleScope,
        )
    }

    private val viewModel: HusViewModel by viewModels {
        val databas = HusDatabas.hamta(applicationContext)
        val bildLager = skapaHusBildLager(applicationContext)
        val paminnelseInstallningar = PaminnelseInstallningarLager(
            applicationContext,
            HusPaminnelseKalla.DATASTORE_NAMN,
        )
        HusViewModelFactory(
            HusRepository(databas.husDao(), bildLager),
            paminnelseInstallningar,
            applicationContext,
            proKalla,
        )
    }

    // Resultatkontrakt för bildval
    private val pickImageContract = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.laggTillBildUri(it) }
    }

    // Resultatkontrakt för kamera
    private var kameraUri: Uri? = null
    private val takePictureContract = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            kameraUri?.let { viewModel.laggTillBildUri(it) }
        }
        kameraUri = null
    }

    // Resultatkontrakt för notisbehörighet
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.sattPaminnelser(true)
            behorighetNekad = false
        } else {
            behorighetNekad = true
        }
    }

    private var behorighetNekad by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hanteraPaminnelseIntent(intent)
        viewModel.synkaPaminnelseJobb()
        enableEdgeToEdge()
        setContent {
            AppfabrikTema(morkt = isSystemInDarkTheme()) {
                var visaInstallningar by rememberSaveable { mutableStateOf(false) }
                var oppnaAttGora by remember { mutableStateOf(skaOppnaAttGora) }
                LaunchedEffect(skaOppnaAttGora) {
                    if (skaOppnaAttGora) {
                        oppnaAttGora = true
                        skaOppnaAttGora = false
                    }
                }
                var visaBildHelskarm by remember { mutableStateOf(false) }
                var bildUrier by remember { mutableStateOf<List<Uri>>(emptyList()) }
                var valdBildIndex by remember { mutableStateOf(0) }
                val uppgifter by viewModel.uppgifter.collectAsState()
                val poster by viewModel.poster.collectAsState()
                val formular by viewModel.formular.collectAsState()
                val postFormular by viewModel.postFormular.collectAsState()
                val postDetaljId by viewModel.postDetaljId.collectAsState()
                val paminnelserPa by viewModel.paminnelserPa.collectAsState()
                val arPro by viewModel.arPro.collectAsState()
                val proDialogSynlig by viewModel.proDialogSynlig.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val markeradGjordText = stringResource(R.string.uppgift_markerad_gjord)
                val uppgiftGjordPostEjSparadText = stringResource(R.string.uppgift_gjord_post_ej_sparad)
                val idag = LocalDate.now()

                LaunchedEffect(viewModel) {
                    viewModel.meddelanden.collect { meddelande ->
                        when (meddelande) {
                            HusUiMeddelande.UppgiftMarkeradGjord -> {
                                snackbarHostState.showSnackbar(markeradGjordText)
                            }
                            HusUiMeddelande.UppgiftMarkeradGjordPostEjSparad -> {
                                snackbarHostState.showSnackbar(uppgiftGjordPostEjSparadText)
                            }
                        }
                    }
                }

                androidx.compose.material3.Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                ) { _ ->
                    when {
                        visaBildHelskarm -> {
                            BildHelskarm(
                                bildUrier = bildUrier,
                                valdBildIndex = valdBildIndex,
                                onStang = { visaBildHelskarm = false },
                            )
                        }
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
                                bildUri = viewModel::bildUri,
                                onBildValj = {
                                    pickImageContract.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                                        ),
                                    )
                                },
                                onTaFoto = {
                                    val uri = viewModel.skapaKameraFotoUri()
                                    kameraUri = uri
                                    takePictureContract.launch(uri)
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
                                val bilder = viewModel.lasBilderForPost(post)
                                PostDetaljSkarm(
                                    post = post,
                                    uppgiftNamn = uppgiftNamn,
                                    onAndra = { viewModel.visaAndraPost(post.id) },
                                    onTaBort = { viewModel.taBortPost(post.id) },
                                    onTillbaka = viewModel::stangPostDetalj,
                                    bildFilnamn = bilder,
                                    bildUri = viewModel::bildUri,
                                    onOppnaBild = { index ->
                                        bildUrier = bilder.map(viewModel::bildUri)
                                        valdBildIndex = index
                                        visaBildHelskarm = true
                                    },
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
                                onNyPost = {
                                    if (viewModel.kanLaggaTillNyPost()) {
                                        viewModel.visaNyPost()
                                    } else {
                                        viewModel.visaProDialog()
                                    }
                                },
                                onOppnaUppgift = viewModel::visaAndraUppgift,
                                onOppnaPost = viewModel::visaPostDetalj,
                                onMarkeraGjord = viewModel::markeraSomGjord,
                                oppnaAttGora = oppnaAttGora,
                                paminnelserPa = paminnelserPa,
                                onPaminnelserAndras = { pa ->
                                    if (pa) {
                                        if (Build.VERSION.SDK_INT >= 33) {
                                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            viewModel.sattPaminnelser(true)
                                        }
                                    } else {
                                        viewModel.sattPaminnelser(false)
                                        behorighetNekad = false
                                    }
                                },
                                behorighetNekad = behorighetNekad,
                                arPro = arPro,
                            )
                        }
                    }
                }

                if (proDialogSynlig) {
                    ProDialog(
                        onDismiss = viewModel::stangProDialog,
                        onKopPro = viewModel::kopPro,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        hanteraPaminnelseIntent(intent)
    }

    private fun hanteraPaminnelseIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(PaminnelseWorker.EXTRA_OPPNA_ATT_GORA, false) == true) {
            skaOppnaAttGora = true
            intent.removeExtra(PaminnelseWorker.EXTRA_OPPNA_ATT_GORA)
        }
    }
}
