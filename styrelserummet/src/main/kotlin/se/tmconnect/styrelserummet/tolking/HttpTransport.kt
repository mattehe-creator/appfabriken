package se.tmconnect.styrelserummet.tolking

data class HttpSvar(
    val statusKod: Int,
    val kropp: String,
)

fun interface HttpTransport {
    fun post(url: String, headers: Map<String, String>, body: String): HttpSvar
}

class JavaHttpTransport(
    private val client: java.net.http.HttpClient = java.net.http.HttpClient.newHttpClient(),
) : HttpTransport {
    override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
        val builder = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
        headers.forEach { (nyckel, varde) -> builder.header(nyckel, varde) }
        val request = builder.build()
        val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        return HttpSvar(response.statusCode(), response.body())
    }
}
