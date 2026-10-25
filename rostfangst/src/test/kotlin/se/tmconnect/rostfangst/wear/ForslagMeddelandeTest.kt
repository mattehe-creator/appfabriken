package se.tmconnect.rostfangst.wear

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ForslagMeddelandeTest {

    @Test
    fun testKodaOchAvkoda() {
        val id = 42L
        val typ = "PAMINNELSE"
        val rubrik = "Ring tandläkaren"
        
        val data = kodaForslag(id, typ, rubrik)
        val forslag = avkodaForslag(data)
        
        assertNotNull(forslag)
        assertEquals(id, forslag!!.id)
        assertEquals(typ, forslag.typ)
        assertEquals(rubrik, forslag.rubrik)
    }

    @Test
    fun testRubrikAvkortning() {
        val id = 1L
        val typ = "PAMINNELSE"
        val rubrik = "a".repeat(121) // 121 tecken
        
        val data = kodaForslag(id, typ, rubrik)
        val forslag = avkodaForslag(data)
        
        assertNotNull(forslag)
        assertEquals(id, forslag!!.id)
        assertEquals(typ, forslag.typ)
        assertTrue(forslag.rubrik.length <= 120)
        assertTrue(forslag.rubrik.endsWith("…"))
    }

    @Test
    fun testOgiltigJson() {
        val data = "{".toByteArray()
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testNegativtId() {
        val id = -1L
        val typ = "PAMINNELSE"
        val rubrik = "Ring tandläkaren"
        
        val data = kodaForslag(id, typ, rubrik)
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testTomTyp() {
        val id = 1L
        val typ = ""
        val rubrik = "Ring tandläkaren"
        
        val data = kodaForslag(id, typ, rubrik)
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testTomRubrik() {
        val id = 1L
        val typ = "PAMINNELSE"
        val rubrik = "   "
        
        val data = kodaForslag(id, typ, rubrik)
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }
}
