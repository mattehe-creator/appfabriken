package se.tmconnect.rostfangst.lokal

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class MultipartTest {

    private fun text(s: String) = s.toByteArray(Charsets.UTF_8)

    @Test
    fun exempletGerByteForByteRattKropp() {
        val ut = byggMultipart(
            "XX",
            listOf("model" to "small", "language" to "sv"),
            "a.wav",
            "audio/wav",
            byteArrayOf(1, 2, 3),
        )
        val forvantat = text(
            "--XX\r\nContent-Disposition: form-data; name=\"model\"\r\n\r\nsmall\r\n" +
                "--XX\r\nContent-Disposition: form-data; name=\"language\"\r\n\r\nsv\r\n" +
                "--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\n" +
                "Content-Type: audio/wav\r\n\r\n",
        ) + byteArrayOf(1, 2, 3) + text("\r\n--XX--\r\n")
        assertArrayEquals(forvantat, ut)
    }

    @Test
    fun tomFaltlistaBorjarMedFilavsnittet() {
        val ut = byggMultipart("XX", emptyList(), "a.wav", "audio/wav", byteArrayOf(9))
        val forvantat = text(
            "--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\n" +
                "Content-Type: audio/wav\r\n\r\n",
        ) + byteArrayOf(9) + text("\r\n--XX--\r\n")
        assertArrayEquals(forvantat, ut)
    }

    @Test
    fun tomFilGerTomNyttolast() {
        val ut = byggMultipart("XX", emptyList(), "a.wav", "audio/wav", ByteArray(0))
        val forvantat = text(
            "--XX\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.wav\"\r\n" +
                "Content-Type: audio/wav\r\n\r\n" + "\r\n--XX--\r\n",
        )
        assertArrayEquals(forvantat, ut)
    }

    @Test
    fun vardeKodasSomUtf8() {
        val med = byggMultipart("XX", listOf("t" to "åäö"), "a", "x", ByteArray(0))
        val utan = byggMultipart("XX", listOf("t" to "abc"), "a", "x", ByteArray(0))
        // åäö är 6 byte i UTF-8, abc är 3 byte.
        assertEquals(utan.size + 3, med.size)
    }
}
