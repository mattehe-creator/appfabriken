package se.tmconnect.garantivalvet.regler

import org.junit.Test
import kotlin.test.assertEquals

class VarutypGissningTest {
    
    @Test
    fun testGissaVarutyp() {
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("Kaffebryggare"))
        assertEquals(Varutyp.MAT_DRYCK, gissaVarutyp("Kaffe 500 g"))
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("Mobiltelefon"))
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("TV 55\""))
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("tv"))
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("Diskmaskin"))
        assertEquals(Varutyp.KLADER_SKOR, gissaVarutyp("Löparskor"))
        assertEquals(Varutyp.KLADER_SKOR, gissaVarutyp("Vinterjacka"))
        assertEquals(Varutyp.HEM_BYGG, gissaVarutyp("Borrmaskin"))
        assertEquals(Varutyp.HEM_BYGG, gissaVarutyp("Väggfärg"))
        assertEquals(Varutyp.VAXTER, gissaVarutyp("Thuja"))
        assertEquals(Varutyp.VAXTER, gissaVarutyp("Rosbuske"))
        assertEquals(Varutyp.VAXTER, gissaVarutyp("Ros"))
        assertEquals(null, gissaVarutyp("Rost"))
        assertEquals(Varutyp.MAT_DRYCK, gissaVarutyp("Havremjölk"))
        assertEquals(Varutyp.MAT_DRYCK, gissaVarutyp("Snittblommor"))
        assertEquals(Varutyp.ELEKTRONIK, gissaVarutyp("Dishwasher"))
        assertEquals(null, gissaVarutyp("Present"))
        assertEquals(null, gissaVarutyp(""))
        assertEquals(null, gissaVarutyp("   "))
        assertEquals(null, gissaVarutyp("Tvättmedel"))
    }
}
