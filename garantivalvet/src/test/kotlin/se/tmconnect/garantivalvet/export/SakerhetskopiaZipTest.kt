package se.tmconnect.garantivalvet.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.data.KvittoLager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SakerhetskopiaZipTest {
    private lateinit var kvittoLager: KvittoLager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        kvittoLager = KvittoLager(context)
    }

    @Test
    fun skrivOchLasMedKvitto() {
        val kvittoBytes = byteArrayOf(1, 2, 3, 4, 5)
        val filnamn = kvittoLager.sparaFranBytes(kvittoBytes, "test.jpg")
        val kop =
            Kop(
                vad = "Med kvitto",
                kopdatum = LocalDate.of(2024, 5, 5),
                garantiManader = 12,
                kvittoFil = filnamn,
            )
        val zipBytes = ByteArrayOutputStream().use { ut ->
            Sakerhetskopia.skriv(ut, listOf(kop), kvittoLager)
            ut.toByteArray()
        }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        assertTrue(las is SakerhetskopiaLasResult.Ok)
        val ok = las as SakerhetskopiaLasResult.Ok
        assertEquals(1, ok.innehall.kop.size)
        assertEquals(filnamn, ok.innehall.kop.single().kvittoFil)
        assertArrayEquals(kvittoBytes, ok.kvittoFiler[filnamn])
    }

    @Test
    fun zipSlipAvvisas() {
        val zipBytes = ByteArrayOutputStream().use { ut ->
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
    fun saknarKopJson() {
        val zipBytes = ByteArrayOutputStream().use { ut ->
            ZipOutputStream(ut).use { zip ->
                zip.putNextEntry(ZipEntry("annat.txt"))
                zip.write("hej".toByteArray())
                zip.closeEntry()
            }
            ut.toByteArray()
        }
        val las = ByteArrayInputStream(zipBytes).use { Sakerhetskopia.lasIn(it) }
        assertEquals(SakerhetskopiaLasResult.Fel.SaknarKopJson, las)
    }

    @Test
    fun filnamnFormat() {
        assertEquals(
            "garantivalvet-sakerhetskopia-2026-10-11.zip",
            sakerhetskopiaFilnamn(LocalDate.of(2026, 10, 11)),
        )
    }
}
