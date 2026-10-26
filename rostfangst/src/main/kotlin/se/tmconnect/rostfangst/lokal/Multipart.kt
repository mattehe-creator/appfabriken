package se.tmconnect.rostfangst.lokal

import java.io.ByteArrayOutputStream

fun byggMultipart(
    grans: String,
    falt: List<Pair<String, String>>,
    filnamn: String,
    filtyp: String,
    fil: ByteArray,
): ByteArray {
    val ut = ByteArrayOutputStream()
    fun skriv(text: String) = ut.write(text.toByteArray(Charsets.UTF_8))

    for ((namn, varde) in falt) {
        skriv("--$grans\r\nContent-Disposition: form-data; name=\"$namn\"\r\n\r\n$varde\r\n")
    }
    skriv(
        "--$grans\r\nContent-Disposition: form-data; name=\"file\"; filename=\"$filnamn\"\r\n" +
            "Content-Type: $filtyp\r\n\r\n",
    )
    ut.write(fil)
    skriv("\r\n--$grans--\r\n")
    return ut.toByteArray()
}
