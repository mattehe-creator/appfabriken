package se.tmconnect.husloggen.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.data.skapaHusBildLager
import se.tmconnect.husloggen.data.skapaHusDokumentLager
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.karna.bild.BildLager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SakerhetskopiaZipTest {
    private lateinit var context: Context
    private lateinit var bildLager: BildLager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        bildLager = skapaHusBildLager(context)
    }

    @Test
    fun skrivOchLasMedBild() {
        val bildBytes = byteArrayOf(9, 8, 7)
        val filnamn = bildLager.sparaFranBytes(bildBytes, "test.jpg")
        val bostad = Bostad(id = 1, namn = "Test")
        val uppgift =
            Uppgift(
                bostadId = 1,
                namn = "U",
                intervallManader = 12,
                startDatum = LocalDate.of(2025, 1, 1),
            )
        val post =
            Post(
                bostadId = 1,
                datum = LocalDate.of(2026, 2, 2),
                vad = "Med bild",
                typ = PostTyp.UNDERHALL,
                bilder = formateraBilder(listOf(filnamn)),
            )
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                Sakerhetskopia.skriv(
                    ut,
                    listOf(bostad),
                    listOf(uppgift),
                    listOf(post),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    emptyList(),
                    bildLager,
                    skapaHusDokumentLager(context),
                )
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        assertTrue(las is SakerhetskopiaLasResult.Ok)
        val ok = las as SakerhetskopiaLasResult.Ok
        assertEquals(filnamn, ok.innehall.poster.single().bilder.single())
        assertArrayEquals(bildBytes, ok.bildFiler[filnamn])
    }

    @Test
    fun zipSlipAvvisas() {
        val zipBytes =
            ByteArrayOutputStream().use { ut ->
                ZipOutputStream(ut).use { zip ->
                    zip.putNextEntry(ZipEntry("../evil.txt"))
                    zip.write("x".toByteArray())
                    zip.closeEntry()
                }
                ut.toByteArray()
            }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        assertEquals(SakerhetskopiaLasResult.Fel.TrasigFil, las)
    }

    @Test
    fun filnamnFormat() {
        assertEquals(
            "husloggen-sakerhetskopia-2026-10-16.zip",
            sakerhetskopiaFilnamn(LocalDate.of(2026, 10, 16)),
        )
    }
}
