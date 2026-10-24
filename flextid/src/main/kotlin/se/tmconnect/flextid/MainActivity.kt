package se.tmconnect.flextid

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import java.util.Locale
import se.tmconnect.flextid.data.UiInstallningarLager
import se.tmconnect.flextid.data.skapaFlextidRepository
import se.tmconnect.flextid.export.ManadsCsvTexter
import se.tmconnect.flextid.export.manadsCsvFilnamn
import se.tmconnect.flextid.export.manadsPdfFilnamn
import se.tmconnect.flextid.export.manadsRapportTexter
import se.tmconnect.flextid.ui.helgdagNamnRes
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.export.normaltidManadEtikett
import se.tmconnect.flextid.export.skrivManadsPdf
import se.tmconnect.flextid.paminnelse.FlextidPaminnelseInstallningarLager
import se.tmconnect.flextid.paminnelse.PaminnelseWorker
import se.tmconnect.flextid.pro.ProKallaSkapare
import se.tmconnect.flextid.ui.FlextidFlik
import se.tmconnect.flextid.ui.FlextidMeddelande
import se.tmconnect.flextid.ui.FlextidStartSkarm
import se.tmconnect.flextid.ui.FlextidViewModel
import se.tmconnect.flextid.ui.FlextidViewModelFactory
import se.tmconnect.flextid.ui.ProDialog
import se.tmconnect.flextid.ui.ValkommenSkarm
import se.tmconnect.karna.export.skrivCsvMedBom
import se.tmconnect.karna.tema.AppfabrikTema
class MainActivity : ComponentActivity() {
    private var oppnaIdagVidStart = false
    private var vantandeGenvag by mutableStateOf<VantandeGenvag?>(null)
    private var behorighetNekad by mutableStateOf(false)
    private var onPaminnelseGodkand: (() -> Unit)? = null

    private val uiInstallningarLager by lazy { UiInstallningarLager(applicationContext) }

    private val paminnelseInstallningar by lazy {
        FlextidPaminnelseInstallningarLager(applicationContext)
    }

    private val proKalla by lazy {
        ProKallaSkapare.skapa(
            applicationContext,
            ProKonfig.PRODUKT_ID,
            lifecycleScope,
        )
    }

