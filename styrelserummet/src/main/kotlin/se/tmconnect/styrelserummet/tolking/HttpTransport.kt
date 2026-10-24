package se.tmconnect.styrelserummet.tolking

data class HttpSvar(
    val statusKod: Int,
    val kropp: String,
)

fun interface HttpTransport {
    fun post(url: String, headers: Map<String, String>, body: String): HttpSvar
}

class JavaHttpTransport(
    private val client: java.net.http.HttpClient =
        java.net.http.HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(20)).build(),
    /** Ett svar på en lång underhållsplan kan ta ett par minuter; längre än så är ett fel. */
    private val tidsgrans: java.time.Duration = java.time.Duration.ofMinutes(5),
) : HttpTransport {
    override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
        val builder = java.net.http.HttpRequest.newBuilder()
            .uri(java.net.URI.create(url))
            .timeout(tidsgrans)
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body))
        headers.forEach { (nyckel, varde) -> builder.header(nyckel, varde) }
        val request = builder.build()
        val response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        return HttpSvar(response.statusCode(), response.body())
    }
}
