package se.tmconnect.styrelserummet.server

import io.ktor.http.HttpStatusCode
import org.junit.Test
import org.junit.Assert.*

class SeoTest {

    @Test
    fun testSitemapXmlNull() {
        assertNull(sitemapXml(null))
    }

    @Test
    fun testSitemapXml() {
        val result = sitemapXml("https://x.se")!!
        assertEquals(HttpStatusCode.OK, result.first)
        val content = result.second
        // Let's debug what we actually get
        println("Actual content start: '${content.take(100)}'")
        assertTrue(content.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"))
        assertTrue(content.endsWith("</urlset>\n"))
        
        // Check that the content contains all expected URLs
        assertTrue(content.contains("  <url><loc>https://x.se</loc></url>"))
        assertTrue(content.contains("  <url><loc>https://x.se/avgift</loc></url>"))
        
        // Count occurrences of <url> tag (should be 8)
        val urlCount = content.split("<url>").size - 1
        assertEquals(8, urlCount)
    }

    @Test
    fun testRobotsTxtNull() {
        val result = robotsTxt(null)
        val expected = "User-agent: *\nAllow: /\nAllow: /nyckeltal\nAllow: /fondplan\nAllow: /rantor\nAllow: /protokoll\nAllow: /avgift\nAllow: /stamma\nAllow: /integritet\n"
        assertEquals(expected, result)
    }

    @Test
    fun testRobotsTxt() {
        val result = robotsTxt("https://x.se")
        assertTrue(result.endsWith("\n\nSitemap: https://x.se/sitemap.xml\n"))
    }
}
