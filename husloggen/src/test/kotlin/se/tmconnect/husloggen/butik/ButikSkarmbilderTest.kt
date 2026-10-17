package se.tmconnect.husloggen.butik

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
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.ui.HusFlik
import se.tmconnect.husloggen.ui.HusStartSkarm
import se.tmconnect.husloggen.ui.PostFormularSkarm
import se.tmconnect.husloggen.ui.PostFormularTillstand
import se.tmconnect.husloggen.ui.ProDialog
import se.tmconnect.husloggen.ui.SammanstallningSkarm
import se.tmconnect.karna.tema.AppfabrikFarger
import se.tmconnect.karna.tema.AppfabrikTema

private val noOp: () -> Unit = {}
private val idag = LocalDate.of(2026, 10, 15)

class ButikRoboActivity : ComponentActivity()

@OptIn(ExperimentalRoborazziApi::class)
@FixMethodOrder(MethodSorters.JVM)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
abstract class ButikSkarmbilderBas {
    protected abstract fun sprakKatalog(): String

    protected abstract fun skarmQualifiers(): String

    protected abstract fun funktionsgrafikQualifiers(): String

    protected abstract fun exempelUppgifter(): List<Uppgift>

    protected abstract fun exempelPoster(): List<Post>

    protected abstract fun exempelFormular(): PostFormularTillstand

    @Before
    fun ställInSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
    }

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

    @Test
    fun skarmQualifiersLaddarRattSprak() {
        RuntimeEnvironment.setQualifiers(skarmQualifiers())
        val app = ApplicationProvider.getApplicationContext<Application>()
        val appNamn = app.getString(R.string.app_name)
        val fab = app.getString(R.string.fab_ny_uppgift)
        when (sprakKatalog()) {
            "sv" -> {
                assertEquals("Husloggen", appNamn)
                assertEquals("Ny uppgift", fab)
            }
            "en" -> {
                assertEquals("House Log", appNamn)
                assertEquals("New task", fab)
            }
        }
    }

    @Test
    fun funktionsgrafik() {
        captureFunktionsgrafik()
    }

    @Test
    fun attGora() {
        captureSkarm("01-att-gora.png") {
            HusStartSkarm(
                uppgifter = exempelUppgifter(),
                poster = exempelPoster(),
                onInstallningar = noOp,
                onNyUppgift = noOp,
                onNyPost = noOp,
                onOppnaUppgift = {},
                onOppnaPost = {},
                onMarkeraGjord = {},
                startFlik = HusFlik.ATT_GORA,
            )
        }
    }

    @Test
    fun logg() {
        captureSkarm("02-logg.png") {
            HusStartSkarm(
                uppgifter = exempelUppgifter(),
                poster = exempelPoster(),
                onInstallningar = noOp,
                onNyUppgift = noOp,
                onNyPost = noOp,
                onOppnaUppgift = {},
                onOppnaPost = {},
                onMarkeraGjord = {},
                startFlik = HusFlik.LOGG,
            )
        }
    }

    @Test
    fun formulär() {
        captureSkarm("03-formular.png") {
            PostFormularSkarm(
                tillstand = exempelFormular(),
                uppgifter = exempelUppgifter(),
                idag = idag,
                onVadAndras = {},
                onDatumAndras = {},
                onTypAndras = {},
                onBeloppAndras = {},
                onRotRutAvdragAndras = {},
                onUtforareAndras = {},
                onUppgiftAndras = {},
                onAnteckningAndras = {},
                bildUri = { null },
                onBildValj = noOp,
                onTaFoto = noOp,
                onTaBortBild = {},
                onSpara = noOp,
                onTaBort = noOp,
                onStang = noOp,
            )
        }
    }

    @Test
    fun sammanstallning() {
        val locale = when (sprakKatalog()) {
            "sv" -> Locale.forLanguageTag("sv-SE")
            else -> Locale.UK
        }
        captureSkarm("04-sammanstallning.png") {
            SammanstallningSkarm(
                poster = exempelPoster(),
                forsaljningsAr = 2026,
                onForsaljningsArAndras = {},
                locale = locale,
                visaSkatteInfo = sprakKatalog() == "sv",
            )
        }
    }

    @Test
    fun proDialog() {
        captureSkarm("05-pro-dialog.png", helSkarm = true) {
            HusStartSkarm(
                uppgifter = exempelUppgifter(),
                poster = exempelPoster(),
                onInstallningar = noOp,
                onNyUppgift = noOp,
                onNyPost = noOp,
                onOppnaUppgift = {},
                onOppnaPost = {},
                onMarkeraGjord = {},
                startFlik = HusFlik.LOGG,
            )
            ProDialog(onDismiss = noOp, onKopPro = {})
        }
    }

    @Test
    fun proDialogSkarmbildSkiljerSigFranLogg() {
        val katalog = File("butik/skarmbilder/${sprakKatalog()}")
        val logg = katalog.resolve("02-logg.png").readBytes()
        val proDialog = katalog.resolve("05-pro-dialog.png").readBytes()
        assertFalse(
            "05-pro-dialog.png får inte vara identisk med 02-logg.png (${sprakKatalog()})",
            logg.contentEquals(proDialog),
        )
    }
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderSvTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "sv"

    override fun skarmQualifiers() = "sv-rSE-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "sv-rSE-w1024dp-h500dp-mdpi"

    override fun exempelUppgifter(): List<Uppgift> =
        listOf(
            Uppgift(
                id = 1,
                namn = "Rensa takrännor",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
                senastGjord = LocalDate.of(2025, 4, 10),
            ),
            Uppgift(
                id = 2,
                namn = "Service värmepump",
                intervallManader = 24,
                startDatum = LocalDate.of(2024, 9, 1),
            ),
        )

    override fun exempelPoster(): List<Post> =
        listOf(
            Post(
                id = 1,
                datum = LocalDate.of(2026, 3, 12),
                vad = "Byte av cirkulationspump",
                typ = PostTyp.REPARATION,
                beloppOre = 4_850_00,
                utforare = "Lokal VVS",
            ),
            Post(
                id = 2,
                datum = LocalDate.of(2025, 11, 2),
                vad = "Målning fasad söder",
                typ = PostTyp.UNDERHALL,
                beloppOre = 18_500_00,
                utforare = "Måleri AB",
            ),
        )

    override fun exempelFormular(): PostFormularTillstand =
        PostFormularTillstand(
            vad = "Tätning fönster kök",
            datum = LocalDate.of(2026, 10, 1),
            typ = PostTyp.UNDERHALL,
            beloppText = "3200",
            utforare = "Byggfirma",
            anteckning = "Silikon runt karm.",
        )
}

