package se.tmconnect.husloggen

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

/**
 * Fångar hårdkodade användarsynliga strängar och temafärger i UI-lager
 * (uppdrag 2026-10-25-husloggen-tillganglighet).
 */
class TillganglighetHardkodadeStrangarTest {
    @Test
    fun ingaHardkodadeUiStrangarEllerFarger() {
        val rot = File("src/main/kotlin/se/tmconnect/husloggen")
        val globs = listOf(
            "ui/**/*.kt",
            "widget/**/*.kt",
            "MainActivity.kt",
        )
        val monster = listOf(
            Regex("""Text\s*\(\s*""" + '"'),
            Regex("""contentDescription\s*=\s*""" + '"'),
            Regex("""Color\s*\(\s*0x[0-9A-Fa-f]+"""),
        )
        val träffar = mutableListOf<String>()
        for (glob in globs) {
            for (fil in matchGlob(rot.toPath(), glob)) {
                val rader = fil.readLines()
                rader.forEachIndexed { index, rad ->
                    val trim = rad.trim()
                    if (trim.startsWith("//")) return@forEachIndexed
                    monster.forEach { regex ->
                        if (regex.containsMatchIn(rad)) {
                            träffar.add("${fil.relativeTo(rot.parentFile.parentFile.parentFile)}:${index + 1}: $trim")
                        }
                    }
                }
            }
        }
        assertTrue(
            "Hårdkodade UI-strängar eller Color(0x…): ${träffar.joinToString("\n")}",
            träffar.isEmpty(),
        )
    }

    private fun matchGlob(root: Path, glob: String): List<File> {
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$glob")
        if (!Files.exists(root)) return emptyList()
        return Files.walk(root)
            .filter { Files.isRegularFile(it) && matcher.matches(root.relativize(it)) }
            .map { it.toFile() }
            .toList()
    }
}