    private val viewModel: FlextidViewModel by viewModels {
        FlextidViewModelFactory(
            skapaFlextidRepository(applicationContext),
            uiInstallningarLager,
            paminnelseInstallningar,
            proKalla,
            applicationContext,
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                behorighetNekad = false
                onPaminnelseGodkand?.invoke()
                onPaminnelseGodkand = null
            } else {
                behorighetNekad = true
                onPaminnelseGodkand = null
            }
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
                                    getString(R.string.csv_helgdag),
                                ),
                            typEtiketter =
                                mapOf(
                                    DagTyp.ARBETE to getString(R.string.dagtyp_arbete),
                                    DagTyp.FLEX to getString(R.string.dagtyp_flex),
                                    DagTyp.LEDIG to getString(R.string.dagtyp_ledig),
                                    DagTyp.SEMESTER to getString(R.string.dagtyp_semester),
                                    DagTyp.SJUK to getString(R.string.dagtyp_sjuk),
                                    DagTyp.HELGDAG to getString(R.string.dagtyp_helgdag),
                                ),
                            helgdagNamn = { h -> helgdagNamnRes(h.namn)?.let { getString(it) } ?: h.namn },
                        )
                    val csv = viewModel.skapaManadsCsv(texter, locale)
                    skrivCsvMedBom(out, csv)
                }
            }
        }

    private val skapaArsCsvContract =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri != null) {
                viewModel.skrivTillUri(uri) { out ->
                    val rubriker = listOf(
                        getString(R.string.csv_manad),
                        getString(R.string.csv_arbetad),
                        getString(R.string.csv_utfall),
                        getString(R.string.csv_saldo),
                    )
                    skrivCsvMedBom(out, viewModel.skapaArsCsv(rubriker, Locale.getDefault()))
                }
            }
        }

    private val skapaPdfContract =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            if (uri != null) {
                viewModel.skrivTillUri(uri) { out ->
                    val locale = Locale.getDefault()
                    val arManad = viewModel.exportArManad.value
                    val texter = manadsRapportTexter(this, arManad, locale)
                    val rapport = viewModel.skapaManadsRapport(texter, locale)
                    val texterMedNormaltid =
                        texter.copy(normaltidManadEtikett = normaltidManadEtikett(this, rapport.arbetsdagarManad))
                    skrivManadsPdf(rapport, texterMedNormaltid, out)
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

    private val valjMappContract =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
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
            val dokument =
                android.provider.DocumentsContract.buildDocumentUriUsingTree(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hanteraPaminnelseIntent(intent)
        registreraGenvagIntent(intent)
        enableEdgeToEdge()
        setContent {
            val temaLage by viewModel.temaLage.collectAsStateWithLifecycle()
            val visaValkommen by viewModel.visaValkommen.collectAsStateWithLifecycle()
            val morkt = temaLage.arMorkt(isSystemInDarkTheme())
            DisposableEffect(morkt) {
                enableEdgeToEdge(
                    statusBarStyle =
                        SystemBarStyle.auto(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ) { morkt },
                    navigationBarStyle =
                        SystemBarStyle.auto(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ) { morkt },
                )
                onDispose {}
            }
            val snackbarHostState = remember { SnackbarHostState() }
            val proDialogSynlig by viewModel.proDialogSynlig.collectAsStateWithLifecycle()
            val startFlik =
                if (oppnaIdagVidStart) {
                    oppnaIdagVidStart = false
                    FlextidFlik.IDAG
                } else {
                    null
                }

            val snackbarLocale = Locale.getDefault()
            LaunchedEffect(vantandeGenvag) {
                when (vantandeGenvag) {
                    VantandeGenvag.STAMPLA_IN -> viewModel.genvagStamplaIn()
                    VantandeGenvag.STAMPLA_UT -> viewModel.genvagStamplaUt()
                    null -> Unit
                }
                vantandeGenvag = null
            }
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
                            FlextidMeddelande.GransUtanPro -> getString(R.string.pro_grans_nadd)
                            is FlextidMeddelande.RedanInstamplad ->
                                getString(
                                    R.string.genvag_redan_instamplad,
                                    se.tmconnect.flextid.regler.formateraTid(meddelande.start, snackbarLocale),
                                )
                            FlextidMeddelande.IngenInstempling -> getString(R.string.genvag_ingen_instempling)
                            FlextidMeddelande.SommartidOgiltig -> getString(R.string.sommartid_ogiltig)
                            FlextidMeddelande.ArbetsdagarSaknas -> getString(R.string.arbetsdagar_saknas)
                        }
                    snackbarHostState.showSnackbar(text)
                }
            }

            AppfabrikTema(morkt = morkt) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (visaValkommen) {
                        ValkommenSkarm(
                            onStamplaIn = {
                                viewModel.avslutaValkommen()
                                viewModel.stamplaIn()
                            },
                            onTittaRunt = viewModel::avslutaValkommen,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        FlextidStartSkarm(
                            viewModel = viewModel,
                            snackbarHostState = snackbarHostState,
                            modifier = Modifier.fillMaxSize(),
                            startFlik = startFlik,
                            behorighetNekad = behorighetNekad,
                            onStamplaUtPaminnelse = { pa -> andraStamplaUtPaminnelse(pa) },
                            onStamplaInPaminnelse = { pa -> andraStamplaInPaminnelse(pa) },
                            onExporteraCsv = {
                                val arManad = viewModel.exportArManad.value
                                skapaCsvContract.launch(manadsCsvFilnamn(arManad))
                            },
                            onExporteraArsCsv = {
                                skapaArsCsvContract.launch("flextid-ar-${viewModel.exportArManad.value.year}.csv")
                            },
                            onExporteraPdf = {
                                val arManad = viewModel.exportArManad.value
                                skapaPdfContract.launch(manadsPdfFilnamn(arManad))
                            },
                            onExporteraSakerhetskopia = {
                                skapaJsonContract.launch("flextid-sakerhetskopia.json")
                            },
                            onImporteraSakerhetskopia = {
                                oppnaJsonContract.launch(arrayOf("application/json", "text/plain", "*/*"))
                            },
                            onAutoBackupPa = { pa ->
                                if (pa) valjMappContract.launch(null) else viewModel.stangAvAutoBackup()
                            },
                            onByttAutoBackupMapp = { valjMappContract.launch(null) },
                            onAutoBackupIntervall = viewModel::sattAutoBackupIntervall,
                            onKorNuAutoBackup = viewModel::korNuAutoBackup,
                            locale = Locale.getDefault(),
                        )
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
        viewModel.synkaPaminnelseJobb()
        viewModel.synkaAutoBackupJobb()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        hanteraPaminnelseIntent(intent)
        registreraGenvagIntent(intent)
    }

    private enum class VantandeGenvag {
        STAMPLA_IN,
        STAMPLA_UT,
    }

    private fun registreraGenvagIntent(intent: Intent?) {
        if (!GenvagIntent.arGenvag(intent)) return
        oppnaIdagVidStart = true
        viewModel.avslutaValkommen()
        vantandeGenvag =
            when {
                GenvagIntent.arStamplaIn(intent) -> VantandeGenvag.STAMPLA_IN
                GenvagIntent.arStamplaUt(intent) -> VantandeGenvag.STAMPLA_UT
                else -> return
            }
        intent?.action = Intent.ACTION_MAIN
    }

    private fun hanteraPaminnelseIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(PaminnelseWorker.EXTRA_OPPNA_IDAG, false) == true) {
            oppnaIdagVidStart = true
            intent.removeExtra(PaminnelseWorker.EXTRA_OPPNA_IDAG)
        }
    }

    private fun andraStamplaUtPaminnelse(pa: Boolean) {
        if (!pa) {
            behorighetNekad = false
            viewModel.sattStamplaUtPaminnelse(false)
            return
        }
        begarNotisBehorighetOmBehovs { viewModel.sattStamplaUtPaminnelse(true) }
    }

    private fun andraStamplaInPaminnelse(pa: Boolean) {
        if (!pa) {
            behorighetNekad = false
            viewModel.sattStamplaInPaminnelse(false)
            return
        }
        begarNotisBehorighetOmBehovs { viewModel.sattStamplaInPaminnelse(true) }
    }

    private fun begarNotisBehorighetOmBehovs(onGodkand: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !harNotisBehorighet()
        ) {
            onPaminnelseGodkand = onGodkand
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onGodkand()
        }
    }

    private fun harNotisBehorighet(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
