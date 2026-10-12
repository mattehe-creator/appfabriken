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
import se.tmconnect.garantivalvet.ocr.KvittoLasare
import se.tmconnect.garantivalvet.ocr.MlKitKvittoLasare
import se.tmconnect.garantivalvet.regler.ButikKontakt
import se.tmconnect.garantivalvet.regler.KvittoForslag
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.garantivalvet.regler.arDelbartKvitto
import se.tmconnect.garantivalvet.regler.giltigaOppetKopDagar
import se.tmconnect.garantivalvet.regler.tolkaKvitto
import se.tmconnect.garantivalvet.ui.varutypText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import se.tmconnect.garantivalvet.pro.ProKallaSkapare
import se.tmconnect.garantivalvet.regler.UnderlagEtiketter
import se.tmconnect.garantivalvet.regler.byggUnderlag
import se.tmconnect.garantivalvet.ui.ExportPdfMeddelande
import se.tmconnect.garantivalvet.ui.GarantivalvetDebugInstallningar
import se.tmconnect.garantivalvet.ui.InstallningarSkarm
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.KopSkarm
import se.tmconnect.garantivalvet.ui.KopViewModel
import se.tmconnect.garantivalvet.ui.KopViewModelFactory
import se.tmconnect.garantivalvet.ui.ProDialog
import se.tmconnect.garantivalvet.ui.ReklamationsSkarm
import se.tmconnect.garantivalvet.ui.SakerhetskopiaMeddelande
import se.tmconnect.garantivalvet.ui.KvittoHelskarm
import se.tmconnect.karna.pro.ProKalla
import se.tmconnect.karna.tema.AppfabrikTema

class MainActivity : ComponentActivity() {
    private val installningarLager: InstallningarLager by lazy {
        InstallningarLager(applicationContext)
    }

    private val kvittoLasare: KvittoLasare by lazy { MlKitKvittoLasare(applicationContext) }

    private val lasKvitto: suspend (Uri) -> KvittoForslag? = { uri ->
        kvittoLasare.lasText(uri)?.let { tolkaKvitto(it, LocalDate.now()) }?.takeUnless { it.arTomt }
    }

    private val proKalla: ProKalla by lazy {
        ProKallaSkapare.skapa(
            applicationContext,
            ProKonfig.PRODUKT_ID,
            lifecycleScope,
            installningarLager,
        )
    }

