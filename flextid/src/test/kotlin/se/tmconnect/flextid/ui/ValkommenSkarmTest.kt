@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.flextid.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.onNodeWithText
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class ValkommenSkarmTest {
    @Test
    fun visarRubrikOchKnappar() {
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                ValkommenSkarm(onStamplaIn = {}, onTittaRunt = {})
            }
            onNodeWithText("Välkommen till Flextid").assertExists()
            onNodeWithText("Stämpla in för idag").assertExists()
            onNodeWithText("Titta runt först").assertExists()
        }
    }
}
