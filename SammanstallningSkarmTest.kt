package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.util.Locale
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

@RunWith(AndroidJUnit4::class)
class SammanstallningSkarmTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testSammanstallningSkarmVisarRattInnehall() {
        // Skapa exempelposter enligt kraven
        val poster = listOf(
            Post(
                id = 1,
                datum = LocalDate.of(2021, 6, 1),
                vad = "Reparation av kök",
                typ = PostTyp.REPARATION,
                beloppOre = 50000L,
                utforare = "Installatör AB",
                anteckning = "Köksrenovering",
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 2,
                datum = LocalDate.of(2020, 12, 31),
                vad = "Reparation av badrum",
                typ = PostTyp.REPARATION,
                beloppOre = 75000L,
                utforare = "Sanitär AB",
                anteckning = "Badrumssanering",
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            ),
            Post(
                id = 3,
                datum = LocalDate.of(2015, 3, 15),
                vad = "Ombyggnad av garage",
                typ = PostTyp.OMBYGGNAD,
                beloppOre = 200000L,
                utforare = "Bygg AB",
                anteckning = "Garagelösning",
                kvittoFil = null,
                bilder = null,
                uppgiftId = null,
                skapad = System.currentTimeMillis()
            )
        )
        
        composeTestRule.setContent {
            SammanstallningSkarm(
                poster = poster,
                forsaljningsAr = 2026,
                onForsaljningsArAndras = {},
                locale = Locale("sv", "SE"),
                visaSkatteInfo = true
            )
        }
        
        // Kontrollera att rätt texter visas
        composeTestRule.onNodeWithText("Reparation av kök").performClick()
        composeTestRule.onNodeWithText("Reparation av badrum").performClick()
        composeTestRule.onNodeWithText("Ombyggnad av garage").performClick()
        
        // Kontrollera att rätt förbättringsetiketter visas
        composeTestRule.onNodeWithText("Kräver bedömning").assertExists()
        composeTestRule.onNodeWithText("Nej").assertExists()
        composeTestRule.onNodeWithText("Ja").assertExists()
    }
}
