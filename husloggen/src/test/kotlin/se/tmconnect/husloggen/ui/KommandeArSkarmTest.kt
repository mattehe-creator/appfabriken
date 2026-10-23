@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.husloggen.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.regler.KommandeUnderhallArGrupp
import se.tmconnect.husloggen.regler.KommandeUnderhallRad
import java.time.LocalDate
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class KommandeArSkarmTest {

    @Test
    fun visarArssummaOchUppgift() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locale = Locale.forLanguageTag("sv-SE")
        val grupper =
            listOf(
                KommandeUnderhallArGrupp(
                    ar = 2027,
                    rader =
                        listOf(
                            KommandeUnderhallRad(
                                uppgiftId = 1L,
                                uppgiftNamn = "Takrengöring",
                                datum = LocalDate.of(2027, 4, 1),
                                uppskattadKostnadOre = 250_000L,
                            ),
                        ),
                    summaOre = 250_000L,
                    harOkandKostnad = false,
                ),
            )
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                KommandeArSkarm(
                    grupper = grupper,
                    locale = locale,
                    onTillbaka = {},
                    onExportCsv = {},
                )
            }
            waitForIdle()
            onNodeWithText(context.getString(R.string.kommande_ar_rubrik)).assertExists()
            onNodeWithText("Takrengöring").assertExists()
            onNodeWithText("2027").assertExists()
        }
    }
}
