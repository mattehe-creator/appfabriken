package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*
import java.nio.charset.StandardCharsets

class MultipartTest {

    @Test
    fun testByggMultipart() {
        val result = byggMultipart(
            "XX",
            listOf("model" to "small", "language" to "sv"),
            "a.wav",
            "audio/wav",
            byteArrayOf(1, 2, 3)
        )
        
        val expected = "--XX\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nsmall\r\n--XX\r\nContent-Disposition: form-data; name=\"language\"\r\n\r\nsv\r\n--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\nContent-Type: audio/wav\r\n\r\n" +
                "\r\n--XX--\r\n"
        
        assertArrayEquals(expected.toByteArray(StandardCharsets.UTF_8), result)
    }

    @Test
    fun testByggMultipartTomFalt() {
        val result = byggMultipart(
            "XX",
            listOf(),
            "a.wav",
            "audio/wav",
            byteArrayOf(1, 2, 3)
        )
        
        val expected = "--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\nContent-Type: audio/wav\r\n\r\n" +
                "\r\n--XX--\r\n"
        
        assertArrayEquals(expected.toByteArray(StandardCharsets.UTF_8), result)
    }

    @Test
    fun testByggMultipartTomFil() {
        val result = byggMultipart(
            "XX",
            listOf("model" to "small"),
            "a.wav",
            "audio/wav",
            byteArrayOf()
        )
        
        val expected = "--XX\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nsmall\r\n--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\nContent-Type: audio/wav\r\n\r\n" +
                "\r\n--XX--\r\n"
        
        assertArrayEquals(expected.toByteArray(StandardCharsets.UTF_8), result)
    }

    @Test
    fun testByggMultipartSpecialChars() {
        val result = byggMultipart(
            "XX",
            listOf("model" to "small", "language" to "svåö"),
            "a.wav",
            "audio/wav",
            byteArrayOf(1, 2, 3)
        )
        
        val expected = "--XX\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nsmall\r\n--XX\r\nContent-Disposition: form-data; name=\"language\"\r\n\r\nsvåö\r\n--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\nContent-Type: audio/wav\r\n\r\n" +
                "\r\n--XX--\r\n"
        
        assertArrayEquals(expected.toByteArray(StandardCharsets.UTF_8), result)
    }
}
