package se.tmconnect.rostfangst.lokal

import java.nio.charset.StandardCharsets

fun byggMultipart(
    grans: String,
    falt: List<Pair<String, String>>,
    filnamn: String,
    filtyp: String,
    fil: ByteArray
): ByteArray {
    val sb = StringBuilder()
    
    // Lägg till fälten
    for ((namn, värde) in falt) {
        sb.append("--").append(grans).append("\r\n")
        sb.append("Content-Disposition: form-data; name=\"").append(namn).append("\"\r\n\r\n")
        sb.append(värde).append("\r\n")
    }
    
    // Lägg till filen
    sb.append("--").append(grans).append("\r\n")
    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(filnamn).append("\"\r\n")
    sb.append("Content-Type: ").append(filtyp).append("\r\n\r\n")
    
    // Lägg till filens innehåll
    sb.append("\r\n")
    
    // Lägg till slutet
    sb.append("--").append(grans).append("--\r\n")
    
    val header = sb.toString().toByteArray(StandardCharsets.UTF_8)
    val resultat = ByteArray(header.size + fil.size)
    System.arraycopy(header, 0, resultat, 0, header.size)
    System.arraycopy(fil, 0, resultat, header.size, fil.size)
    
    return resultat
}
