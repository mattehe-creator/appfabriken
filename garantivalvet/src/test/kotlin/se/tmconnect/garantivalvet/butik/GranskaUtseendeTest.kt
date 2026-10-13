package se.tmconnect.garantivalvet.butik

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureRoboImage
import java.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.ui.InstallningarSkarm
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.ReklamationsSkarm
import se.tmconnect.karna.tema.AppfabrikTema

/**
 * Granskningsbilder (inte butiksbilder): samma skärmar i mörkt läge och med stor text, till
 * `butik/granskning/`, så att utseendet kan kontrolleras utan telefon. Skrivs bara av recordRoborazziDebug.
 */
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = Application::class, sdk = [35])
class GranskaUtseendeTest {
    private val noOp: () -> Unit = {}

    private val lista = listOf(
        Kop(id = 1, vad = "Kaffebryggare", varKopt = "Elektronikbutiken", kopdatum = LocalDate.of(2025, 11, 1), garantiManader = 12, prisOre = 129_900),
        Kop(id = 2, vad = "Diskmaskin", varKopt = "Lokal vitvaruhandel", kopdatum = LocalDate.of(2025, 3, 10), garantiManader = 24, prisOre = 899_000),
        Kop(id = 3, vad = "Dammsugare", varKopt = "Hem och hus", kopdatum = LocalDate.of(2022, 6, 1), garantiManader = 12),
    )

    private fun fota(namn: String, morkt: Boolean, fontScale: Float, innehall: @Composable () -> Unit) {
        RuntimeEnvironment.setQualifiers("sv-rSE-w360dp-h640dp-port-" + (if (morkt) "night" else "notnight") + "-xxhdpi")
        RuntimeEnvironment.setFontScale(fontScale)
        val activity = Robolectric.buildActivity(ButikRoboActivity::class.java).setup().get()
        activity.setContent { AppfabrikTema(morkt = morkt) { innehall() } }
        ShadowLooper.idleMainLooper()
        activity.window.decorView.captureRoboImage("butik/granskning/$namn.png")
    }

    private fun alla(prefix: String, morkt: Boolean, fontScale: Float) {
        fota("$prefix-01-lista", morkt, fontScale) {
            KopListaSkarm(kopLista = lista, kvittoUriFor = { null }, onLaggTill = noOp, onOppna = {}, onOppnaInstallningar = noOp)
        }
        fota("$prefix-02-detalj", morkt, fontScale) {
            KopDetaljSkarm(
                kop = lista[1].copy(felBeskrivning = "Läcker vatten.", felUpptackt = LocalDate.of(2026, 10, 1)),
                kvittoUri = null, onOppnaKvitto = noOp, onAndra = noOp, onTaBort = noOp, onTillbaka = noOp,
                arPro = false, onExportPdf = noOp, onVisaProDialog = noOp,
            )
        }
        fota("$prefix-03-formular", morkt, fontScale) {
            KopFormularSkarm(
                titel = "Nytt kvitto", initialVad = "Diskmaskin", initialVar = "Lokal vitvaruhandel",
                initialGarantiManader = "24", initialPris = "8990", initialAnteckning = null,
                initialKopdatum = LocalDate.of(2025, 3, 10), kvittoUri = null, onValjBild = noOp, onTaFoto = noOp,
                onTaBortKvitto = noOp, onSpara = { _, _, _, _, _, _, _, _, _ -> }, onStang = noOp,
            )
        }
        fota("$prefix-04-reklamera", morkt, fontScale) {
            ReklamationsSkarm(
                kop = lista[1].copy(felBeskrivning = "Diskmaskinen läcker vatten från luckan.", felUpptackt = LocalDate.of(2026, 10, 1)),
                kvittoFinns = true, onSkickaMejl = { _, _, _ -> }, onSparaFel = { _, _ -> },
                onHarReklamerat = { _, _ -> }, onTillbaka = noOp,
            )
        }
        fota("$prefix-05-installningar", morkt, fontScale) {
            InstallningarSkarm(
                paminnelserPa = true, onPaminnelserAndras = {}, behorighetNekad = false, arPro = false,
                onVisaProDialog = noOp, onSparaSakerhetskopia = noOp, onHamtaSakerhetskopia = noOp, onTillbaka = noOp,
            )
        }
    }

    @Test
    fun morktLage() = alla("mork", morkt = true, fontScale = 1f)

    @Test
    fun morktLageStorText() = alla("mork-stor", morkt = true, fontScale = 1.5f)

    @Test
    fun ljustLageStorText() = alla("ljus-stor", morkt = false, fontScale = 1.5f)
}
