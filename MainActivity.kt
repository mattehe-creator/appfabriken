package se.tmconnect.garantivalvet

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import java.time.LocalDate
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.paminnelse.PaminnelseWorker
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.KopSkarm
import se.tmconnect.garantivalvet.ui.KopViewModel
import se.tmconnect.garantivalvet.ui.KopViewModelFactory
import se.tmconnect.garantivalvet.ui.KvittoHelskarm
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val viewModel: KopViewModel by viewModels {
        val databas = GarantiDatabas.hamta(applicationContext)
        val kvittoLager = KvittoLager(applicationContext)
        val installningarLager = InstallningarLager(applicationContext)
        KopViewModelFactory(
            KopRepository(databas.kopDao(), kvittoLager),
            kvittoLager,
            installningarLager,
            applicationContext,
        )
    }

    // Resultatkontrakt för bildval
    private val pickImageContract = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.sattTillfalligtKvitto(it)
        }
    }

    // Resultatkontrakt för kamera
    private var kameraUri: Uri? = null
    private val takePictureContract = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && kameraUri != null) {
            viewModel.sattTillfalligtKvitto(kameraUri!!)
        }
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
            AppfabrikTema {
                val kopLista by viewModel.kopLista.collectAsState()
                val aktivSkarm by viewModel.aktivSkarm.collectAsState()
                val valtKop by viewModel.valtKop.collectAsState()
                val paminnelserPa by viewModel.paminnelserPa.collectAsState()

                when (aktivSkarm) {
                    KopSkarm.Lista -> {
                        KopListaSkarm(
                            kopLista = kopLista,
                            onLaggTill = viewModel::visaLaggTill,
                            onOppna = viewModel::visaDetalj,
                            paminnelserPa = paminnelserPa,
                            onPaminnelserAndras = { pa ->
                                if (pa) {
                                    // För Android 13+ behöver vi begära behörighet
                                    if (Build.VERSION.SDK_INT >= 33) {
                                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        // För äldre versioner går det direkt
                                        viewModel.sattPaminnelser(true)
                                    }
                                } else {
                                    // Avslå påminnelser direkt
                                    viewModel.sattPaminnelser(false)
                                    behorighetNekad = false
                                }
                            },
                            behorighetNekad = behorighetNekad
                        )
                    }

                    is KopSkarm.Detalj -> {
                        val kop = valtKop
                        if (kop != null) {
                            KopDetaljSkarm(
                                kop = kop,
                                kvittoUri = viewModel.sparatKvittoUri(kop),
                                onOppnaKvitto = { viewModel.visaKvittoHelskarm(kop.id) },
                                onAndra = { viewModel.visaAndra(kop.id) },
                                onTaBort = viewModel::taBortValtKop,
                                onTillbaka = viewModel::visaLista,
                            )
                        }
                    }

                    KopSkarm.LaggTill -> {
                        KopFormularSkarm(
                            titel = stringResource(R.string.lagg_till_titel),
                            initialVad = "",
                            initialVar = "",
                            initialGarantiManader = "24",
                            initialPris = null,
                            initialAnteckning = null,
                            initialKopdatum = LocalDate.now(),
                            kvittoUri = viewModel.kvittoUriForFormular(null),
                            onValjBild = {
                                pickImageContract.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                )
                            },
                            onTaFoto = {
                                val uri = viewModel.skapaKameraKvittoUri()
                                kameraUri = uri
                                takePictureContract.launch(uri)
                            },
                            onTaBortKvitto = viewModel::markeraKvittoForBorttagning,
                            onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning ->
                                viewModel.sparaNyttKop(
                                    vad = vad,
                                    varKopt = varKopt,
                                    kopdatum = kopdatum,
                                    garantiManader = garantiManader,
                                    prisOre = prisOre,
                                    anteckning = anteckning,
                                )
                            },
                            onAvbryt = viewModel::visaLista,
                        )
                    }

                    is KopSkarm.KvittoHelskarm -> {
                        val kop = valtKop
                        val kvittoUri = kop?.let { viewModel.sparatKvittoUri(it) }
                        if (kop != null && kvittoUri != null) {
                            KvittoHelskarm(
                                kvittoUri = kvittoUri,
                                onTillbaka = { viewModel.visaDetalj(kop.id) },
                            )
                        }
                    }

                    is KopSkarm.Andra -> {
                        val kop = valtKop
                        if (kop != null) {
                            KopFormularSkarm(
                                titel = stringResource(R.string.andra_titel),
                                initialVad = kop.vad,
                                initialVar = kop.varKopt.orEmpty(),
                                initialGarantiManader = kop.garantiManader.toString(),
                                initialPris = kop.prisOre?.let { (it / 100.0).toString() },
                                initialAnteckning = kop.anteckning,
                                initialKopdatum = kop.kopdatum,
                                kvittoUri = viewModel.kvittoUriForFormular(kop),
                                onValjBild = {
                                    pickImageContract.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                    )
                                },
                                onTaFoto = {
                                    val uri = viewModel.skapaKameraKvittoUri()
                                    kameraUri = uri
                                    takePictureContract.launch(uri)
                                },
                                onTaBortKvitto = viewModel::markeraKvittoForBorttagning,
                                onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning ->
                                    viewModel.uppdateraKop(
                                        kop.copy(
                                            vad = vad.trim(),
                                            varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                                            kopdatum = kopdatum,
                                            garantiManader = garantiManader,
                                            prisOre = prisOre,
                                            anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
                                        ),
                                    )
                                },
                                onAvbryt = viewModel::visaLista,
                            )
                        }
                    }
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
        val kopId = intent?.getLongExtra(PaminnelseWorker.EXTRA_KOP_ID, -1L) ?: return
        if (kopId >= 0L) {
            viewModel.visaDetalj(kopId)
            intent.removeExtra(PaminnelseWorker.EXTRA_KOP_ID)
        }
    }
}
