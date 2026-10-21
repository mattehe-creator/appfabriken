package se.tmconnect.styrelserummet.server

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.writeText

class JobbLagringTest {
    private lateinit var tempDir: java.nio.file.Path

    @Before
    fun setUp() {
        tempDir = createTempDirectory("jobb_lagring_test")
    }

    @After
    fun tearDown() {
        Files.walk(tempDir)
            .sorted(Comparator.reverseOrder())
            .forEach { it.toFile().delete() }
    }

    // ... rest of the file remains unchanged
}
