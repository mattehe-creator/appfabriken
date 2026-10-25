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
    fun testAvkodaTomt() {
        val data = ByteArray(0)
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testAvkodaOgiltigJson() {
        val data = "{".toByteArray()
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testAvkodaNegativtId() {
        val json = """{"id": -1, "typ": "PAMINNELSE", "rubrik": "Ring tandläkaren"}"""
        val data = json.toByteArray()
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testAvkodaTomTyp() {
        val json = """{"id": 1, "typ": "", "rubrik": "Ring tandläkaren"}"""
        val data = json.toByteArray()
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testAvkodaTomRubrik() {
        val json = """{"id": 1, "typ": "PAMINNELSE", "rubrik": ""}"""
        val data = json.toByteArray()
        val forslag = avkodaForslag(data)
        assertNull(forslag)
    }

    @Test
    fun testAvkodaRubrikLängreAn120() {
        val rubrik = "a".repeat(121)
        val data = kodaForslag(1, "PAMINNELSE", rubrik)
        val forslag = avkodaForslag(data)
        assertNotNull(forslag)
        assertTrue(forslag!!.rubrik.length <= 120)
        assertTrue(forslag.rubrik.endsWith("…"))
    }
}
