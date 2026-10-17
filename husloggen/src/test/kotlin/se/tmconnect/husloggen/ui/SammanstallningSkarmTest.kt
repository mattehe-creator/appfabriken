@file:OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)

package se.tmconnect.husloggen.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.AndroidComposeUiTestEnvironment
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import java.util.Locale
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], qualifiers = "sv-rSE")
class SammanstallningSkarmTest {
    @Test
    fun sammanstallningVisarForbattringsetiketterForExempelposter() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val poster = listOf(
            Post(
                id = 1,
                datum = java.time.LocalDate.of(2021, 6, 1),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 50_000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis(),
            ),
            Post(
                id = 2,
                datum = java.time.LocalDate.of(2020, 12, 31),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 50_000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis(),
            ),
            Post(
                id = 3,
                datum = java.time.LocalDate.of(2015, 1, 1),
                vad = "Ombyggnad",
                typ = PostTyp.OMBYGGNAD,
                beloppOre = 50_000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis(),
            ),
        )

        AndroidComposeUiTestEnvironment<ComponentActivity>(
            activityProvider = {
                Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
            },
        ).runTest {
            setContent {
                Box(modifier = Modifier.fillMaxSize()) {
                    SammanstallningSkarm(
                        poster = poster,
                        forsaljningsAr = 2026,
                        onForsaljningsArAndras = {},
                        locale = Locale.forLanguageTag("sv-SE"),
                        visaSkatteInfo = true,
                    )
                }
            }
            waitForIdle()
            val bedomning = context.getString(R.string.sammanstallning_forbattring_bedomning)
            val nej = context.getString(R.string.sammanstallning_forbattring_nej)
            val ja = context.getString(R.string.sammanstallning_forbattring_ja)
            val scrollable = onNode(hasScrollAction())
            scrollable.performScrollToNode(hasText(bedomning))
            onNodeWithText(bedomning).assertExists()
            scrollable.performScrollToNode(hasText(nej))
            onNodeWithText(nej).assertExists()
            scrollable.performScrollToNode(hasText(ja))
            onNodeWithText(ja).assertExists()
        }
    }
}
