package se.tmconnect.flextid.butik

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.captureScreenRoboImage
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.flextid.R
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.ui.DagFormularTillstand
import se.tmconnect.flextid.ui.FlextidFlik
import se.tmconnect.flextid.ui.IdagInnehall
import se.tmconnect.flextid.ui.InstallningarInnehall
import se.tmconnect.flextid.ui.ProDialog
import se.tmconnect.flextid.ui.VeckaDagRad
import se.tmconnect.flextid.ui.VeckaInnehall
import se.tmconnect.flextid.ui.DagFormularDialog
import se.tmconnect.karna.tema.AppfabrikFarger
import se.tmconnect.karna.tema.AppfabrikTema
import se.tmconnect.karna.tema.TemaLage
import java.time.YearMonth

private val noOp: () -> Unit = {}
private val idag = LocalDate.of(2026, 10, 15)
private val veckaAnkar = LocalDate.of(2026, 10, 13)

class ButikRoboActivity : ComponentActivity()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlextidButikRam(
    flik: FlextidFlik,
    locale: Locale,
    innehall: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = flik == FlextidFlik.IDAG,
                    onClick = {},
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = stringResource(R.string.flik_idag))
                    },
                    label = { Text(stringResource(R.string.flik_idag)) },
                )
                NavigationBarItem(
                    selected = flik == FlextidFlik.VECKA,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.flik_vecka),
                        )
                    },
                    label = { Text(stringResource(R.string.flik_vecka)) },
                )
                NavigationBarItem(
                    selected = flik == FlextidFlik.INSTALLNINGAR,
                    onClick = {},
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.flik_installningar))
                    },
                    label = { Text(stringResource(R.string.flik_installningar)) },
                )
            }
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
        ) {
            innehall()
        }
    }
}

@OptIn(ExperimentalRoborazziApi::class, ExperimentalMaterial3Api::class)
@FixMethodOrder(MethodSorters.JVM)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
abstract class ButikSkarmbilderBas {
    protected abstract fun sprakKatalog(): String

    protected abstract fun skarmQualifiers(): String

    protected abstract fun funktionsgrafikQualifiers(): String

    protected abstract fun locale(): Locale

