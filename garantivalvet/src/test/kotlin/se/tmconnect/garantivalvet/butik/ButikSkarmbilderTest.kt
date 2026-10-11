package se.tmconnect.garantivalvet.butik

import android.app.Application
import android.content.res.Configuration
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
import com.github.takahirom.roborazzi.captureRoboImage
import java.time.LocalDate
import java.util.Locale
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.karna.tema.AppfabrikFarger
import se.tmconnect.karna.tema.AppfabrikTema

private val exempelLista = listOf(
    Kop(
        id = 1,
        vad = "Diskmaskin",
        varKopt = "Lokal vitvaruhandel",
        kopdatum = LocalDate.of(2025, 3, 10),
        garantiManader = 24,
    ),
    Kop(
        id = 2,
        vad = "Mobiltelefon",
        varKopt = "Telefonbutiken i centrum",
        kopdatum = LocalDate.of(2025, 8, 1),
        garantiManader = 12,
    ),
    Kop(
        id = 3,
        vad = "Cykel",
        varKopt = "Sport och fritid",
        kopdatum = LocalDate.of(2024, 11, 20),
        garantiManader = 36,
    ),
)

private val exempelDetalj = exempelLista.first()

private val noOp: () -> Unit = {}

class ButikRoboActivity : ComponentActivity()

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
abstract class ButikSkarmbilderBas {
    protected abstract fun sprakKatalog(): String

    protected abstract fun localeTag(): String

    protected abstract fun skarmQualifiers(): String

    @Before
    fun ställInSprak() {
        val locale = Locale.forLanguageTag(localeTag())
        Locale.setDefault(locale)
        val app = ApplicationProvider.getApplicationContext<Application>()
        val config = Configuration(app.resources.configuration)
        config.setLocale(locale)
        app.resources.updateConfiguration(config, app.resources.displayMetrics)
    }

    private fun captureSkarm(
        filnamn: String,
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
        activity.window.decorView.captureRoboImage("butik/skarmbilder/${sprakKatalog()}/$filnamn")
    }

    @Test
    fun lista() {
        captureSkarm("01-lista.png") {
            KopListaSkarm(
                kopLista = exempelLista,
                onLaggTill = noOp,
                onOppna = {},
                paminnelserPa = true,
                onPaminnelserAndras = {},
                behorighetNekad = false,
                arPro = false,
                proDialogSynlig = false,
                onVisaProDialog = noOp,
                onStangProDialog = noOp,
                onKopPro = {},
            )
        }
    }

    @Test
    fun detaljMedReklamationsratt() {
        captureSkarm("02-detalj-reklamation.png") {
            KopDetaljSkarm(
                kop = exempelDetalj,
                kvittoUri = null,
                onOppnaKvitto = noOp,
                onAndra = noOp,
                onTaBort = noOp,
                onTillbaka = noOp,
                arPro = false,
                onExportPdf = noOp,
                onDelaPdf = noOp,
                onVisaProDialog = noOp,
                exportPdfMeddelande = null,
                onExportMeddelandeVisat = noOp,
            )
        }
    }

    @Test
    fun formulär() {
        captureSkarm("03-formular.png") {
            KopFormularSkarm(
                titel = stringResource(R.string.lagg_till_titel),
                initialVad = "Diskmaskin",
                initialVar = "Lokal vitvaruhandel",
                initialGarantiManader = "24",
                initialPris = "8990",
                initialAnteckning = "Extended warranty registered.",
                initialKopdatum = LocalDate.of(2025, 3, 10),
                kvittoUri = null,
                onValjBild = noOp,
                onTaFoto = noOp,
                onTaBortKvitto = noOp,
                onSpara = { _, _, _, _, _, _ -> },
                onAvbryt = noOp,
            )
        }
    }

    @Test
    fun proDialog() {
        captureSkarm("04-pro-dialog.png") {
            KopListaSkarm(
                kopLista = exempelLista,
                onLaggTill = noOp,
                onOppna = {},
                paminnelserPa = true,
                onPaminnelserAndras = {},
                behorighetNekad = false,
                arPro = false,
                proDialogSynlig = true,
                onVisaProDialog = noOp,
                onStangProDialog = noOp,
                onKopPro = {},
            )
        }
    }
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderSvTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "sv"

    override fun localeTag() = "sv-SE"

    override fun skarmQualifiers() = "w360dp-h640dp-port-xxhdpi"
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderEnTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "en"

    override fun localeTag() = "en-GB"

    override fun skarmQualifiers() = "w360dp-h640dp-port-xxhdpi"
}

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = Application::class, sdk = [35])
class ButikPlayTillgangarTest {
    @Test
    fun funktionsgrafik() {
        RuntimeEnvironment.setQualifiers("w1024dp-h500dp-mdpi")
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
                            text = "Kvitton, garantier och reklamationsfrister – helt offline",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                }
            }
        }
        ShadowLooper.idleMainLooper()
        activity.window.decorView.captureRoboImage("butik/funktionsgrafik.png")
    }

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
