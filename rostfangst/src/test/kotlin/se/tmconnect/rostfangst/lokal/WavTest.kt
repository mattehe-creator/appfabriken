package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavTest {
    
    @Test
    fun testPcmTillWav() {
        // Test with 1 second of audio (16000 Hz, 1 channel)
        val pcm = ByteArray(32000) { it.toByte() } // 32000 bytes of sample data
        val result = pcmTillWav(pcm, 16000, 1)
        
        assertEquals(32044, result.size)
        
        val buffer = ByteBuffer.wrap(result)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        
        // Check RIFF header
        assertEquals("RIFF", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(32036, buffer.int) // File size minus 8 bytes
        
        // Check WAVE header
        assertEquals("WAVE", String(byteArrayFromBuffer(buffer, 4)))
        
        // Check fmt chunk
        assertEquals("fmt ", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(16, buffer.int) // Format size
        assertEquals(1, buffer.short) // PCM format
        assertEquals(1, buffer.short) // Channels
        assertEquals(16000, buffer.int) // Sample rate
        assertEquals(32000, buffer.int) // Byte rate
        assertEquals(2, buffer.short) // Block align
        assertEquals(16, buffer.short) // Bits per sample
        
        // Check data chunk
        assertEquals("data", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(32000, buffer.int) // Data size
        
        // Check that PCM data is unchanged
        val pcmData = ByteArray(32000)
        buffer.get(pcmData)
        assertArrayEquals(pcm, pcmData)
    }
    
    @Test
    fun testPcmTillWavEmpty() {
        // Test with empty PCM data
        val pcm = ByteArray(0)
        val result = pcmTillWav(pcm, 16000, 1)
        
        assertEquals(44, result.size)
        
        val buffer = ByteBuffer.wrap(result)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        
        // Check RIFF header
        assertEquals("RIFF", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(36, buffer.int) // File size minus 8 bytes
        
        // Check WAVE header
        assertEquals("WAVE", String(byteArrayFromBuffer(buffer, 4)))
        
        // Check fmt chunk
        assertEquals("fmt ", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(16, buffer.int) // Format size
        assertEquals(1, buffer.short) // PCM format
        assertEquals(1, buffer.short) // Channels
        assertEquals(16000, buffer.int) // Sample rate
        assertEquals(32000, buffer.int) // Byte rate
        assertEquals(2, buffer.short) // Block align
        assertEquals(16, buffer.short) // Bits per sample
        
        // Check data chunk
        assertEquals("data", String(byteArrayFromBuffer(buffer, 4)))
        assertEquals(0, buffer.int) // Data size
    }
    
    private fun byteArrayFromBuffer(buffer: ByteBuffer, size: Int): ByteArray {
        val array = ByteArray(size)
        buffer.get(array)
        return array
    }
}
