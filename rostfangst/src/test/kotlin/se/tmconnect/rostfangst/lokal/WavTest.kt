package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavTest {

    @Test
    fun testPcmTillWav() {
        // Test with 1 second of audio (16000 Hz, 1 channel)
        val pcm = ByteArray(32000) { it.toByte() } // 32000 bytes of PCM data
        val wav = pcmTillWav(pcm, 16000, 1)

        // Check header size
        assertEquals(32044, wav.size)

        // Check RIFF header
        assertArrayEquals("RIFF".toByteArray(Charsets.US_ASCII), wav.copyOfRange(0, 4))
        
        // Check file size (should be 36 + pcm.size = 32036)
        val fileSize = ByteBuffer.wrap(wav.copyOfRange(4, 8)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(32036, fileSize)

        assertArrayEquals("WAVE".toByteArray(Charsets.US_ASCII), wav.copyOfRange(8, 12))
        assertArrayEquals("fmt ".toByteArray(Charsets.US_ASCII), wav.copyOfRange(12, 16))

        // Check format size
        val formatSize = ByteBuffer.wrap(wav.copyOfRange(16, 20)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(16, formatSize)

        // Check audio format (PCM)
        val audioFormat = ByteBuffer.wrap(wav.copyOfRange(20, 22)).order(ByteOrder.LITTLE_ENDIAN).short
        assertEquals(1, audioFormat)

        // Check number of channels
        val channels = ByteBuffer.wrap(wav.copyOfRange(22, 24)).order(ByteOrder.LITTLE_ENDIAN).short
        assertEquals(1, channels)

        // Check sample rate
        val sampleRate = ByteBuffer.wrap(wav.copyOfRange(24, 28)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(16000, sampleRate)

        // Check byte rate
        val byteRate = ByteBuffer.wrap(wav.copyOfRange(28, 32)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(32000, byteRate)

        // Check block align
        val blockAlign = ByteBuffer.wrap(wav.copyOfRange(32, 34)).order(ByteOrder.LITTLE_ENDIAN).short
        assertEquals(2, blockAlign)

        // Check bits per sample
        val bitsPerSample = ByteBuffer.wrap(wav.copyOfRange(34, 36)).order(ByteOrder.LITTLE_ENDIAN).short
        assertEquals(16, bitsPerSample)

        assertArrayEquals("data".toByteArray(Charsets.US_ASCII), wav.copyOfRange(36, 40))

        // Check data size
        val dataSize = ByteBuffer.wrap(wav.copyOfRange(40, 44)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(32000, dataSize)

        // Check that PCM data is copied correctly
        assertArrayEquals(pcm, wav.copyOfRange(44, wav.size))
    }

    @Test
    fun testPcmTillWavEmpty() {
        val pcm = ByteArray(0)
        val wav = pcmTillWav(pcm, 16000, 1)

        // Should be exactly 44 bytes (header only)
        assertEquals(44, wav.size)

        // Check file size in header
        val fileSize = ByteBuffer.wrap(wav.copyOfRange(4, 8)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(36, fileSize) // 36 + 0

        // Check data size in header
        val dataSize = ByteBuffer.wrap(wav.copyOfRange(40, 44)).order(ByteOrder.LITTLE_ENDIAN).int
        assertEquals(0, dataSize)
    }
}
