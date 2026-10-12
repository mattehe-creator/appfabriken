package se.tmconnect.garantivalvet.butik

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
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
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.ui.InstallningarSkarm
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.ProDialog
import se.tmconnect.karna.tema.AppfabrikFarger
import se.tmconnect.karna.tema.AppfabrikTema

private val noOp: () -> Unit = {}

class ButikRoboActivity : ComponentActivity()

@OptIn(ExperimentalRoborazziApi::class)
@FixMethodOrder(MethodSorters.JVM)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
abstract class ButikSkarmbilderBas {
    protected abstract fun sprakKatalog(): String

    protected abstract fun skarmQualifiers(): String

    @Before
    fun ställInSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
    }

    protected abstract fun exempelLista(): List<Kop>

    protected abstract fun exempelDetalj(): Kop

    protected abstract fun exempelFormular(): Kop

    protected abstract fun exempelAnteckning(): String

    private fun captureSkarm(
        filnamn: String,
        helSkarm: Boolean = false,
        innehall: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
        val activity = Robolectric.buildActivity(ButikRoboActivity::class.java)
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
        val activity = Robolectric.buildActivity(ButikRoboActivity::class.java)
            .setup()
            .get()
        activity.setContent {
            AppfabrikTema(morkt = false) {
                Box(
                    modifier = Modifier
                        .size(1024.dp, 500.dp)
                        .background(AppfabrikFarger.LjusPrimar),
                    contentAlignment = Alignment.CenterStart,
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

    protected abstract fun funktionsgrafikQualifiers(): String

    @Test
    fun skarmQualifiersLaddarRattSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
        val app = ApplicationProvider.getApplicationContext<Application>()
        val appNamn = app.getString(R.string.app_name)
        val fab = app.getString(R.string.fab_nytt_kvitto)
        when (sprakKatalog()) {
            "sv" -> {
                assertEquals("Garantivalvet", appNamn)
                assertEquals("Nytt kvitto", fab)
            }
            "en" -> {
                assertEquals("Warranty Vault", appNamn)
                assertEquals("New receipt", fab)
            }
        }
    }

    @Test
    fun funktionsgrafik() {
        captureFunktionsgrafik()
    }

    @Test
    fun lista() {
        captureSkarm("01-lista.png") {
            KopListaSkarm(
                kopLista = exempelLista(),
                kvittoUriFor = { null },
                onLaggTill = noOp,
                onOppna = {},
                onOppnaInstallningar = noOp,
            )
        }
    }

    @Test
    fun detaljMedReklamationsratt() {
        captureSkarm("02-detalj-reklamation.png") {
            KopDetaljSkarm(
                kop = exempelDetalj(),
                kvittoUri = null,
                onOppnaKvitto = noOp,
                onAndra = noOp,
                onTaBort = noOp,
                onTillbaka = noOp,
                arPro = false,
                onExportPdf = noOp,
                onVisaProDialog = noOp,
            )
        }
    }

    @Test
    fun formulär() {
        val formularData = exempelFormular()
        captureSkarm("03-formular.png") {
            KopFormularSkarm(
                titel = stringResource(R.string.lagg_till_titel),
                initialVad = formularData.vad,
                initialVar = formularData.varKopt!!,
                initialGarantiManader = formularData.garantiManader.toString(),
                initialPris = "8990",
                initialAnteckning = exempelAnteckning(),
                initialKopdatum = formularData.kopdatum,
                kvittoUri = null,
                onValjBild = noOp,
                onTaFoto = noOp,
                onTaBortKvitto = noOp,
                onSpara = { _, _, _, _, _, _ -> },
                onStang = noOp,
            )
        }
    }

    @Test
    fun proDialog() {
        captureSkarm("04-pro-dialog.png", helSkarm = true) {
            KopListaSkarm(
                kopLista = exempelLista(),
                kvittoUriFor = { null },
                onLaggTill = noOp,
                onOppna = {},
                onOppnaInstallningar = noOp,
            )
            ProDialog(
                onDismiss = noOp,
                onKopPro = {},
            )
        }
    }

    @Test
    fun installningar() {
        captureSkarm("05-installningar.png") {
            InstallningarSkarm(
                paminnelserPa = true,
                onPaminnelserAndras = {},
                behorighetNekad = false,
                arPro = false,
                onVisaProDialog = noOp,
                onSparaSakerhetskopia = noOp,
                onHamtaSakerhetskopia = noOp,
                onTillbaka = noOp,
            )
        }
    }

    @Test
    fun proDialogSkarmbildSkiljerSigFranLista() {
        val katalog = File("butik/skarmbilder/${sprakKatalog()}")
        val lista = katalog.resolve("01-lista.png").readBytes()
        val proDialog = katalog.resolve("04-pro-dialog.png").readBytes()
        assertFalse(
            "04-pro-dialog.png får inte vara identisk med 01-lista.png (${sprakKatalog()})",
            lista.contentEquals(proDialog),
        )
    }
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderSvTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "sv"

    override fun skarmQualifiers() = "sv-rSE-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "sv-rSE-w1024dp-h500dp-mdpi"

    override fun exempelLista(): List<Kop> {
        return listOf(
            Kop(
                id = 1,
                vad = "Kaffebryggare",
                varKopt = "Elektronikbutiken",
                kopdatum = LocalDate.of(2025, 11, 1),
                garantiManader = 12,
            ),
            Kop(
                id = 2,
                vad = "Diskmaskin",
                varKopt = "Lokal vitvaruhandel",
                kopdatum = LocalDate.of(2025, 3, 10),
                garantiManader = 24,
            ),
            Kop(
                id = 3,
                vad = "Dammsugare",
                varKopt = "Hem och hus",
                kopdatum = LocalDate.of(2022, 6, 1),
                garantiManader = 12,
            ),
        )
    }

    override fun exempelDetalj(): Kop = exempelLista()[1]

    override fun exempelFormular(): Kop = exempelLista()[1]

    override fun exempelAnteckning(): String = "Extragaranti registrerad."
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderEnTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "en"

    override fun skarmQualifiers() = "en-rGB-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "en-rGB-w1024dp-h500dp-mdpi"

    override fun exempelLista(): List<Kop> {
        return listOf(
            Kop(
                id = 1,
                vad = "Coffee maker",
                varKopt = "Electronics store",
                kopdatum = LocalDate.of(2025, 11, 1),
                garantiManader = 12,
            ),
            Kop(
                id = 2,
                vad = "Dishwasher",
                varKopt = "Local appliance store",
                kopdatum = LocalDate.of(2025, 3, 10),
                garantiManader = 24,
            ),
            Kop(
                id = 3,
                vad = "Vacuum cleaner",
                varKopt = "Home store",
                kopdatum = LocalDate.of(2022, 6, 1),
                garantiManader = 12,
            ),
        )
    }

    override fun exempelDetalj(): Kop = exempelLista()[1]

    override fun exempelFormular(): Kop = exempelLista()[1]

    override fun exempelAnteckning(): String = "Extended warranty registered."
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = Application::class, sdk = [35])
class ButikPlayTillgangarTest {
    @Test
    fun ikon512() {
        RuntimeEnvironment.setQualifiers("w512dp-h512dp-mdpi")
        val activity = Robolectric.buildActivity(ButikRoboActivity::class.java)
            .setup()
            .get()
        activity.setContent {
            Box(
                modifier = Modifier
                    .size(512.dp)
                    .background(Color(AppfabrikFarger.LjusPrimarArgb)),
                contentAlignment = Alignment.Center,
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
