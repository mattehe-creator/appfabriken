package se.tmconnect.husloggen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
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
import se.tmconnect.husloggen.data.InstallningarLager
import se.tmconnect.husloggen.data.skapaHusRepository
import se.tmconnect.husloggen.paminnelse.HusPaminnelseKalla
import se.tmconnect.husloggen.paminnelse.PaminnelseWorker
import se.tmconnect.karna.paminnelse.PaminnelseInstallningarLager
import se.tmconnect.husloggen.ui.BildHelskarm
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.ValkommenSkarm
import se.tmconnect.husloggen.ui.HusUiMeddelande
import se.tmconnect.husloggen.ui.HusViewModel
import se.tmconnect.husloggen.ui.HusViewModelFactory
import se.tmconnect.husloggen.ui.InstallningarSkarm
import se.tmconnect.husloggen.ui.PostDetaljSkarm
import se.tmconnect.husloggen.ui.PostFormularSkarm
import se.tmconnect.husloggen.ui.ProDialog
import se.tmconnect.husloggen.ui.UppgiftFormularSkarm
import se.tmconnect.karna.tema.AppfabrikTema
import se.tmconnect.husloggen.export.AvlasningCsvTexter
import se.tmconnect.husloggen.export.HusHistorikTexter
import se.tmconnect.husloggen.export.avlasningCsvFilnamn
import se.tmconnect.husloggen.export.husHistorikCsvFilnamn
import se.tmconnect.husloggen.export.husHistorikPdfFilnamn
import se.tmconnect.husloggen.export.skatteKallorText
import se.tmconnect.husloggen.export.sakerhetskopiaFilnamn
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.postBeloppForSammanstallningOre
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var skaOppnaAttGora = false

    private val proKalla by lazy {
        ProKallaSkapare.skapa(
            applicationContext,
            ProKonfig.PRODUKT_ID,
            lifecycleScope,
        )
    }

    private val installningarLager: InstallningarLager by lazy {
        InstallningarLager(applicationContext)
    }

    private val viewModel: HusViewModel by viewModels {
        val databas = HusDatabas.hamta(applicationContext)
        val paminnelseInstallningar = PaminnelseInstallningarLager(
            applicationContext,
            HusPaminnelseKalla.DATASTORE_NAMN,
        )
        HusViewModelFactory(
            skapaHusRepository(applicationContext, databas),
            paminnelseInstallningar,
            installningarLager,
            applicationContext,
            proKalla,
        )
    }

    private val valjMappContract = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
            } catch (_: Exception) {
            }
            viewModel.valtAutoBackupMapp(uri, mappNamn(uri))
        }
    }

    private fun mappNamn(trad: Uri): String? =
        try {
            val dokument = android.provider.DocumentsContract.buildDocumentUriUsingTree(
                trad,
                android.provider.DocumentsContract.getTreeDocumentId(trad),
            )
            contentResolver.query(
                dokument,
                arrayOf(android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { markor -> if (markor.moveToFirst()) markor.getString(0) else null }
        } catch (_: Exception) {
            null
        }

    private val pickImageContract = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.laggTillBildUri(it) }
    }

    private var kameraUri: Uri? = null
    private val takePictureContract = registerForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            kameraUri?.let { viewModel.laggTillBildUri(it) }
        }
        kameraUri = null
    }

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

    // Resultatkontrakt för säkerhetskopia
    private val createSakerhetskopiaContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.sparaSakerhetskopia(uri)
        }
    }

    private val hamtaSakerhetskopiaContract = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.lasInSakerhetskopia(uri)
        }
    }

    private val valjDokumentContract = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.valjDokumentFil(uri)
        }
    }

    private val createHistorikPdfContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri: Uri? ->
        if (uri != null) {
            sparaHistorikPdf(uri)
        }
    }

    private val createHistorikCsvContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri != null) {
            sparaHistorikCsv(uri)
        }
    }

    private var avlasningCsvMatareId: Long? = null
    private val createAvlasningCsvContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        val matareId = avlasningCsvMatareId
        if (uri != null && matareId != null) {
            viewModel.sparaAvlasningCsv(uri, matareId, avlasningCsvTexter(), resources.configuration.locales[0])
        }
        avlasningCsvMatareId = null
    }

    private var behorighetNekad by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hanteraPaminnelseIntent(intent)
        viewModel.synkaPaminnelseJobb()
        viewModel.synkaAutoBackupJobb()
        setContent {
            val temaLage by viewModel.temaLage.collectAsState()
            val visaValkommen by viewModel.visaValkommen.collectAsState()
            val morkt = temaLage.arMorkt(isSystemInDarkTheme())
            DisposableEffect(morkt) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { morkt },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { morkt },
                )
                onDispose {}
            }
            AppfabrikTema(morkt = morkt) {
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
                val matare by viewModel.matare.collectAsState()
                val dokument by viewModel.dokument.collectAsState()
                val planeradeKostnader by viewModel.planeradeKostnader.collectAsState()
                val planFormular by viewModel.planFormular.collectAsState()
                val dokumentDetaljId by viewModel.dokumentDetaljId.collectAsState()
                val dokumentFormular by viewModel.dokumentFormular.collectAsState()
                val matareDetaljId by viewModel.matareDetaljId.collectAsState()
                val matareFormular by viewModel.matareFormular.collectAsState()
                val avlasningFormular by viewModel.avlasningFormular.collectAsState()
                val valdMatareAvlasningar by viewModel.valdMatareAvlasningar.collectAsState()
                val formular by viewModel.formular.collectAsState()
                val postFormular by viewModel.postFormular.collectAsState()
                val postDetaljId by viewModel.postDetaljId.collectAsState()
                val paminnelserPa by viewModel.paminnelserPa.collectAsState()
                val arPro by viewModel.arPro.collectAsState()
                val proDialogSynlig by viewModel.proDialogSynlig.collectAsState()
                val autoBackup by viewModel.autoBackup.collectAsState()
                val forsaljningsAr by viewModel.forsaljningsAr.collectAsState()
                val markeradGjordText = stringResource(R.string.uppgift_markerad_gjord)
                val uppgiftGjordPostEjSparadText = stringResource(R.string.uppgift_gjord_post_ej_sparad)
                val sakerhetskopiaSparadText = stringResource(R.string.sakerhetskopia_sparad)
                val sakerhetskopiaKundeInteLasasText = stringResource(R.string.sakerhetskopia_kunde_inte_lasas)
                val sakerhetskopiaNyareVersionText = stringResource(R.string.sakerhetskopia_nyare_version)
                val historikPdfSparadText = stringResource(R.string.export_pdf_sparad)
                val historikPdfFelText = stringResource(R.string.export_pdf_misslyckades)
                val historikCsvSparadText = stringResource(R.string.export_csv_sparad)
                val historikCsvFelText = stringResource(R.string.export_csv_misslyckades)
                val avlasningCsvSparadText = stringResource(R.string.export_csv_sparad)
                val avlasningCsvFelText = stringResource(R.string.export_csv_misslyckades)
                val dokumentForStortText = stringResource(R.string.dokument_for_stort)
                val dokumentOgiltigTypText = stringResource(R.string.dokument_ogiltig_typ)
                val dokumentKundeInteLaggasTillText = stringResource(R.string.dokument_kunde_inte_laggas_till)
                val idag = LocalDate.now()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(viewModel) {
                    viewModel.meddelanden.collect { meddelande ->
                        val text = when (meddelande) {
                            HusUiMeddelande.UppgiftMarkeradGjord -> markeradGjordText
                            HusUiMeddelande.UppgiftMarkeradGjordPostEjSparad -> uppgiftGjordPostEjSparadText
                            HusUiMeddelande.SakerhetskopiaSparad -> sakerhetskopiaSparadText
                            HusUiMeddelande.SakerhetskopiaKundeInteLasas -> sakerhetskopiaKundeInteLasasText
                            HusUiMeddelande.SakerhetskopiaNyareVersion -> sakerhetskopiaNyareVersionText
                            is HusUiMeddelande.SakerhetskopiaImporterad -> getString(
                                R.string.sakerhetskopia_importerad,
                                meddelande.tillagda,
                                meddelande.hoppadeOver,
                            )
                            HusUiMeddelande.HistorikPdfSparad -> historikPdfSparadText
                            HusUiMeddelande.HistorikPdfMisslyckades -> historikPdfFelText
                            HusUiMeddelande.HistorikCsvSparad -> historikCsvSparadText
                            HusUiMeddelande.HistorikCsvMisslyckades -> historikCsvFelText
                            HusUiMeddelande.AvlasningCsvSparad -> avlasningCsvSparadText
                            HusUiMeddelande.AvlasningCsvMisslyckades -> avlasningCsvFelText
                            HusUiMeddelande.DokumentForStort -> dokumentForStortText
                            HusUiMeddelande.DokumentOgiltigTyp -> dokumentOgiltigTypText
                            HusUiMeddelande.DokumentKundeInteLaggasTill -> dokumentKundeInteLaggasTillText
                        }
                        snackbarHostState.showSnackbar(text)
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
                        avlasningFormular != null -> {
                            val tillstand = avlasningFormular!!
                            val matareRad = matare.find { it.id == tillstand.matareId }
                            if (matareRad != null) {
                                se.tmconnect.husloggen.ui.AvlasningFormularSkarm(
                                    tillstand = tillstand,
                                    enhet = matareRad.enhet,
                                    idag = idag,
                                    onDatumAndras = viewModel::uppdateraAvlasningDatum,
                                    onVardeAndras = viewModel::uppdateraAvlasningVarde,
                                    onSpara = { viewModel.sparaAvlasningFormular() },
                                    onBekraftaLagreVarde = { viewModel.sparaAvlasningFormular(bekraftatLagreVarde = true) },
                                    onAvbrytLagreVarde = viewModel::avbrytLagreVardeBekraftelse,
                                    onStang = viewModel::stangAvlasningFormular,
                                )
                            } else {
                                LaunchedEffect(tillstand.matareId) {
                                    viewModel.stangAvlasningFormular()
                                }
                            }
                        }
                        planFormular != null -> {
                            val tillstand = planFormular!!
                            se.tmconnect.husloggen.ui.PlanFormularSkarm(
                                tillstand = tillstand,
                                innevarandeAr = idag.year,
                                onTitelAndras = viewModel::uppdateraPlanTitel,
                                onBeloppAndras = viewModel::uppdateraPlanBelopp,
                                onPlaneratArAndras = viewModel::uppdateraPlaneratAr,
                                onAnteckningAndras = viewModel::uppdateraPlanAnteckning,
                                onSpara = viewModel::sparaPlanFormular,
                                onTaBort = viewModel::taBortPlanFormular,
                                onStang = viewModel::stangPlanFormular,
                            )
                        }
                        dokumentFormular != null -> {
                            val tillstand = dokumentFormular!!
                            val locale = resources.configuration.locales[0]
                            se.tmconnect.husloggen.ui.DokumentFormularSkarm(
                                tillstand = tillstand,
                                locale = locale,
                                idag = idag,
                                onTitelAndras = viewModel::uppdateraDokumentTitel,
                                onKategoriAndras = viewModel::uppdateraDokumentKategori,
                                onAnteckningAndras = viewModel::uppdateraDokumentAnteckning,
                                onValjFil = {
                                    valjDokumentContract.launch(
                                        arrayOf("application/pdf", "image/*"),
                                    )
                                },
                                onSpara = viewModel::sparaDokumentFormular,
                                onStang = viewModel::stangDokumentFormular,
                            )
                        }
                        dokumentDetaljId != null -> {
                            val dokumentRad = dokument.find { it.id == dokumentDetaljId }
                            if (dokumentRad != null) {
                                val locale = resources.configuration.locales[0]
                                se.tmconnect.husloggen.ui.DokumentDetaljSkarm(
                                    dokument = dokumentRad,
                                    locale = locale,
                                    onTillbaka = viewModel::stangDokumentDetalj,
                                    onOppnaFil = { oppnaDokument(dokumentRad.mimeTyp, dokumentRad.filnamn) },
                                    onAndra = { viewModel.visaAndraDokument(dokumentRad.id) },
                                    onTaBort = { viewModel.taBortDokument(dokumentRad.id) },
                                )
                            } else {
                                LaunchedEffect(dokumentDetaljId) {
                                    viewModel.stangDokumentDetalj()
                                }
                            }
                        }
                        matareFormular != null -> {
                            se.tmconnect.husloggen.ui.MatareFormularSkarm(
                                tillstand = matareFormular!!,
                                onNamnAndras = viewModel::uppdateraMatareNamn,
                                onEnhetAndras = viewModel::uppdateraMatareEnhet,
                                onAnteckningAndras = viewModel::uppdateraMatareAnteckning,
                                onValjForslag = viewModel::appliceraMatareForslag,
                                onSpara = viewModel::sparaMatareFormular,
                                onStang = viewModel::stangMatareFormular,
                            )
                        }
                        matareDetaljId != null -> {
                            val matareRad = matare.find { it.id == matareDetaljId }
                            if (matareRad != null) {
                                val locale = resources.configuration.locales[0]
                                se.tmconnect.husloggen.ui.MatareDetaljSkarm(
                                    matare = matareRad,
                                    avlasningar = valdMatareAvlasningar,
                                    locale = locale,
                                    idag = idag,
                                    onTillbaka = viewModel::stangMatareDetalj,
                                    onNyAvlasning = { viewModel.visaNyAvlasning(matareRad.id) },
                                    onExportCsv = {
                                        avlasningCsvMatareId = matareRad.id
                                        createAvlasningCsvContract.launch(
                                            avlasningCsvFilnamn(matareRad.namn, idag),
                                        )
                                    },
                                    onTaBortMatare = { viewModel.taBortMatare(matareRad.id) },
                                )
                            } else {
                                LaunchedEffect(matareDetaljId) {
                                    viewModel.stangMatareDetalj()
                                }
                            }
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
                                onRotRutAvdragAndras = viewModel::uppdateraPostRotRutAvdrag,
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
                            InstallningarSkarm(
                                onTillbaka = { visaInstallningar = false },
                                temaLage = temaLage,
                                onTemaLage = viewModel::sattTemaLage,
                                autoBackup = autoBackup,
                                onAutoBackupPa = { pa ->
                                    if (pa) valjMappContract.launch(null) else viewModel.stangAvAutoBackup()
                                },
                                onByttAutoBackupMapp = { valjMappContract.launch(null) },
                                onAutoBackupIntervall = viewModel::sattAutoBackupIntervall,
                                onKorNuAutoBackup = viewModel::korNuAutoBackup,
                                onSparaSakerhetskopia = {
                                    createSakerhetskopiaContract.launch(
                                        sakerhetskopiaFilnamn(LocalDate.now()),
                                    )
                                },
                                onHamtaSakerhetskopia = {
                                    hamtaSakerhetskopiaContract.launch(
                                        arrayOf("application/zip", "application/octet-stream"),
                                    )
                                },
                            )
                        }
                        visaValkommen -> {
                            ValkommenSkarm(
                                onLaggTillForstaUppgift = {
                                    viewModel.avslutaValkommen()
                                    viewModel.visaNyUppgift()
                                },
                                onTittaRunt = viewModel::avslutaValkommen,
                            )
                        }
                        else -> {
                            HusStartSkarm(
                                uppgifter = uppgifter,
                                poster = poster,
                                matare = matare,
                                dokument = dokument,
                                planeradeKostnader = planeradeKostnader,
                                onInstallningar = { visaInstallningar = true },
                                onNyUppgift = viewModel::visaNyUppgift,
                                onNyMatare = viewModel::visaNyMatare,
                                onNyttDokument = viewModel::visaNyttDokument,
                                onNyPlaneradKostnad = viewModel::visaNyPlaneradKostnad,
                                onAndraPlaneradKostnad = viewModel::visaAndraPlaneradKostnad,
                                onGenomfordPlaneradKostnad = viewModel::genomforPlaneradKostnad,
                                onNyPost = {
                                    if (viewModel.kanLaggaTillNyPost()) {
                                        viewModel.visaNyPost()
                                    } else {
                                        viewModel.visaProDialog()
                                    }
                                },
                                onOppnaUppgift = viewModel::visaAndraUppgift,
                                onOppnaPost = viewModel::visaPostDetalj,
                                onOppnaMatare = viewModel::visaMatareDetalj,
                                onOppnaDokument = viewModel::visaDokumentDetalj,
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
                                onVisaProDialog = viewModel::visaProDialog,
                                forsaljningsAr = forsaljningsAr,
                                onForsaljningsArAndras = viewModel::sattForsaljningsAr,
                                onExportHistorikPdf = {
                                    createHistorikPdfContract.launch(husHistorikPdfFilnamn(idag))
                                },
                                onExportHistorikCsv = {
                                    createHistorikCsvContract.launch(husHistorikCsvFilnamn(idag))
                                },
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

    private fun oppnaDokument(mimeTyp: String, filnamn: String) {
        val uri = viewModel.dokumentUri(filnamn)
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeTyp)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun hanteraPaminnelseIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(PaminnelseWorker.EXTRA_OPPNA_ATT_GORA, false) == true) {
            skaOppnaAttGora = true
            intent.removeExtra(PaminnelseWorker.EXTRA_OPPNA_ATT_GORA)
        }
    }

    private fun sparaHistorikPdf(uri: Uri) {
        val locale = resources.configuration.locales[0]
        viewModel.sparaHusHistorikPdf(
            uri = uri,
            texter = historikTexter(locale),
            locale = locale,
            typEtikett = { typ -> getString(typTillResId(typ)) },
            forbattringEtikett = { mojlighet -> getString(forbattringTillResId(mojlighet)) },
            datumEtikett = { post -> formateraDatum(post.datum, locale) },
            beloppEtikett = { post -> formateraPrisKr(postBeloppForSammanstallningOre(post), locale) },
        )
    }

    private fun sparaHistorikCsv(uri: Uri) {
        val locale = resources.configuration.locales[0]
        viewModel.sparaHusHistorikCsv(
            uri = uri,
            texter = historikTexter(locale),
            locale = locale,
            typEtikett = { typ -> getString(typTillResId(typ)) },
            forbattringEtikett = { mojlighet -> getString(forbattringTillResId(mojlighet)) },
            datumEtikett = { post -> formateraDatum(post.datum, locale) },
            beloppEtikett = { post -> formateraPrisKr(postBeloppForSammanstallningOre(post), locale) },
        )
    }

    private fun avlasningCsvTexter(): AvlasningCsvTexter =
        AvlasningCsvTexter(
            kolumnDatum = getString(R.string.falt_datum),
            kolumnVarde = getString(R.string.falt_avlasning_varde),
            kolumnEnhet = getString(R.string.falt_enhet),
            kolumnForbrukning = getString(R.string.falt_forbrukning),
            kolumnEnhetKort = getString(R.string.falt_enhet),
        )

    private fun historikTexter(@Suppress("UNUSED_PARAMETER") locale: Locale): HusHistorikTexter =
        HusHistorikTexter(
            rubrik = getString(R.string.historik_pdf_rubrik),
            kolumnDatum = getString(R.string.falt_datum),
            kolumnBeskrivning = getString(R.string.falt_vad),
            kolumnTyp = getString(R.string.falt_typ),
            kolumnBelopp = getString(R.string.falt_belopp),
            kolumnForbattring = getString(R.string.sammanstallning_kolumn_forbattring),
            arSummaRubrik = { ar -> getString(R.string.sammanstallning_ar_summa, ar.toString()) },
            typSummaRad = { typ, belopp -> getString(R.string.sammanstallning_typ_summa, typ, belopp) },
            totalt = getString(R.string.sammanstallning_totalt),
            gransJa = getString(R.string.sammanstallning_grans_ar),
            gransNej = getString(R.string.sammanstallning_grans_ar_ej),
            skattRubrik = getString(R.string.historik_pdf_skatt_rubrik),
            skattText = getString(R.string.historik_pdf_skatt_text),
            kallorRubrik = getString(R.string.sammanstallning_skatte_info_rubrik),
            kallorText = skatteKallorText(),
        )

    private fun typTillResId(typ: se.tmconnect.husloggen.data.PostTyp): Int =
        when (typ) {
            se.tmconnect.husloggen.data.PostTyp.UNDERHALL -> R.string.post_typ_underhall
            se.tmconnect.husloggen.data.PostTyp.REPARATION -> R.string.post_typ_reparation
            se.tmconnect.husloggen.data.PostTyp.OMBYGGNAD -> R.string.post_typ_ombyggnad
        }

    private fun forbattringTillResId(mojlighet: ForbattringMojlighet): Int =
        when (mojlighet) {
            ForbattringMojlighet.Ja -> R.string.sammanstallning_forbattring_ja
            ForbattringMojlighet.KravBedomning -> R.string.sammanstallning_forbattring_bedomning
            ForbattringMojlighet.Nej -> R.string.sammanstallning_forbattring_nej
        }
}
