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
import androidx.core.os.BundleCompat
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
import se.tmconnect.husloggen.export.KommandeUnderhallCsvTexter
import se.tmconnect.husloggen.export.avlasningCsvFilnamn
import se.tmconnect.husloggen.export.husHistorikCsvFilnamn
import se.tmconnect.husloggen.export.husHistorikPdfFilnamn
import se.tmconnect.husloggen.export.kommandeUnderhallCsvFilnamn
import se.tmconnect.husloggen.regler.kommandeUnderhall
import se.tmconnect.husloggen.ui.KommandeArSkarm
import se.tmconnect.husloggen.export.skatteKallorText
import se.tmconnect.husloggen.export.sakerhetskopiaFilnamn
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.kommandePerManad
import se.tmconnect.husloggen.regler.postBeloppForSammanstallningOre
import se.tmconnect.husloggen.regler.senasteKostnadPerUppgift
import se.tmconnect.husloggen.regler.uppskattadKostnadKommande
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

    private val createKommandeUnderhallCsvContract = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri != null) {
            sparaKommandeUnderhallCsv(uri)
        }
    }

    private var behorighetNekad by mutableStateOf(false)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Kameran och filväljaren öppnas i en annan app; under tiden kan aktiviteten skapas om
        // (rotation, systemet frigör minne). Utan detta tappas resultatet när de kommer tillbaka.
        kameraUri?.let { outState.putParcelable(SPARAT_KAMERA_URI, it) }
        avlasningCsvMatareId?.let { outState.putLong(SPARAT_AVLASNING_CSV_MATARE, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            kameraUri = BundleCompat.getParcelable(savedInstanceState, SPARAT_KAMERA_URI, Uri::class.java)
            if (savedInstanceState.containsKey(SPARAT_AVLASNING_CSV_MATARE)) {
                avlasningCsvMatareId = savedInstanceState.getLong(SPARAT_AVLASNING_CSV_MATARE)
            }
        }
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
                var visaKommandeAr by rememberSaveable { mutableStateOf(false) }
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
                val bostader by viewModel.bostader.collectAsState()
                val valdBostad by viewModel.valdBostad.collectAsState()
                val valdBostadId by viewModel.valdBostadId.collectAsState()
                val autoBackup by viewModel.autoBackup.collectAsState()
                val forsaljningsAr by viewModel.forsaljningsAr.collectAsState()
                val markeradGjordText = stringResource(R.string.uppgift_markerad_gjord)
                val uppgiftGjordPostEjSparadText = stringResource(R.string.uppgift_gjord_post_ej_sparad)
                val sakerhetskopiaSparadText = stringResource(R.string.sakerhetskopia_sparad)
                val sakerhetskopiaKundeInteLasasText = stringResource(R.string.sakerhetskopia_kunde_inte_lasas)
                val sakerhetskopiaKundeInteSparasText = stringResource(R.string.sakerhetskopia_kunde_inte_sparas)
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
                val senasteKostnadPerUppgiftMap = remember(poster) { senasteKostnadPerUppgift(poster) }
                val kommandeKostnad = remember(uppgifter, senasteKostnadPerUppgiftMap, idag) {
                    uppskattadKostnadKommande(
                        kommandePerManad(uppgifter, idag),
                        senasteKostnadPerUppgiftMap,
                    )
                }
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(viewModel) {
                    viewModel.meddelanden.collect { meddelande ->
                        val text = when (meddelande) {
                            HusUiMeddelande.UppgiftMarkeradGjord -> markeradGjordText
                            HusUiMeddelande.UppgiftMarkeradGjordPostEjSparad -> uppgiftGjordPostEjSparadText
                            HusUiMeddelande.SakerhetskopiaSparad -> sakerhetskopiaSparadText
                            HusUiMeddelande.SakerhetskopiaKundeInteLasas -> sakerhetskopiaKundeInteLasasText
                            HusUiMeddelande.SakerhetskopiaKundeInteSparas -> sakerhetskopiaKundeInteSparasText
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
                                onPrisOrePerEnhetAndras = viewModel::uppdateraMatarePrisOrePerEnhet,
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
                                    onTillbaka = { matareDetaljId = null },
                                    onTaBort = { viewModel.taBortMatare(matareRad.id) },
                                )
                            } else {
                                LaunchedEffect(matareDetaljId) {
                                    matareDetaljId = null
                                }
                            }
                        }
                        formular != null -> {
                            val tillstand = formular!!
                            se.tmconnect.husloggen.ui.UppgiftFormularSkarm(
                                tillstand = tillstand,
                                idag = idag,
                                onSpara = { viewModel.sparaUppgift(tillstand) },
                                onStang = { viewModel.stangUppgiftFormular() },
                            )
                        }
                        postFormular != null -> {
                            val tillstand = postFormular!!
                            se.tmconnect.husloggen.ui.PostFormularSkarm(
                                tillstand = tillstand,
                                idag = idag,
                                onSpara = { viewModel.sparaPost(tillstand) },
                                onStang = { viewModel.stangPostFormular() },
                            )
                        }
                        postDetaljId != null -> {
                            val post = poster.firstOrNull { it.id == postDetaljId }
                            if (post != null) {
                                PostDetaljSkarm(
                                    post = post,
                                    locale = resources.configuration.locales[0],
                                    onTillbaka = { viewModel.stangPostDetalj() },
                                    onTaBort = { viewModel.taBortPost(post.id) },
                                )
                            } else {
                                LaunchedEffect(postDetaljId) {
                                    viewModel.stangPostDetalj()
                                }
                            }
                        }
                        visaInstallningar -> {
                            InstallningarSkarm(
                                onTillbaka = { visaInstallningar = false },
                                bostader = bostader,
                                valdBostadId = valdBostadId,
                                onValjBostad = viewModel::valjBostad,
                                onSparaNyBostad = viewModel::sparaNyBostad,
                                onUppdateraBostad = viewModel::uppdateraBostad,
                                onTaBortBostad = viewModel::taBortBostad,
                                hamtaRaderingInfo = viewModel::hamtaRaderingInfo,
                                temaLage = viewModel.temaLage.value,
                                onTemaLage = viewModel::sattTemaLage,
                                autoBackup = viewModel.autoBackup.value,
                                onAutoBackupPa = viewModel::sattAutoBackupPa,
                                onByttAutoBackupMapp = viewModel::byttAutoBackupMapp,
                                onAutoBackupIntervall = viewModel::sattAutoBackupIntervall,
                                onKorNuAutoBackup = viewModel::korNuAutoBackup,
                                onSparaSakerhetskopia = viewModel::sparaSakerhetskopia,
                                onHamtaSakerhetskopia = viewModel::hamtaSakerhetskopia,
                            )
                        }
                        visaKommandeAr -> {
                            KommandeArSkarm(
                                uppgifter = uppgifter,
                                kommandeKostnad = kommandeKostnad,
                                locale = resources.configuration.locales[0],
                                onTillbaka = { visaKommandeAr = false },
                                onOppnaUppgift = viewModel::oppnaUppgift,
                            )
                        }
                        visaValkommen -> {
                            ValkommenSkarm(
                                onSpara = viewModel::sparaValkommen,
                                onStang = { visaValkommen = false },
                            )
                        }
                        else -> {
                            HusStartSkarm(
                                uppgifter = uppgifter,
                                poster = poster,
                                matare = matare,
                                dokument = dokument,
                                planeradeKostnader = planeradeKostnader,
                                bostader = bostader,
                                valdBostad = valdBostad,
                                onValjBostad = viewModel::valjBostad,
                                onInstallningar = { visaInstallningar = true },
                                onNyUppgift = viewModel::nyUppgift,
                                onNyPost = viewModel::nyPost,
                                onNyMatare = viewModel::nyMatare,
                                onNyttDokument = viewModel::nyttDokument,
                                onNyPlaneradKostnad = viewModel::nyPlaneradKostnad,
                                onAndraPlaneradKostnad = viewModel::andraPlaneradKostnad,
                                onGenomfordPlaneradKostnad = viewModel::genomfordPlaneradKostnad,
                                onOppnaUppgift = viewModel::oppnaUppgift,
                                onOppnaPost = viewModel::oppnaPost,
                                onOppnaMatare = viewModel::oppnaMatare,
                                onOppnaDokument = viewModel::oppnaDokument,
                                onMarkeraGjord = viewModel::markeraGjord,
                                oppnaAttGora = false,
                                paminnelserPa = viewModel.paminnelserPa.value,
                                onPaminnelserAndras = viewModel::sattPaminnelserPa,
                                behorighetNekad = false,
                                arPro = viewModel.arPro.value,
                                onVisaProDialog = { /* Inte implementerad i denna version */ },
                                forsaljningsAr = viewModel.forsaljningsAr.value,
                                onForsaljningsArAndras = viewModel::sattForsaljningsAr,
                                onExportHistorikPdf = viewModel::exporteraHistorikPdf,
                                onExportHistorikCsv = viewModel::exporteraHistorikCsv,
                                onVisaKommandeAr = { visaKommandeAr = true },
                                senasteKostnadPerUppgift = viewModel.senasteKostnadPerUppgift.value,
                                kommandeKostnad = kommandeKostnad,
                                startFlik = HusFlik.SAMMANSTALLNING,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun sparaHistorikPdf(uri: Uri) {
        // Implementation för PDF-export
    }

    private fun sparaHistorikCsv(uri: Uri) {
        // Implementation för CSV-export
    }

    private fun sparaKommandeUnderhallCsv(uri: Uri) {
        // Implementation för CSV-export av kommande underhåll
    }

    private fun oppnaDokument(dokumentId: Long) {
        // Implementation för att öppna dokument
    }

    private fun hamtaRaderingInfo(bostadId: Long): Int {
        // Implementation för att hämta raderingsinformation
        return 0
    }

    private fun hanteraPaminnelseIntent(intent: Intent) {
        // Implementation för att hantera påminnelser
    }

    companion object {
        private const val SPARAT_KAMERA_URI = "sparat_kamera_uri"
        private const val SPARAT_AVLASNING_CSV_MATARE = "sparat_avlasning_csv_matare"
    }
}
