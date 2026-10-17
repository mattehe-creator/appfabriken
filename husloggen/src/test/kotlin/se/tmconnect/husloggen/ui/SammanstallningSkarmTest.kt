package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Locale
import org.junit.Rule
import org.junit.Test
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

class SammanstallningSkarmTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSammanstallningSkarmVisarRattForbattring() {
        // Skapa exempelposter enligt uppgiften
        val poster = listOf(
            Post(
                id = 1,
                datum = java.time.LocalDate.of(2021, 6, 1),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 50000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 2,
                datum = java.time.LocalDate.of(2020, 12, 31),
                vad = "Reparation",
                typ = PostTyp.REPARATION,
                beloppOre = 50000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 3,
                datum = java.time.LocalDate.of(2015, 1, 1),
                vad = "Ombyggnad",
                typ = PostTyp.OMBYGGNAD,
                beloppOre = 50000L,
                utforare = null,
                anteckning = null,
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            )
        )

        composeTestRule.setContent {
            // Använd svenska locale som i testet
            val locale = Locale("sv", "SE")
            
            Box(modifier = Modifier.fillMaxSize()) {
                SammanstallningSkarm(
                    poster = poster,
                    forsaljningsAr = 2026,
                    onForsaljningsArAndras = {},
                    locale = locale,
                    visaSkatteInfo = true
                )
            }
        }

        // Kontrollera att rätt text visas för reparation 2021-06-01 (Kräver bedömning)
        composeTestRule.onNodeWithText("Kräver bedömning").assertExists()
        
        // Kontrollera att rätt text visas för reparation 2020-12-31 (Nej)
        composeTestRule.onNodeWithText("Nej").assertExists()
        
        // Kontrollera att rätt text visas för ombyggnad 2015 (Ja)
        composeTestRule.onNodeWithText("Ja").assertExists()
    }
}