@Config(application = Application::class, sdk = [35])
class ButikSkarmbilderEnTest : ButikSkarmbilderBas() {
    override fun sprakKatalog() = "en"

    override fun skarmQualifiers() = "en-rGB-w360dp-h640dp-port-xxhdpi"

    override fun funktionsgrafikQualifiers() = "en-rGB-w1024dp-h500dp-mdpi"

    override fun exempelUppgifter(): List<Uppgift> =
        listOf(
            Uppgift(
                id = 1,
                namn = "Clean gutters",
                intervallManader = 12,
                startDatum = LocalDate.of(2026, 4, 1),
                senastGjord = LocalDate.of(2025, 4, 10),
            ),
            Uppgift(
                id = 2,
                namn = "Heat pump service",
                intervallManader = 24,
                startDatum = LocalDate.of(2024, 9, 1),
            ),
        )

    override fun exempelPoster(): List<Post> =
        listOf(
            Post(
                id = 1,
                datum = LocalDate.of(2026, 3, 12),
                vad = "Circulation pump replaced",
                typ = PostTyp.REPARATION,
                beloppOre = 4_850_00,
                utforare = "Local plumber",
            ),
            Post(
                id = 2,
                datum = LocalDate.of(2025, 11, 2),
                vad = "South facade paint",
                typ = PostTyp.UNDERHALL,
                beloppOre = 18_500_00,
                utforare = "Paint crew",
            ),
        )

    override fun exempelFormular(): PostFormularTillstand =
        PostFormularTillstand(
            vad = "Kitchen window seal",
            datum = LocalDate.of(2026, 10, 1),
            typ = PostTyp.UNDERHALL,
            beloppText = "3200",
            utforare = "Builder",
            anteckning = "Silicone around frame.",
        )
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
