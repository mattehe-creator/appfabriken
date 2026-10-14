@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.garantivalvet.tillganglighet

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import se.tmconnect.garantivalvet.butik.ButikRoboActivity
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.export.AutoBackupTillstand
import se.tmconnect.garantivalvet.ui.InstallningarSkarm
import se.tmconnect.garantivalvet.ui.KopDetaljSkarm
import se.tmconnect.garantivalvet.ui.KopFormularSkarm
import se.tmconnect.garantivalvet.ui.KopListaSkarm
import se.tmconnect.garantivalvet.ui.ReklamationsSkarm
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.karna.tema.AppfabrikTema

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(application = Application::class, sdk = [35])
class TryckytaTest {
    private val noOp: () -> Unit = {}

    private val listaFem = (1..5).map { id ->
        Kop(
            id = id.toLong(),
            vad = "Vara $id",
            varKopt = "Butik",
            kopdatum = LocalDate.of(2025, 1, id),
            garantiManader = 24,
            varutyp = Varutyp.ELEKTRONIK.name,
        )
    }

    private val detaljKop = listaFem[1].copy(
        felBeskrivning = "Läcker vatten.",
        felUpptackt = LocalDate.of(2026, 10, 1),
        butikEpost = "butik@example.com",
        butikTelefon = "08-123 45 67",
    )

    private fun kor(morkt: Boolean, fontScale: Float, innehall: @Composable () -> Unit) {
        RuntimeEnvironment.setQualifiers(
            "sv-rSE-w360dp-h640dp-port-" + (if (morkt) "night" else "notnight") + "-xxhdpi",
        )
        RuntimeEnvironment.setFontScale(fontScale)
        AndroidComposeUiTestEnvironment<ButikRoboActivity>(
            activityProvider = {
                Robolectric.buildActivity(ButikRoboActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                AppfabrikTema(morkt = morkt) {
                    innehall()
                }
            }
            waitForIdle()
            val minPx = with(density) { 48.dp.toPx() }
            val fel = mutableListOf<String>()
            val klick = hasClickAction() and !hasScrollAction()
            onAllNodes(klick).fetchSemanticsNodes().forEach { node ->
                val h = node.size.height
                val w = node.size.width
                if (w < 1f || h < 1f) {
                    return@forEach
                }
                // Scroll-container och liknande kan få orimlig höjd i Robolectric; inte en tryckyta.
                if (h > minPx * 4) {
                    return@forEach
                }
                if (h + 0.5f < minPx || w + 0.5f < minPx) {
                    fel.add("Klickbar nod ${w.toInt()}×${h.toInt()} px (min ${minPx.toInt()} px)")
                }
            }
            assertTrue(fel.joinToString("\n"), fel.isEmpty())
        }
    }

    private fun allaLagen(skarm: @Composable () -> Unit) {
        for (morkt in listOf(false, true)) {
            for (fontScale in listOf(1f, 1.5f)) {
                kor(morkt, fontScale, skarm)
            }
        }
    }

    @Test
    fun kopListaSkarm() {
        allaLagen {
            KopListaSkarm(
                kopLista = listaFem,
                kvittoUriFor = { null },
                onLaggTill = noOp,
                onOppna = {},
                onOppnaInstallningar = noOp,
            )
        }
    }

    @Test
    fun kopDetaljSkarmUtanReklamation() {
        allaLagen {
            KopDetaljSkarm(
                kop = listaFem[1],
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
    fun kopDetaljSkarmMedReklamation() {
        allaLagen {
            KopDetaljSkarm(
                kop = detaljKop.copy(reklamerad = LocalDate.of(2026, 10, 5)),
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
    fun kopFormularSkarm() {
        allaLagen {
            KopFormularSkarm(
                titel = "Nytt kvitto",
                initialVad = "Diskmaskin",
                initialVar = "Lokal vitvaruhandel",
                initialGarantiManader = "24",
                initialPris = "8990",
                initialAnteckning = null,
                initialKopdatum = LocalDate.of(2025, 3, 10),
                kvittoUri = null,
                onValjBild = noOp,
                onTaFoto = noOp,
                onTaBortKvitto = noOp,
                onSpara = { _, _, _, _, _, _, _, _, _ -> },
                onStang = noOp,
            )
        }
    }

    @Test
    fun reklamationsSkarm() {
        allaLagen {
            ReklamationsSkarm(
                kop = detaljKop,
                kvittoFinns = true,
                onSkickaMejl = { _, _, _ -> },
                onSparaFel = { _, _ -> },
                onHarReklamerat = { _, _ -> },
                onTillbaka = noOp,
            )
        }
    }

    @Test
    fun installningarSkarm() {
        allaLagen {
            InstallningarSkarm(
                paminnelserPa = true,
                onPaminnelserAndras = {},
                behorighetNekad = false,
                arPro = false,
                onVisaProDialog = noOp,
                onSparaSakerhetskopia = noOp,
                onHamtaSakerhetskopia = noOp,
                onTillbaka = noOp,
                autoBackup = AutoBackupTillstand(pa = true, mappNamn = "Säkerhetskopia"),
                onAutoBackupPa = {},
                onByttMapp = noOp,
                onAutoBackupIntervall = {},
                onKorNuAutoBackup = noOp,
            )
        }
    }

    @Test
    fun valkommenSkarm() {
        allaLagen {
            se.tmconnect.garantivalvet.ui.ValkommenSkarm(onFotaForstaKvittot = noOp, onTittaRunt = noOp)
        }
    }
}
