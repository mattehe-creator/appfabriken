package se.tmconnect.rostfangst.lokal

import org.junit.Test
import org.junit.Assert.*

class AdressbedomningTest {

    @Test
    fun testBedomAdress() {
        assertEquals(AdressBedomning.Krypterad, bedomAdress("https://dator.tail1234.ts.net"))
        assertEquals(AdressBedomning.Krypterad, bedomAdress("HTTPS://dator.ts.net:11434/api"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://192.168.1.10:11434"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://10.0.0.5"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://172.16.0.1"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://172.31.255.1"))
        assertEquals(AdressBedomning.Avvisad("okrypterad_utanfor_lokalt_nat"), bedomAdress("http://172.32.0.1"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://localhost:11434"))
        assertEquals(AdressBedomning.OkrypteradLokal, bedomAdress("http://minpc.local"))
        assertEquals(AdressBedomning.Avvisad("okrypterad_utanfor_lokalt_nat"), bedomAdress("http://example.com"))
        assertEquals(AdressBedomning.Avvisad("okrypterad_utanfor_lokalt_nat"), bedomAdress("http://10.example.com"))
        assertEquals(AdressBedomning.Ogiltig("saknar_schema"), bedomAdress("192.168.1.10"))
        assertEquals(AdressBedomning.Ogiltig("tom"), bedomAdress("   "))
        assertEquals(AdressBedomning.Ogiltig("saknar_vard"), bedomAdress("https://"))
    }
}
