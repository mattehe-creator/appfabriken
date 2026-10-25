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
        val result = avkodaForslag(data)
        
        assertNotNull(result)
        assertEquals(id, result!!.id)
        assertEquals(typ, result.typ)
        assertEquals(rubrik, result.rubrik)
    }

    @Test
    fun testRubrikAvkortning() {
        val id = 42L
        val typ = "PAMINNELSE"
        val rubrik = "a".repeat(121)
        
        val data = kodaForslag(id, typ, rubrik)
        val result = avkodaForslag(data)
        
        assertNotNull(result)
        assertEquals(id, result!!.id)
        assertEquals(typ, result.typ)
        assertTrue(result.rubrik.length <= 120)
        assertTrue(result.rubrik.endsWith("…"))
    }

    @Test
    fun testOgiltigJson() {
        val data = "{".toByteArray()
        val result = avkodaForslag(data)
        assertNull(result)
    }

    @Test
    fun testNegativtId() {
        val data = kodaForslag(-1, "PAMINNELSE", "Ring tandläkaren")
        val result = avkodaForslag(data)
        assertNull(result)
    }

    @Test
    fun testTomTyp() {
        val data = kodaForslag(42, "", "Ring tandläkaren")
        val result = avkodaForslag(data)
        assertNull(result)
    }

    @Test
    fun testTomRubrik() {
        val data = kodaForslag(42, "PAMINNELSE", "")
        val result = avkodaForslag(data)
        assertNull(result)
    }
}
