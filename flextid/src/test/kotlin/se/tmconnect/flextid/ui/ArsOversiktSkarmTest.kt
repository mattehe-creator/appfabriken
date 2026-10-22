@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.flextid.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.onNodeWithText
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.flextid.regler.ManadsRad

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class ArsOversiktSkarmTest {
    @Test
    fun visarPrognosOchManadslista() {
        val locale = Locale.forLanguageTag("sv-SE")
        val tillstand =
            ArsOversiktTillstand(
                ar = 2026,
                rader =
                    listOf(
                        ManadsRad(manad = 1, arbetadMin = 480, utfallMin = 30, saldoVidSlutMin = 30),
                    ),
                prognosMin = 120,
                arArInnevarandeAr = true,
            )
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                ArsOversiktInnehall(
                    tillstand = tillstand,
                    locale = locale,
                    onExporteraCsv = {},
                )
            }
            onNodeWithText("Uppskattat saldo vid årets slut").assertExists()
            onNodeWithText("+2:00").assertExists()
            onNodeWithText("jan.").assertExists()
            onNodeWithText("Årsöversikt (CSV)").assertExists()
        }
    }
}
