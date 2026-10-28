package se.tmconnect.styrelserummet.server

import io.ktor.http.HttpStatusCode

private val sitemapSidor = listOf(
    "/",
    "/nyckeltal",
    "/fondplan",
    "/rantor",
    "/protokoll",
    "/avgift",
    "/stamma",
    "/integritet",
)

fun robotsTxt(basUrl: String?): String {
    val rader = mutableListOf(
        "User-agent: *",
    )
    for (sida in sitemapSidor) {
        rader.add("Allow: $sida")
    }
    if (basUrl != null) {
        rader.add("")
        rader.add("Sitemap: $basUrl/sitemap.xml")
    }
    return rader.joinToString("\n") + "\n"
}

fun sitemapXml(basUrl: String?): Pair<HttpStatusCode, String>? {
    if (basUrl == null) return null
    val urlar = sitemapSidor.joinToString("\n") { sida ->
        val loc = if (sida == "/") basUrl else "$basUrl$sida"
        "  <url><loc>$loc</loc></url>"
    }
    val xml = buildString {
        appendLine("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        appendLine("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
        append(urlar)
        appendLine("</urlset>")
        appendLine()
    }
    return HttpStatusCode.OK to xml
}

fun publikBasUrl(konfig: BetalKonfig): String? = konfig.basUrl
