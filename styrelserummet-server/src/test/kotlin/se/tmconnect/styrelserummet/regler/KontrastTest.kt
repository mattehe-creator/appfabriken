package se.tmconnect.styrelserummet.regler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals

class KontrastTest {
    
    @Test
    fun `test kontrastforhallande`() {
        // Testa med standardvärden
        assertEquals(21.0, kontrastforhallande("#000000", "#ffffff"))
        assertEquals(21.0, kontrastforhallande("#fff", "#000"))
        
        // Testa med värden från uppgiften
        assertEquals(4.5, kontrastforhallande("#777777", "#ffffff"))
    }
    
    @Test
    fun `test css variabler`() {
        val cssContent = readCssFile()
        
        // Läs ljust läge variabler
        val lightModeVars = parseCssVariables(cssContent, false)
        
        // Kontrollera att alla nödvändiga variabler finns
        checkVariableExists(lightModeVars, "--text", "text")
        checkVariableExists(lightModeVars, "--bg", "background")
        checkVariableExists(lightModeVars, "--dampad", "dampad")
        checkVariableExists(lightModeVars, "--accent", "accent")
        checkVariableExists(lightModeVars, "--accent-text", "accent text")
        
        // Testa kontrastförhållanden för ljust läge
        val textBgContrast = kontrastforhallande(lightModeVars["--text"]!!, lightModeVars["--bg"]!!)
        assertEquals(4.5, textBgContrast, 0.1)
        
        val dampadBgContrast = kontrastforhallande(lightModeVars["--dampad"]!!, lightModeVars["--bg"]!!)
        assertEquals(4.5, dampadBgContrast, 0.1)
        
        val accentTextAccentContrast = kontrastforhallande(lightModeVars["--accent-text"]!!, lightModeVars["--accent"]!!)
        assertEquals(4.5, accentTextAccentContrast, 0.1)
        
        // Sekundär knappkant: --accent mot --bg
        val accentBgContrast = kontrastforhallande(lightModeVars["--accent"]!!, lightModeVars["--bg"]!!)
        assertEquals(3.0, accentBgContrast, 0.1)
        
        // Läs mörkt läge variabler
        val darkModeVars = parseCssVariables(cssContent, true)
        
        // Kontrollera att alla nödvändiga variabler finns i mörkt läge också
        checkVariableExists(darkModeVars, "--text", "text")
        checkVariableExists(darkModeVars, "--bg", "background")
        checkVariableExists(darkModeVars, "--dampad", "dampad")
        checkVariableExists(darkModeVars, "--accent", "accent")
        checkVariableExists(darkModeVars, "--accent-text", "accent text")
        
        // Testa kontrastförhållanden för mörkt läge
        val darkTextBgContrast = kontrastforhallande(darkModeVars["--text"]!!, darkModeVars["--bg"]!!)
        assertEquals(4.5, darkTextBgContrast, 0.1)
        
        val darkDampadBgContrast = kontrastforhallande(darkModeVars["--dampad"]!!, darkModeVars["--bg"]!!)
        assertEquals(4.5, darkDampadBgContrast, 0.1)
        
        val darkAccentTextAccentContrast = kontrastforhallande(darkModeVars["--accent-text"]!!, darkModeVars["--accent"]!!)
        assertEquals(4.5, darkAccentTextAccentContrast, 0.1)
        
        // Sekundär knappkant: --accent mot --bg
        val darkAccentBgContrast = kontrastforhallande(darkModeVars["--accent"]!!, darkModeVars["--bg"]!!)
        assertEquals(3.0, darkAccentBgContrast, 0.1)
    }
    
    @Test
    fun `test saknade variabler`() {
        val cssContent = """
            :root {
                --bg: #f3f5f2;
                --yta: #ffffff;
                --text: #1b2629;
                --dampad: #55636a;
                --linje: #cfd8d6;
                --accent: #1d6a66;
                --accent-text: #ffffff;
                --stampel: #b8860b;
                --stampel-bg: #fbf3dc;
                --fel: #a3322a;
                --varning: #8a5a00;
            }
        """.trimIndent()
        
        val vars = parseCssVariables(cssContent, false)
        
        // Testa att vi får ett fel när en variabel saknas
        assertThrows<IllegalStateException> {
            checkVariableExists(vars, "--missing", "missing")
        }
    }
    
    private fun readCssFile(): String {
        try {
            // Försök läsa från classpath
            val resource = this::class.java.classLoader.getResource("static/gemensam.css")
            
            if (resource != null) {
                return resource.readText()
            }
        } catch (e: Exception) {
            // Ignorera och försök med annan metod
        }
        
        try {
            // Försök läsa direkt från filsystemet
            val file = File("main/resources/static/gemensam.css")
            if (file.exists()) {
                return file.readText()
            }
        } catch (e: Exception) {
            // Ignorera och kasta ett tydligt felmeddelande
        }
        
        try {
            // Försök med relativ sökväg från testkatalogen
            val file = File("src/main/resources/static/gemensam.css")
            if (file.exists()) {
                return file.readText()
            }
        } catch (e: Exception) {
            // Ignorera och kasta ett tydligt felmeddelande
        }
        
        throw IllegalStateException("Kunde inte hitta filen static/gemensam.css i classpath eller i src/main/resources")
    }
    
    private fun parseCssVariables(cssContent: String, darkMode: Boolean): Map<String, String> {
        val rootRegex = if (darkMode) {
            Regex(":root\\[data-theme=\"light\"\\] \\{([^}]+)\\}")
        } else {
            Regex(":root \\{([^}]+)\\}")
        }
        
        val match = rootRegex.find(cssContent)
            ?: throw IllegalStateException("Kunde inte hitta :root block i CSS")
        
        val variables = mutableMapOf<String, String>()
        val variableRegex = Regex("--(\\w+):\\s*([^;]+);")
        
        variableRegex.findAll(match.groupValues[1]).forEach { 
            variables[it.groupValues[1]] = it.groupValues[2].trim()
        }
        
        return variables
    }
    
    private fun checkVariableExists(vars: Map<String, String>, varName: String, description: String) {
        if (!vars.containsKey(varName)) {
            throw IllegalStateException("CSS-variabeln $varName saknas i :root. ($description)")
        }
    }
}