    private val viewModel: KopViewModel by viewModels {
        val databas = GarantiDatabas.hamta(applicationContext)
        val kvittoLager = KvittoLager(applicationContext)
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
            viewModel.valjBild(it)
        }
    }

    // Resultatkontrakt för kamera
    private var kameraUri: Uri? = null
    private val takePictureContract = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && kameraUri != null) {
            viewModel.valjBild(kameraUri!!)
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
                    val etiketter = underlagEtiketter()
                    
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
        if (savedInstanceState == null) {
            hanteraDelatKvitto(intent)
        }
        viewModel.synkaPaminnelseJobb()
        enableEdgeToEdge()
        setContent {
            AppfabrikTema {
                val kopLista by viewModel.kopLista.collectAsState()
                // Måste läsas här: annars ritas formuläret inte om när ett kvitto fotas eller väljs.
                val tillfalligtKvitto by viewModel.tillfalligtKvittoUri.collectAsState()
                val delatKvitto by viewModel.delatKvitto.collectAsState()
                val aktivSkarm by viewModel.aktivSkarm.collectAsState()
                val valtKop by viewModel.valtKop.collectAsState()
                val paminnelserPa by viewModel.paminnelserPa.collectAsState()
                val arPro by viewModel.arPro.collectAsState()
                val proDialogSynlig by viewModel.proDialogSynlig.collectAsState()
                val exportPdfMeddelande by viewModel.exportPdfMeddelande.collectAsState()
                val sakerhetskopiaMeddelande by viewModel.sakerhetskopiaMeddelande.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                exportPdfMeddelande?.let { meddelande ->
                    val text = when (meddelande) {
                        ExportPdfMeddelande.Sparad -> stringResource(R.string.export_pdf_sparad)
                        ExportPdfMeddelande.Misslyckades -> stringResource(R.string.export_pdf_misslyckades)
                    }
                    val delaLabel = stringResource(R.string.export_dela)
                    LaunchedEffect(meddelande) {
                        val result = snackbarHostState.showSnackbar(
                            message = text,
                            actionLabel = if (meddelande == ExportPdfMeddelande.Sparad) delaLabel else null,
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            exportKop?.let { delaPdfForKop(it) }
                        }
                        viewModel.rensaExportMeddelande()
                    }
                }

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
                            kvittoUriFor = { kop -> viewModel.sparatKvittoUri(kop) },
                            onLaggTill = {
                                if (viewModel.kanLaggaTillNy()) {
                                    viewModel.visaLaggTill()
                                } else {
                                    viewModel.visaProDialog()
                                }
                            },
                            onOppna = viewModel::visaDetalj,
                            onOppnaInstallningar = viewModel::visaInstallningar,
                            arPro = arPro,
                            onVisaProDialog = viewModel::visaProDialog,
                        )
                    }

                    KopSkarm.Installningar -> {
                        InstallningarSkarm(
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
                            // Säkerhetskopia och PDF är gratis för alla; Pro tar bara bort gränsen på 15 köp.
                            onSparaSakerhetskopia = {
                                createSakerhetskopiaContract.launch(
                                    sakerhetskopiaFilnamn(LocalDate.now()),
                                )
                            },
                            onHamtaSakerhetskopia = {
                                openSakerhetskopiaContract.launch(
                                    arrayOf("application/zip", "application/octet-stream"),
                                )
                            },
                            onTillbaka = viewModel::visaLista,
                            debugInstallningar = {
                                GarantivalvetDebugInstallningar(
                                    arPro = arPro,
                                    onTestlageProAndras = viewModel::sattDebugProTestlagePa,
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
                                onVisaProDialog = viewModel::visaProDialog,
                                onReklamera = { viewModel.visaReklamera(kop.id) },
                                onMarkeraReklamerad = viewModel::markeraReklamerad,
                                onMejlaButik = { epost -> mejlaButik(epost) },
                                onRingButik = { telefon -> ringButik(telefon) },
                            )
                        }
                    }

                    is KopSkarm.Reklamera -> {
                        val kop = valtKop
                        if (kop != null) {
                            ReklamationsSkarm(
                                kop = kop,
                                kvittoFinns = kop.kvittoFil != null,
                                onSkickaMejl = { amne, text -> skickaReklamationsMejl(kop, amne, text) },
                                onSparaFel = viewModel::sparaFel,
                                onHarReklamerat = { upptackt, beskrivning ->
                                    viewModel.harReklamerat(upptackt, beskrivning)
                                },
                                onTillbaka = { viewModel.visaDetalj(kop.id) },
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
                            initialOppetKop = null,
                            initialVarutyp = null,
                            lasInitialtKvitto = delatKvitto,
                            onRotera = viewModel::roteraKvitto,
                            kvittoUri = tillfalligtKvitto ?: viewModel.kvittoUriForFormular(null),
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
                            lasKvitto = lasKvitto,
                            onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning, oppetKopDagar, varutyp, kontakt ->
                                viewModel.sparaNyttKop(
                                    vad = vad,
                                    varKopt = varKopt,
                                    kopdatum = kopdatum,
                                    garantiManader = garantiManader,
                                    prisOre = prisOre,
                                    anteckning = anteckning,
                                    oppetKopDagar = oppetKopDagar,
                                    varutyp = varutyp,
                                    kontakt = kontakt,
                                )
                            },
                            onStang = viewModel::visaLista,
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
                                initialOppetKop = kop.oppetKopDagar,
                                initialVarutyp = kop.varutyp,
                                initialKontakt = ButikKontakt(kop.butikEpost, kop.butikTelefon, kop.butikOrgnr),
                                onRotera = viewModel::roteraKvitto,
                                kvittoUri = tillfalligtKvitto ?: viewModel.kvittoUriForFormular(kop),
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
                                lasKvitto = lasKvitto,
                                onSpara = { vad, varKopt, kopdatum, garantiManader, prisOre, anteckning, oppetKopDagar, varutyp, kontakt ->
                                    viewModel.uppdateraKop(
                                        kop.copy(
                                            vad = vad.trim(),
                                            varKopt = varKopt?.trim()?.takeIf { it.isNotEmpty() },
                                            kopdatum = kopdatum,
                                            garantiManader = garantiManader,
                                            prisOre = prisOre,
                                            anteckning = anteckning?.trim()?.takeIf { it.isNotEmpty() },
                                            oppetKopDagar = giltigaOppetKopDagar(oppetKopDagar),
                                            varutyp = Varutyp.franNamn(varutyp)?.name,
                                            butikEpost = kontakt.epost?.trim()?.takeIf { it.isNotEmpty() },
                                            butikTelefon = kontakt.telefon?.trim()?.takeIf { it.isNotEmpty() },
                                            butikOrgnr = kontakt.orgnr?.trim()?.takeIf { it.isNotEmpty() },
                                        ),
                                    )
                                },
                                onStang = viewModel::visaLista,
                            )
                        }
                    }
                }

                    if (proDialogSynlig) {
                        ProDialog(
                            onDismiss = viewModel::stangProDialog,
                            onKopPro = { activity -> proKalla.kop(activity) },
                        )
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
        hanteraDelatKvitto(intent)
    }

    private fun underlagEtiketter() = UnderlagEtiketter(
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
        oppetKop = getString(R.string.underlag_oppet_kop),
        oppetKopSlut = getString(R.string.underlag_oppet_kop_slut),
        varutyp = getString(R.string.underlag_varutyp),
        farskvara = getString(R.string.underlag_farskvara),
        farskvaraText = getString(R.string.underlag_farskvara_text),
        butikEpost = getString(R.string.underlag_butik_epost),
        butikTelefon = getString(R.string.underlag_butik_telefon),
        butikOrgnr = getString(R.string.underlag_butik_orgnr),
        varutyper = Varutyp.entries.associate { it.name to getString(varutypText(it)) },
    )

    /** Öppnar mejlappen med färdigt meddelande och kvittot bifogat. Ingen behörighet eller nätverk i appen. */
    private fun skickaReklamationsMejl(kop: Kop, amne: String, text: String) {
        val kvittoUri = viewModel.sparatKvittoUri(kop)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            kop.butikEpost?.takeIf { it.isNotBlank() }?.let { putExtra(Intent.EXTRA_EMAIL, arrayOf(it)) }
            putExtra(Intent.EXTRA_SUBJECT, amne)
            putExtra(Intent.EXTRA_TEXT, text)
            if (kvittoUri != null) {
                putExtra(Intent.EXTRA_STREAM, kvittoUri)
                clipData = android.content.ClipData.newRawUri("", kvittoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.reklamera_skicka_mejl)))
        } catch (_: android.content.ActivityNotFoundException) {
            android.widget.Toast.makeText(this, R.string.reklamera_inget_mejl, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun mejlaButik(epost: String) {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(epost))))
        } catch (_: android.content.ActivityNotFoundException) {
            android.widget.Toast.makeText(this, R.string.reklamera_inget_mejl, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    /** Öppnar uppringaren med numret ifyllt; användaren trycker själv på ring. Ingen behörighet behövs. */
    private fun ringButik(telefon: String) {
        val nummer = telefon.filter { it.isDigit() || it == '+' }
        try {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$nummer")))
        } catch (_: android.content.ActivityNotFoundException) {
        }
    }

    private fun delaPdfForKop(kop: Kop) {
        try {
            val etiketter = underlagEtiketter()
            val dokumentRubrik = getString(R.string.underlag_rubrik)
            val locale = resources.configuration.locales[0]
            val underlag = byggUnderlag(kop, LocalDate.now(), etiketter, dokumentRubrik, locale)
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
                        this,
                        KvittoLager.fileProviderAuthority(this),
                        pdfFil,
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(Intent.createChooser(intent, getString(R.string.export_dela)))
                }
            }
        } catch (_: Exception) {
        }
    }

    /** Tar emot ett kvitto (bild eller PDF) som delats från en annan app, till exempel mejl eller galleri. */
    private fun hanteraDelatKvitto(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND || !arDelbartKvitto(intent.type)) return
        val uri = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        } ?: return
        intent.removeExtra(Intent.EXTRA_STREAM)
        viewModel.tagEmotDelatKvitto(uri, intent.type) {
            android.widget.Toast.makeText(this, R.string.dela_in_fel, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun hanteraPaminnelseIntent(intent: Intent?) {
        val kopId = intent?.getLongExtra(PaminnelseWorker.EXTRA_KOP_ID, -1L) ?: return
        if (kopId >= 0L) {
            viewModel.visaDetalj(kopId)
            intent.removeExtra(PaminnelseWorker.EXTRA_KOP_ID)
        }
    }
}
