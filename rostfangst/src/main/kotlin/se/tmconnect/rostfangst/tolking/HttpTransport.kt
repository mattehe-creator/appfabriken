package se.tmconnect.rostfangst.tolking

data class HttpSvar(
    val statusKod: Int,
    val kropp: String,
)

fun interface HttpTransport {
    fun post(url: String, headers: Map<String, String>, body: String): HttpSvar
}

class HttpURLConnectionTransport : HttpTransport {
    override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
        val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.connectTimeout = 30_000
        connection.readTimeout = 60_000
        headers.forEach { (nyckel, varde) -> connection.setRequestProperty(nyckel, varde) }
        connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val kod = connection.responseCode
        val stream = if (kod in 200..299) connection.inputStream else connection.errorStream
        val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        connection.disconnect()
        return HttpSvar(kod, text)
    }
}
