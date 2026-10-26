package se.tmconnect.rostfangst.lokal

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun pcmTillWav(pcm: ByteArray, samplingsfrekvens: Int, kanaler: Int): ByteArray {
    val bos = ByteArrayOutputStream()
    
    // RIFF header
    bos.write("RIFF".toByteArray(Charsets.US_ASCII))
    
    // File size (4 bytes) - 36 + pcm.size
    val fileSize = 36 + pcm.size
    bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(fileSize).array())
    
    bos.write("WAVE".toByteArray(Charsets.US_ASCII))
    bos.write("fmt ".toByteArray(Charsets.US_ASCII)) // Note the space after fmt
    
    // Format size (4 bytes) - 16 for PCM
    bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(16).array())
    
    // Audio format (2 bytes) - 1 for PCM
    bos.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(1.toShort()).array())
    
    // Number of channels (2 bytes)
    bos.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(kanaler.toShort()).array())
    
    // Sample rate (4 bytes)
    bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(samplingsfrekvens).array())
    
    // Byte rate (4 bytes) - samplingsfrekvens * kanaler * 2
    val byteRate = samplingsfrekvens * kanaler * 2
    bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(byteRate).array())
    
    // Block align (2 bytes) - kanaler * 2
    val blockAlign = kanaler * 2
    bos.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(blockAlign.toShort()).array())
    
    // Bits per sample (2 bytes) - 16
    bos.write(ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(16.toShort()).array())
    
    bos.write("data".toByteArray(Charsets.US_ASCII))
    
    // Data size (4 bytes)
    bos.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(pcm.size).array())
    
    val header = bos.toByteArray()
    
    val result = ByteArray(header.size + pcm.size)
    System.arraycopy(header, 0, result, 0, header.size)
    System.arraycopy(pcm, 0, result, header.size, pcm.size)
    
    return result
}
