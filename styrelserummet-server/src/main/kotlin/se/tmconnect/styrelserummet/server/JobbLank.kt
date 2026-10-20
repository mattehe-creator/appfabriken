package se.tmconnect.styrelserummet.server

import java.security.SecureRandom

private val random = SecureRandom()

/** 128-bitars slumpat jobb-id som hex (32 tecken). */
fun genereraJobbId(): String {
    val bytes = ByteArray(16)
    random.nextBytes(bytes)
    return bytes.joinToString("") { "%02x".format(it) }
}
