@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.flextid.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class ExportPeriodAvsnittTest {
    private fun miljo() =
        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        )

    @Test
    fun manad13StangerAvKnapparnaIStalletForAttExporteraDecember() {
        val valda = mutableListOf<YearMonth>()
        var exporterat = 0
        miljo().runTest {
            setContent {
                ExportPeriodAvsnitt(
                    exportArManad = YearMonth.of(2026, 10),
                    onExportManad = { valda += it },
                    onExporteraCsv = { exporterat++ },
                    onExporteraPdf = { exporterat++ },
                    onExporteraArsCsv = { exporterat++ },
                )
            }
            onNodeWithText("10").performTextReplacement("13")
            onNodeWithText("Ange ett år (1900–9999) och en månad (1–12).").assertExists()
            onNodeWithText("Exportera CSV för månad").assertIsNotEnabled().performClick()
            onNodeWithText("Exportera PDF för månad").assertIsNotEnabled()
        }
        assertTrue(valda.isEmpty())
        assertEquals(0, exporterat)
    }

    @Test
    fun arFaltetGarAttTommaOchSkrivaOm() {
        val valda = mutableListOf<YearMonth>()
        miljo().runTest {
            setContent {
                ExportPeriodAvsnitt(
                    exportArManad = YearMonth.of(2026, 10),
                    onExportManad = { valda += it },
                    onExporteraCsv = {},
                    onExporteraPdf = {},
                    onExporteraArsCsv = {},
                )
            }
            onNodeWithText("2026").performTextReplacement("")
            onNodeWithText("Exportera CSV för månad").assertIsNotEnabled()
            onNodeWithText("Ange ett år (1900–9999) och en månad (1–12).").assertExists()
        }
        assertTrue(valda.isEmpty())
    }

    @Test
    fun giltigPeriodSkickasFore() {
        val valda = mutableListOf<YearMonth>()
        var exporterat = 0
        miljo().runTest {
            setContent {
                ExportPeriodAvsnitt(
                    exportArManad = YearMonth.of(2026, 10),
                    onExportManad = { valda += it },
                    onExporteraCsv = { exporterat++ },
                    onExporteraPdf = {},
                    onExporteraArsCsv = {},
                )
            }
            onNodeWithText("2026").performTextReplacement("2025")
            onNodeWithText("10").performTextReplacement("3")
            onNodeWithText("Exportera CSV för månad").performClick()
        }
        assertEquals(listOf(YearMonth.of(2025, 3)), valda)
        assertEquals(1, exporterat)
    }

    @Test
    fun inskrivetArSparasVidRotation() {
        miljo().runTest {
            var visa by mutableStateOf(true)
            var register by mutableStateOf(SaveableStateRegistry(null) { true })
            setContent {
                if (visa) {
                    CompositionLocalProvider(LocalSaveableStateRegistry provides register) {
                        ExportPeriodAvsnitt(
                            exportArManad = YearMonth.of(2026, 10),
                            onExportManad = {},
                            onExporteraCsv = {},
                            onExporteraPdf = {},
                            onExporteraArsCsv = {},
                        )
                    }
                }
            }
            onNodeWithText("2026").performTextReplacement("2024")
            waitForIdle()
            // Som när aktiviteten skapas om: spara, riv kompositionen och bygg upp den igen.
            val sparat = runOnIdle { register.performSave() }
            runOnIdle { visa = false }
            waitForIdle()
            runOnIdle {
                register = SaveableStateRegistry(sparat) { true }
                visa = true
            }
            waitForIdle()
            onNodeWithText("2024").assertExists()
        }
    }
}
