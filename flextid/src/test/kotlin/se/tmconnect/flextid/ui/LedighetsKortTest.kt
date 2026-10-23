@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.flextid.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.onNodeWithText
import java.time.LocalDate
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class LedighetsKortTest {
    private val locale = Locale.forLanguageTag("sv-SE")

    @Test
    fun visarFlexdagarNarGolvSatt() {
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                LedighetsKort(
                    tillstand =
                        LedighetsKortTillstand(
                            flexDagar = 2,
                            tidigasteLedig = LocalDate.of(2026, 10, 23),
                        ),
                    locale = locale,
                    onPlaneraLedig = {},
                )
            }
            onNodeWithText("Du kan ta ut 2 flexdagar").assertExists()
            onNodeWithText("Tidigast ledig 23 okt. 2026").assertExists()
        }
    }

    @Test
    fun visarIngenGransNarGolvSaknas() {
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                LedighetsKort(
                    tillstand =
                        LedighetsKortTillstand(
                            flexDagar = null,
                            tidigasteLedig = LocalDate.of(2026, 10, 23),
                        ),
                    locale = locale,
                    onPlaneraLedig = {},
                )
            }
            onNodeWithText("Ingen gräns satt").assertExists()
        }
    }

    @Test
    fun visarForLagtSaldo() {
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                LedighetsKort(
                    tillstand =
                        LedighetsKortTillstand(
                            flexDagar = 0,
                            tidigasteLedig = null,
                        ),
                    locale = locale,
                    onPlaneraLedig = {},
                )
            }
            onNodeWithText("Saldo räcker inte för en hel flexdag").assertExists()
        }
    }
}
