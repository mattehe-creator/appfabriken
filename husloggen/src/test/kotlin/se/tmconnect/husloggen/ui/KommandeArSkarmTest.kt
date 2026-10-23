@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.husloggen.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.karna.format.formateraPrisKr
import se.tmconnect.karna.tema.AppfabrikTema

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class KommandeArSkarmTest {

    @Test
    fun visarRubrikArssummaOchExport() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locale = Locale.forLanguageTag("sv-SE")
        val idag = LocalDate.of(2026, 10, 23)
        val uppgifter =
            listOf(
                Uppgift(
                    id = 1L,
                    namn = "Takrengöring",
                    intervallManader = 12,
                    startDatum = LocalDate.of(2025, 10, 1),
                    senastGjord = LocalDate.of(2025, 10, 1),
                ),
            )
        val poster =
            listOf(
                Post(
                    datum = LocalDate.of(2025, 10, 1),
                    vad = "Tak",
                    typ = PostTyp.UNDERHALL,
                    beloppOre = 200_000L,
                    uppgiftId = 1L,
                ),
            )
        val forvantadSumma =
            context.getString(
                R.string.kommande_ar_summa,
                formateraPrisKr(200_000L, locale),
            )

        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                AppfabrikTema(morkt = false) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        KommandeArSkarm(
                            uppgifter = uppgifter,
                            poster = poster,
                            locale = locale,
                            contentPadding = PaddingValues(0.dp),
                            onExportCsv = {},
                            idag = idag,
                        )
                    }
                }
            }
            waitForIdle()
            onNodeWithText(context.getString(R.string.kommande_ar_rubrik)).assertExists()
            onNodeWithText(context.getString(R.string.export_kommande_ar_csv)).assertExists()
            val scrollable = onNode(hasScrollAction())
            scrollable.performScrollToNode(hasText(forvantadSumma))
            onNodeWithText(forvantadSumma).assertExists()
        }
    }
}
