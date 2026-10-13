package se.tmconnect.garantivalvet.tillganglighet

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Varje [androidx.compose.material3.Icon] och IconButton i main-källan ska ha
 * contentDescription från strings eller null med motiverande kommentar.
 */
class IkonContentDescriptionTest {
    @Test
    fun ikonerHarBeskrivningEllerDekorativKommentar() {
        val rot = File("src/main/kotlin/se/tmconnect/garantivalvet/ui")
        val fel = rot.walkTopDown()
            .filter { it.extension == "kt" }
            .flatMap { fil -> kontrolleraFil(fil) }
            .toList()
        assertTrue(fel.joinToString("\n"), fel.isEmpty())
    }

    private fun kontrolleraFil(fil: File): List<String> {
        val text = fil.readText()
        if (!text.contains("Icon(") && !text.contains("IconButton(")) {
            return emptyList()
        }
        val fel = mutableListOf<String>()
        val iconRegex = Regex("""Icon\s*\(""")
        iconRegex.findAll(text).forEach { match ->
            val start = match.range.first
            val snippet = text.substring(start, minOf(text.length, start + 600))
            when {
                snippet.contains("contentDescription = stringResource") -> Unit
                snippet.contains("contentDescription = null") &&
                    snippet.contains("// Dekorativ") -> Unit
                snippet.contains("contentDescription = null") ->
                    fel.add("${fil.path}: Icon med null utan dekorativ-kommentar")
                else ->
                    fel.add("${fil.path}: Icon saknar contentDescription")
            }
        }
        return fel
    }
}
