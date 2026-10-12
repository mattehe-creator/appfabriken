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
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import se.tmconnect.garantivalvet.data.GarantiDatabas
import se.tmconnect.garantivalvet.data.InstallningarLager
import se.tmconnect.garantivalvet.data.KopRepository
import se.tmconnect.garantivalvet.data.KvittoLager
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.export.PdfExportResult
import se.tmconnect.garantivalvet.export.reklamationsPdfFilnamn
import se.tmconnect.garantivalvet.export.skrivUnderlagTillPdf
import se.tmconnect.garantivalvet.export.sakerhetskopiaFilnamn
import se.tmconnect.garantivalvet.paminnelse.PaminnelseWorker
import se.tmconnect.garantivalvet.regler.UnderlagEtiketter
import se.tmconnect.garantivalvet.regler.byggUnderlag
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.KopSkarm
import se.tmconnect.garantivalvet.ui.KopViewModel
import se.tmconnect.garantivalvet.ui.KopViewModelFactory
import se.tmconnect.garantivalvet.ui.SakerhetskopiaMeddelande
import se.tmconnect.garantivalvet.ui.KvittoHelskarm
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.pro.skapaPlayProKalla
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val proKalla: ProKalla by lazy {
        skapaPlayProKalla(
            applicationContext,
            ProKonfig.PRODUKT_ID,
            lifecycleScope,
        )
    }

    private val viewModel: KopViewModel by viewModels {
        val databas = GarantiDatabas.hamta(applicationContext)
        val kvittoLager = KvittoLager(applicationContext)
        val installningarLager = InstallningarLager(applicationContext)
        KopViewModelFactory(
            KopRepository(databas.kopDao(), kvittoLager),
            kvittoLager,
            installningarLager,
            applicationContext,
            proKalla,
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

    // Resultatkontrakt för PDF-export
    private val createPdfContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            exportKop?.let { kop ->
                try {
                    // Bygg underlaget
                    val etiketter = UnderlagEtiketter(
                        vad = getString(R.string.underlag_vad),
                        varKopt = getString(R.string.underlag_var_kopt),
                        kopdatum = getString(R.string.underlag_kopdatum),
                        pris = getString(R.string.underlag_pris),
                        garantiManader = getString(R.string.underlag_garanti_manader),
                        garantiSlut = getString(R.string.underlag_garanti_slut),
                        reklamationSlut = getString(R.string.underlag_reklamation_slut),
                        presumtionSlut = getString(R.string.underlag_presumtion_slut),
                        reklamationStatus = getString(R.string.underlag_reklamation_status),
                        statusFelAntasFunnits = getString(R.string.underlag_status_fel_antas_funnits),
                        statusGaller = getString(R.string.underlag_status_galler),
                        statusUtgangen = getString(R.string.underlag_status_utgangen),
                        anteckning = getString(R.string.underlag_anteckning),
                        skapad = getString(R.string.underlag_skapad)
                    )
                    
                    val dokumentRubrik = getString(R.string.underlag_rubrik)
                    val locale = resources.configuration.locales[0]
                    
                    val underlag = byggUnderlag(
                        kop,
                        LocalDate.now(),
                        etiketter,
                        dokumentRubrik,
                        locale
                    )
                    
                    // Hämta kvittobitmap om det finns ett kvitto
                    val kvittoBitmap = viewModel.sparatKvittoUri(kop)?.let { kvittoUri ->
                            try {
                                contentResolver.openInputStream(kvittoUri)?.use { stream ->
                                    android.graphics.BitmapFactory.decodeStream(stream)
                                }
                            } catch (_: Exception) {
                                null
                            }
                    }
                    
                    // Skriv PDF till fil
                    contentResolver.openOutputStream(uri)?.use { stream ->
                        val resultat = skrivUnderlagTillPdf(underlag, stream, kvittoBitmap)
                        when (resultat) {
                            PdfExportResult.Ok -> viewModel.meddelaExportSparad()
                            is PdfExportResult.Fel -> viewModel.meddelaExportMisslyckades()
                        }
                    }
                    
                    exportKop = null
                } catch (e: Exception) {
                    viewModel.meddelaExportMisslyckades()
                    exportKop = null
                }
            }
        }
    }

    // Resultatkontrakt för säkerhetskopia
    private val createSakerhetskopiaContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.sparaSakerhetskopia(uri)
        }
    }

    // Resultatkontrakt för säkerhetskopia import
    private val openSakerhetskopiaContract = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.lasInSakerhetskopia(uri)
        }
    }

    private var behorighetNekad by mutableStateOf(false)
    private var exportKop: Kop? = null

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
                val arPro by viewModel.arPro.collectAsState()
                val proDialogSynlig by viewModel.proDialogSynlig.collectAsState()
                val exportPdfMeddelande by viewModel.exportPdfMeddelande.collectAsState()
                val sakerhetskopiaMeddelande by viewModel.sakerhetskopiaMeddelande.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                sakerhetskopiaMeddelande?.let { meddelande ->
                    val text = when (meddelande) {
                        SakerhetskopiaMeddelande.Sparad ->
                            stringResource(R.string.sakerhetskopia_sparad)
                        SakerhetskopiaMeddelande.KundeInteLasas ->
                            stringResource(R.string.sakerhetskopia_kunde_inte_lasas)
                        is SakerhetskopiaMeddelande.Importerad -> {
                            stringResource(
                                R.string.sakerhetskopia_importerad,
                                meddelande.tillagda,
                                meddelande.hoppadeOver
                            )
                        }
                        SakerhetskopiaMeddelande.NyareVersion ->
                            stringResource(R.string.sakerhetskopia_nyare_version)
                    }
                    LaunchedEffect(meddelande) {
                        snackbarHostState.showSnackbar(text)
                        viewModel.rensaSakerhetskopiaMeddelande()
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                when (aktivSkarm) {
                    KopSkarm.Lista -> {
                        KopListaSkarm(
                            kopLista = kopLista,
                            onLaggTill = {
                                if (viewModel.kanLaggaTillNy()) {
                                    viewModel.visaLaggTill()
                                } else {
                                    viewModel.visaProDialog()
                                }
                            },
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
                            behorighetNekad = behorighetNekad,
                            arPro = arPro,
                            proDialogSynlig = proDialogSynlig,
                            onVisaProDialog = viewModel::visaProDialog,
                            onStangProDialog = viewModel::stangProDialog,
                            onKopPro = { activity -> proKalla.kop(activity) },
                            onSparaSakerhetskopia = {
                                createSakerhetskopiaContract.launch(
                                    sakerhetskopiaFilnamn(LocalDate.now()),
                                )
                            },
                            onHamtaSakerhetskopia = {
                                openSakerhetskopiaContract.launch(
                                    arrayOf("application/zip", "application/octet-stream")
                                )
                            },
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
                                arPro = arPro,
                                onExportPdf = {
                                    exportKop = kop
                                    createPdfContract.launch(reklamationsPdfFilnamn(kop.vad, kop.kopdatum))
                                },
                                onDelaPdf = {
                                    try {
                                        val etiketter = UnderlagEtiketter(
                                            vad = getString(R.string.underlag_vad),
                                            varKopt = getString(R.string.underlag_var_kopt),
                                            kopdatum = getString(R.string.underlag_kopdatum),
                                            pris = getString(R.string.underlag_pris),
                                            garantiManader = getString(R.string.underlag_garanti_manader),
                                            garantiSlut = getString(R.string.underlag_garanti_slut),
                                            reklamationSlut = getString(R.string.underlag_reklamation_slut),
                                            presumtionSlut = getString(R.string.underlag_presumtion_slut),
                                            reklamationStatus = getString(R.string.underlag_reklamation_status),
                                            statusFelAntasFunnits = getString(R.string.underlag_status_fel_antas_funnits),
                                            statusGaller = getString(R.string.underlag_status_galler),
                                            statusUtgangen = getString(R.string.underlag_status_utgangen),
                                            anteckning = getString(R.string.underlag_anteckning),
                                            skapad = getString(R.string.underlag_skapad),
                                        )

                                        val dokumentRubrik = getString(R.string.underlag_rubrik)
                                        val locale = resources.configuration.locales[0]

                                        val underlag = byggUnderlag(
                                            kop,
                                            LocalDate.now(),
                                            etiketter,
                                            dokumentRubrik,
                                            locale,
                                        )

                                        val kvittoBitmap = viewModel.sparatKvittoUri(kop)?.let { kvittoUri ->
                                            try {
                                                contentResolver.openInputStream(kvittoUri)?.use { stream ->
                                                    android.graphics.BitmapFactory.decodeStream(stream)
                                                }
                                            } catch (_: Exception) {
                                                null
                                            }
                                        }

                                        val pdfFil = File(
                                            cacheDir,
                                            "export/${reklamationsPdfFilnamn(kop.vad, kop.kopdatum)}",
                                        )
                                        pdfFil.parentFile?.mkdirs()

                                        FileOutputStream(pdfFil).use { stream ->
                                            val resultat = skrivUnderlagTillPdf(underlag, stream, kvittoBitmap)
                                            if (resultat is PdfExportResult.Ok) {
                                                val fileUri = FileProvider.getUriForFile(
                                                    this@MainActivity,
                                                    KvittoLager.fileProviderAuthority(this@MainActivity),
                                                    pdfFil,
                                                )

                                                val intent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "application/pdf"
                                                    putExtra(Intent.EXTRA_STREAM, fileUri)
                                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                }

                                                startActivity(
                                                    Intent.createChooser(
                                                        intent,
                                                        getString(R.string.export_dela),
                                                    ),
                                                )
                                            }
                                        }
                                    } catch (_: Exception) {
                                    }
                                },
                                onVisaProDialog = viewModel::visaProDialog,
                                exportPdfMeddelande = exportPdfMeddelande,
                                onExportMeddelandeVisat = viewModel::rensaExportMeddelande,
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
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter),
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
        val kopId = intent?.getLongExtra(PaminnelseWorker.EXTRA_KOP_ID, -1L) ?: return
        if (kopId >= 0L) {
            viewModel.visaDetalj(kopId)
            intent.removeExtra(PaminnelseWorker.EXTRA_KOP_ID)
        }
    }
}
