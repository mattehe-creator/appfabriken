package se.tmconnect.rostfangst.lokal

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun pcmTillWav(pcm: ByteArray, samplingsfrekvens: Int, kanaler: Int): ByteArray {
    val headerSize = 44
    val dataSize = pcm.size
    val totalSize = headerSize + dataSize
    
    val buffer = ByteBuffer.allocate(totalSize)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    
    // RIFF header
    buffer.put("RIFF".toByteArray())
    buffer.putInt(totalSize - 8) // File size minus 8 bytes
    buffer.put("WAVE".toByteArray())
    
    // fmt chunk
    buffer.put("fmt ".toByteArray()) // Note the space after fmt
    buffer.putInt(16) // Format size
    buffer.short(1) // PCM format
    buffer.short(kanaler.toShort())
    buffer.putInt(samplingsfrekvens)
    buffer.putInt(samplingsfrekvens * kanaler * 2) // Byte rate
    buffer.short((kanaler * 2).toShort()) // Block align
    buffer.short(16) // Bits per sample
    
    // data chunk
    buffer.put("data".toByteArray())
    buffer.putInt(dataSize)
    
    // PCM data
    buffer.put(pcm)
    
    return buffer.array()
}