    @Before
    fun ställInSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
    }

    private fun captureSkarm(
        filnamn: String,
        helSkarm: Boolean = false,
        innehall: @Composable () -> Unit,
    ) {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
        val activity =
            Robolectric.buildActivity(ButikRoboActivity::class.java)
                .setup()
                .get()
        activity.setContent {
            AppfabrikTema(morkt = false) {
                innehall()
            }
        }
        ShadowLooper.idleMainLooper()
        val filSokvag = "butik/skarmbilder/${sprakKatalog()}/$filnamn"
        if (helSkarm) {
            captureScreenRoboImage(filSokvag)
        } else {
            activity.window.decorView.captureRoboImage(filSokvag)
        }
    }

    private fun captureFunktionsgrafik() {
        RuntimeEnvironment.setQualifiers(funktionsgrafikQualifiers())
        val activity =
            Robolectric.buildActivity(ButikRoboActivity::class.java)
                .setup()
                .get()
        activity.setContent {
            AppfabrikTema(morkt = false) {
                Box(
                    modifier =
                        Modifier
                            .size(1024.dp, 500.dp)
                            .background(AppfabrikFarger.LjusPrimar),
                    contentAlignment = androidx.compose.ui.Alignment.CenterStart,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 64.dp)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                        )
                        Text(
                            text = stringResource(R.string.pro_forklaring),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }
        ShadowLooper.idleMainLooper()
        activity.window.decorView.captureRoboImage("butik/funktionsgrafik-${sprakKatalog()}.png")
    }

    protected abstract fun exempelVeckaRader(): List<VeckaDagRad>

    protected abstract fun exempelDagFormular(): DagFormularTillstand

    @Test
    fun skarmQualifiersLaddarRattSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
        val app = ApplicationProvider.getApplicationContext<Application>()
        val appNamn = app.getString(R.string.app_name)
        when (sprakKatalog()) {
            "sv" -> assertEquals("Flextid", appNamn)
            "en" -> assertEquals("Flextime", appNamn)
        }
    }

    @Test
    fun funktionsgrafik() {
        captureFunktionsgrafik()
    }

    @Test
    fun idag() {
        val loc = locale()
        captureSkarm("01-idag.png") {
            FlextidButikRam(flik = FlextidFlik.IDAG, locale = loc) {
                IdagInnehall(
                    saldo = 125,
                    dag =
                        Dag(
                            datum = LocalDate.now(),
                            start = LocalTime.of(8, 0),
                            slut = LocalTime.of(16, 30),
                            rastMin = 30,
                        ),
                    standardRast = 30,
                    locale = loc,
                    onStamplaIn = noOp,
                    onStamplaUt = noOp,
                    onRastChange = {},
                    onRedigera = noOp,
                )
            }
        }
    }

    @Test
    fun vecka() {
        val loc = locale()
        captureSkarm("02-vecka.png") {
            FlextidButikRam(flik = FlextidFlik.VECKA, locale = loc) {
                VeckaInnehall(
                    veckaAnkar = veckaAnkar,
                    rader = exempelVeckaRader(),
                    locale = loc,
                    onForegaende = noOp,
                    onNasta = noOp,
                    onDennaVecka = noOp,
                    onDagClick = {},
                )
            }
        }
    }

    @Test
    fun dagformular() {
        val loc = locale()
        val form = exempelDagFormular()
        captureSkarm("03-dagformular.png", helSkarm = true) {
            FlextidButikRam(flik = FlextidFlik.IDAG, locale = loc) {
                IdagInnehall(
                    saldo = 125,
                    dag = null,
                    standardRast = 30,
                    locale = loc,
                    onStamplaIn = noOp,
                    onStamplaUt = noOp,
                    onRastChange = {},
                    onRedigera = noOp,
                )
            }
            DagFormularDialog(
                tillstand = form,
                locale = loc,
                onAndra = {},
                onSpara = noOp,
                onTaBort = noOp,
                onStang = noOp,
            )
        }
    }

    @Test
    fun installningar() {
        val loc = locale()
        captureSkarm("04-installningar.png") {
            FlextidButikRam(flik = FlextidFlik.INSTALLNINGAR, locale = loc) {
                InstallningarInnehall(
                    installning = Installning(ingangssaldoMin = 60),
                    exportArManad = YearMonth.of(2026, 10),
                    temaLage = TemaLage.SYSTEM,
                    locale = loc,
                    arPro = false,
                    antalDagar = 8,
                    stamplaUtPaminnelse = true,
                    stamplaInPaminnelse = false,
                    stamplaInPaminnelseMinEfterMidnatt = 480,
                    behorighetNekad = false,
                    onVisaProDialog = noOp,
                    onStamplaUtPaminnelse = {},
                    onStamplaInPaminnelse = {},
                    onStamplaInPaminnelseKlockslag = {},
                    onSpara = { _, _, _, _ -> },
                    onTema = {},
                    onExportManad = {},
                    onExporteraCsv = noOp,
                    onExporteraPdf = noOp,
                    onExporteraJson = noOp,
                    onImporteraJson = noOp,
                )
            }
        }
    }

    @Test
    fun proDialog() {
        val loc = locale()
        captureSkarm("05-pro-dialog.png", helSkarm = true) {
            FlextidButikRam(flik = FlextidFlik.INSTALLNINGAR, locale = loc) {
                InstallningarInnehall(
                    installning = Installning(),
                    exportArManad = YearMonth.of(2026, 10),
                    temaLage = TemaLage.SYSTEM,
                    locale = loc,
                    arPro = false,
                    antalDagar = 15,
                    stamplaUtPaminnelse = false,
                    stamplaInPaminnelse = false,
                    stamplaInPaminnelseMinEfterMidnatt = 480,
                    behorighetNekad = false,
                    onVisaProDialog = noOp,
                    onStamplaUtPaminnelse = {},
                    onStamplaInPaminnelse = {},
                    onStamplaInPaminnelseKlockslag = {},
                    onSpara = { _, _, _, _ -> },
                    onTema = {},
                    onExportManad = {},
                    onExporteraCsv = noOp,
                    onExporteraPdf = noOp,
                    onExporteraJson = noOp,
                    onImporteraJson = noOp,
                )
            }
            ProDialog(onDismiss = noOp, onKopPro = {})
        }
    }

    @Test
    fun proDialogSkarmbildSkiljerSigFranInstallningar() {
        val katalog = File("butik/skarmbilder/${sprakKatalog()}")
        val installningar = katalog.resolve("04-installningar.png").readBytes()
        val proDialog = katalog.resolve("05-pro-dialog.png").readBytes()
        assertFalse(
            "05-pro-dialog.png får inte vara identisk med 04-installningar.png (${sprakKatalog()})",
            installningar.contentEquals(proDialog),
        )
    }
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderSvTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "sv"

    override fun skarmQualifiers() = "sv-rSE-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "sv-rSE-w1024dp-h500dp-mdpi"

    override fun locale() = Locale.forLanguageTag("sv-SE")

    override fun exempelVeckaRader(): List<VeckaDagRad> =
        listOf(
            rad(LocalDate.of(2026, 10, 13), "08:00", "16:30", 30),
            rad(LocalDate.of(2026, 10, 14), "07:45", "15:45", 30),
            rad(LocalDate.of(2026, 10, 15), "08:00", "16:30", 30),
            rad(LocalDate.of(2026, 10, 16), null, null, 0, typ = DagTyp.LEDIG),
            rad(LocalDate.of(2026, 10, 17), "08:15", "14:00", 15),
            rad(LocalDate.of(2026, 10, 18), null, null, 0, typ = DagTyp.LEDIG),
            rad(LocalDate.of(2026, 10, 19), null, null, 0, typ = DagTyp.LEDIG),
        )

    override fun exempelDagFormular(): DagFormularTillstand =
        DagFormularTillstand(
            datum = LocalDate.of(2026, 10, 14),
            startText = "07:45",
            slutText = "15:45",
            rastText = "30",
            typ = DagTyp.ARBETE,
            anteckning = "Teamdag på kontoret",
        )

    private fun rad(
        datum: LocalDate,
        start: String?,
        slut: String?,
        rast: Int,
        typ: DagTyp = DagTyp.ARBETE,
        utfall: Int = 0,
    ): VeckaDagRad {
        val dag =
            if (start != null && slut != null) {
                Dag(
                    datum = datum,
                    start = LocalTime.parse(start),
                    slut = LocalTime.parse(slut),
                    rastMin = rast,
                    typ = typ,
                )
            } else if (typ != DagTyp.ARBETE) {
                Dag(datum = datum, typ = typ)
            } else {
                null
            }
        return VeckaDagRad(datum = datum, dag = dag, arbetadeMin = 450, utfallMin = utfall)
    }
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderEnTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "en"

    override fun skarmQualifiers() = "en-rGB-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "en-rGB-w1024dp-h500dp-mdpi"

    override fun locale() = Locale.UK

    override fun exempelVeckaRader(): List<VeckaDagRad> =
        listOf(
            rad(LocalDate.of(2026, 10, 13), "08:00", "16:30", 30),
            rad(LocalDate.of(2026, 10, 14), "07:45", "15:45", 30),
            rad(LocalDate.of(2026, 10, 15), "08:00", "16:30", 30),
            rad(LocalDate.of(2026, 10, 16), null, null, 0, typ = DagTyp.LEDIG),
            rad(LocalDate.of(2026, 10, 17), "08:15", "14:00", 15),
            rad(LocalDate.of(2026, 10, 18), null, null, 0, typ = DagTyp.LEDIG),
            rad(LocalDate.of(2026, 10, 19), null, null, 0, typ = DagTyp.LEDIG),
        )

    override fun exempelDagFormular(): DagFormularTillstand =
        DagFormularTillstand(
            datum = LocalDate.of(2026, 10, 14),
            startText = "07:45",
            slutText = "15:45",
            rastText = "30",
            typ = DagTyp.ARBETE,
            anteckning = "Office team day",
        )

    private fun rad(
        datum: LocalDate,
        start: String?,
        slut: String?,
        rast: Int,
        typ: DagTyp = DagTyp.ARBETE,
        utfall: Int = 0,
    ): VeckaDagRad {
        val dag =
            if (start != null && slut != null) {
                Dag(
                    datum = datum,
                    start = LocalTime.parse(start),
                    slut = LocalTime.parse(slut),
                    rastMin = rast,
                    typ = typ,
                )
            } else if (typ != DagTyp.ARBETE) {
                Dag(datum = datum, typ = typ)
            } else {
                null
            }
        return VeckaDagRad(datum = datum, dag = dag, arbetadeMin = 450, utfallMin = utfall)
    }
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = Application::class, sdk = [35])
class ButikPlayTillgangarTest {
    @Test
    fun ikon512() {
        RuntimeEnvironment.setQualifiers("w512dp-h512dp-mdpi")
        val activity =
            Robolectric.buildActivity(ButikRoboActivity::class.java)
                .setup()
                .get()
        activity.setContent {
            Box(
                modifier =
                    Modifier
                        .size(512.dp)
                        .background(Color(AppfabrikFarger.LjusPrimarArgb)),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        ShadowLooper.idleMainLooper()
        activity.window.decorView.captureRoboImage("butik/ikon-512.png")
    }
}
