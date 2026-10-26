package se.tmconnect.rostfangst.lokal

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavTest {

    private fun int(b: ByteArray, from: Int): Int =
        ByteBuffer.wrap(b.copyOfRange(from, from + 4)).order(ByteOrder.LITTLE_ENDIAN).int

    private fun kort(b: ByteArray, from: Int): Int =
        ByteBuffer.wrap(b.copyOfRange(from, from + 2)).order(ByteOrder.LITTLE_ENDIAN).short.toInt()

    @Test
    fun testPcmTillWav() {
        val pcm = ByteArray(32000) { it.toByte() }
        val wav = pcmTillWav(pcm, 16000, 1)

        assertEquals(32044, wav.size)
        assertArrayEquals("RIFF".toByteArray(Charsets.US_ASCII), wav.copyOfRange(0, 4))
        assertEquals(32036, int(wav, 4))
        assertArrayEquals("WAVE".toByteArray(Charsets.US_ASCII), wav.copyOfRange(8, 12))
        assertArrayEquals("fmt ".toByteArray(Charsets.US_ASCII), wav.copyOfRange(12, 16))
        assertEquals(16, int(wav, 16))
        assertEquals(1, kort(wav, 20))
        assertEquals(1, kort(wav, 22))
        assertEquals(16000, int(wav, 24))
        assertEquals(32000, int(wav, 28))
        assertEquals(2, kort(wav, 32))
        assertEquals(16, kort(wav, 34))
        assertArrayEquals("data".toByteArray(Charsets.US_ASCII), wav.copyOfRange(36, 40))
        assertEquals(32000, int(wav, 40))
        assertArrayEquals(pcm, wav.copyOfRange(44, wav.size))
    }

    @Test
    fun testPcmTillWavEmpty() {
        val wav = pcmTillWav(ByteArray(0), 16000, 1)

        assertEquals(44, wav.size)
        assertEquals(36, int(wav, 4))
        assertEquals(0, int(wav, 40))
    }
}
