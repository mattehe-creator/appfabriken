package se.tmconnect.styrelserummet.regler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class KontrastTest {

    @Test
    fun kontrastforhallandeExempel() {
        assertEquals(21.0, kontrastforhallande("#000000", "#ffffff"), 0.001)
        assertEquals(21.0, kontrastforhallande("#fff", "#000"), 0.001)
        assertEquals(4.5, kontrastforhallande("#777777", "#ffffff"), 0.1)
    }

    @Test
    fun cssVariablerLjustOchMorktLage() {
        val cssContent = readCssFile()

        val lightModeVars = parseLightModeVariables(cssContent)
        val darkModeVars = parseDarkModeVariables(cssContent)

        assertKontrastkrav(lightModeVars, "ljust läge")
        assertKontrastkrav(darkModeVars, "mörkt läge")
    }

    @Test
    fun saknadVariabelGerTydligtFel() {
        val cssContent = """
            :root {
                --bg: #f3f5f2;
            }
        """.trimIndent()

        val vars = parseLightModeVariables(cssContent)
        try {
            requireVariable(vars, "--text")
            fail("Förväntade undantag för saknad variabel")
        } catch (e: IllegalStateException) {
            assertTrue(
                "Felmeddelandet ska nämna variabelnamnet: ${e.message}",
                e.message!!.contains("--text"),
            )
        }
    }

    private fun assertKontrastkrav(vars: Map<String, String>, label: String) {
        val text = requireVariable(vars, "--text")
        val bg = requireVariable(vars, "--bg")
        val dampad = requireVariable(vars, "--dampad")
        val accent = requireVariable(vars, "--accent")
        val accentText = requireVariable(vars, "--accent-text")

        assertMinstKontrast(text, bg, 4.5, "$label: --text mot --bg")
        assertMinstKontrast(dampad, bg, 4.5, "$label: --dampad mot --bg")
        assertMinstKontrast(accentText, accent, 4.5, "$label: --accent-text mot --accent")
        assertMinstKontrast(accent, bg, 3.0, "$label: --accent mot --bg (knappkant)")
    }

    private fun assertMinstKontrast(fg: String, bg: String, minimum: Double, beskrivning: String) {
        val ratio = kontrastforhallande(fg, bg)
        assertTrue(
            "$beskrivning: kontrast $ratio under $minimum",
            ratio >= minimum,
        )
    }

    private fun readCssFile(): String {
        val resource = javaClass.classLoader.getResource("static/gemensam.css")
            ?: throw IllegalStateException("Kunde inte hitta static/gemensam.css på classpath")
        return resource.readText()
    }

    private fun parseLightModeVariables(cssContent: String): Map<String, String> {
        val match = Regex("""(?m)^:root\s*\{([^}]*)\}""")
            .find(cssContent)
            ?: throw IllegalStateException("Kunde inte hitta :root-block i CSS")
        return parseVariableBlock(match.groupValues[1])
    }

    private fun parseDarkModeVariables(cssContent: String): Map<String, String> {
        val match = Regex(
            """@media\s*\(\s*prefers-color-scheme:\s*dark\s*\)\s*\{[^{]*:root:not\([^)]*\)\s*\{([^}]*)\}""",
            RegexOption.DOT_MATCHES_ALL,
        ).find(cssContent)
            ?: throw IllegalStateException("Kunde inte hitta mörkt läge (@media prefers-color-scheme: dark) i CSS")
        return parseVariableBlock(match.groupValues[1])
    }

    private fun parseVariableBlock(block: String): Map<String, String> {
        val variables = mutableMapOf<String, String>()
        val variableRegex = Regex("""(--[\w-]+)\s*:\s*([^;]+);""")
        variableRegex.findAll(block).forEach { match ->
            variables[match.groupValues[1]] = match.groupValues[2].trim()
        }
        return variables
    }

    private fun requireVariable(vars: Map<String, String>, name: String): String {
        if (!vars.containsKey(name)) {
            throw IllegalStateException("CSS-variabeln $name saknas i :root")
        }
        return vars.getValue(name)
    }
}
