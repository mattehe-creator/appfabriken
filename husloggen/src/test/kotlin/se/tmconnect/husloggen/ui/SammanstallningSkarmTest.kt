package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

@RunWith(AndroidJUnit4::class)
class SammanstallningSkarmTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSammanstallningSkarm() {
        // Skapa exempelposter enligt specifikationen
        val poster = listOf(
            Post(
                id = 1,
                datum = LocalDate.of(2021, 6, 1),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 100000L,
                anteckning = null,
                uppgiftId = null,
                kvittoFil = null,
                bilder = emptyList(),
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 2,
                datum = LocalDate.of(2020, 12, 31),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 150000L,
                anteckning = null,
                uppgiftId = null,
                kvittoFil = null,
                bilder = emptyList(),
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 3,
                datum = LocalDate.of(2015, 1, 1),
                vad = "Ombyggnad",
                typ = PostTyp.OMBYGGNAD,
                beloppOre = 200000L,
                anteckning = null,
                uppgiftId = null,
                kvittoFil = null,
                bilder = emptyList(),
                skapad = System.currentTimeMillis()
            )
        )

        composeTestRule.setContent {
            SammanstallningSkarm(
                poster = poster,
                forsaljningsAr = 2026,
                onForsaljningsArAndras = {},
                locale = java.util.Locale.getDefault(),
                visaSkatteInfo = true,
                onTillbaka = {}
            )
        }

        // Kontrollera att poster visas korrekt
        composeTestRule.onNodeWithText("2021").performClick()
        composeTestRule.onNodeWithText("2020").performClick()
        composeTestRule.onNodeWithText("2015").performClick()
    }
}
