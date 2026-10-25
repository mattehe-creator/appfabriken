package se.tmconnect.rostfangst.wear

import java.nio.charset.StandardCharsets

fun kodaUtfor(id: Long): ByteArray {
    if (id <= 0) return ByteArray(0)
    return id.toString().toByteArray(StandardCharsets.UTF_8)
}

fun avkodaUtfor(data: ByteArray): Long? {
    val str = data.toString(StandardCharsets.UTF_8).trim()
    if (str.isEmpty()) return null
    
    return try {
        val id = str.toLong()
        if (id > 0) id else null
    } catch (e: NumberFormatException) {
        null
    }
}
