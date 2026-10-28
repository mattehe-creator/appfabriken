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
    fun testSitemapXmlSuccess() {
        val result = sitemapXml("https://x.se")!!
        assertEquals(HttpStatusCode.OK, result.first)
        val xmlContent = result.second
        assertTrue(xmlContent.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset"))
        assertTrue(xmlContent.endsWith("</urlset>\n"))
        
        // Kontrollera att XML-deklarationen är först
        val lines = xmlContent.lines()
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", lines[0])
        
        // Kontrollera att det finns 8 url-element (en per sida i sitemapSidor)
        val urlCount = xmlContent.count { it == '<' && xmlContent.substringAfter(it, "").startsWith("/url>") }
        assertEquals(8, urlCount)
        
        // Kontrollera specifika URL:er
        assertTrue(xmlContent.contains("  <url><loc>https://x.se</loc></url>"))
        assertTrue(xmlContent.contains("  <url><loc>https://x.se/avgift</loc></url>"))
    }

    @Test
    fun testRobotsTxtNull() {
        val result = robotsTxt(null)
        assertEquals("User-agent: *\nAllow: /\nAllow: /nyckeltal\nAllow: /fondplan\nAllow: /rantor\nAllow: /protokoll\nAllow: /avgift\nAllow: /stamma\nAllow: /integritet\n", result)
    }

    @Test
    fun testRobotsTxtWithBaseUrl() {
        val result = robotsTxt("https://x.se")
        assertTrue(result.endsWith("\n\nSitemap: https://x.se/sitemap.xml\n"))
        
        // Kontrollera att alla sidor är tillåtna
        val lines = result.lines()
        assertEquals("User-agent: *", lines[0])
        assertEquals("Allow: /", lines[1])
        assertEquals("Allow: /nyckeltal", lines[2])
        assertEquals("Allow: /fondplan", lines[3])
        assertEquals("Allow: /rantor", lines[4])
        assertEquals("Allow: /protokoll", lines[5])
        assertEquals("Allow: /avgift", lines[6])
        assertEquals("Allow: /stamma", lines[7])
        assertEquals("Allow: /integritet", lines[8])
        assertEquals("", lines[9])
        assertEquals("Sitemap: https://x.se/sitemap.xml", lines[10])
    }
}
