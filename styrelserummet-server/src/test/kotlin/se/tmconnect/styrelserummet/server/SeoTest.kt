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
    fun testSitemapXmlWithBasUrl() {
        val result = sitemapXml("https://x.se")!!
        assertEquals(HttpStatusCode.OK, result.first)
        
        val xmlContent = result.second
        assertTrue(xmlContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset"))
        assertTrue(xmlContent.endsWith("</urlset>\n"))
        
        // Kontrollera att de olika sidorna finns med
        assertTrue(xmlContent.contains("  <url><loc>https://x.se</loc></url>"))
        assertTrue(xmlContent.contains("  <url><loc>https://x.se/avgift</loc></url>"))
        
        // Kontrollera att det finns exakt 8 <url>-taggar (en för varje sida i sitemapSidor)
        val urlCount = xmlContent.split("<url>").size - 1
        assertEquals(8, urlCount)
    }

    @Test
    fun testRobotsTxtNull() {
        val result = robotsTxt(null)
        assertEquals("User-agent: *\nAllow: /\nAllow: /nyckeltal\nAllow: /fondplan\nAllow: /rantor\nAllow: /protokoll\nAllow: /avgift\nAllow: /stamma\nAllow: /integritet\n", result)
    }

    @Test
    fun testRobotsTxtWithBasUrl() {
        val result = robotsTxt("https://x.se")
        assertTrue(result.endsWith("\n\nSitemap: https://x.se/sitemap.xml\n"))
    }
}
