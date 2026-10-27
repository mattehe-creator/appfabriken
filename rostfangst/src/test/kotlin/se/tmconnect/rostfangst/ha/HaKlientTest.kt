package se.tmconnect.rostfangst.ha

import org.junit.Test
import se.tmconnect.rostfangst.tolking.HttpSvar
import se.tmconnect.rostfangst.tolking.HttpTransport
import kotlin.test.assertEquals

class HaKlientTest {
    
    @Test
    fun testSkickaHandelse() {
        val transport = object : HttpTransport {
            var lastUrl: String? = null
            var lastHeaders: Map<String, String>? = null
            var lastBody: String? = null
            
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                lastUrl = url
                lastHeaders = headers
                lastBody = body
                return HttpSvar(200, "[]")
            }
        }
        
        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{\"typ\":\"KALENDER\"}")
        
        assertEquals("http://ha.local:8123/api/events/rostfangst_fangst", transport.lastUrl)
        assertEquals("Bearer abc", transport.lastHeaders!!["Authorization"])
        assertEquals("application/json", transport.lastHeaders!!["Content-Type"])
        assertEquals("{\"typ\":\"KALENDER\"}", transport.lastBody)
        assertEquals(HaUtfall.Ok("[]"), resultat)
    }
    
    @Test
    fun testLaggTillTodo() {
        val transport = object : HttpTransport {
            var lastUrl: String? = null
            var lastHeaders: Map<String, String>? = null
            var lastBody: String? = null
            
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                lastUrl = url
                lastHeaders = headers
                lastBody = body
                return HttpSvar(200, "[]")
            }
        }
        
        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.laggTillTodo("{\"item\":\"test\"}")
        
        assertEquals("http://ha.local:8123/api/services/todo/add_item", transport.lastUrl)
        assertEquals("Bearer abc", transport.lastHeaders!!["Authorization"])
        assertEquals("application/json", transport.lastHeaders!!["Content-Type"])
        assertEquals("{\"item\":\"test\"}", transport.lastBody)
        assertEquals(HaUtfall.Ok("[]"), resultat)
    }
    
    @Test
    fun testAssist() {
        val transport = object : HttpTransport {
            var lastUrl: String? = null
            var lastHeaders: Map<String, String>? = null
            var lastBody: String? = null
            
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                lastUrl = url
                lastHeaders = headers
                lastBody = body
                return HttpSvar(200, "{}")
            }
        }
        
        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.assist("tänd \"lampan\"")
        
        assertEquals("http://ha.local:8123/api/conversation/process", transport.lastUrl)
        assertEquals("Bearer abc", transport.lastHeaders!!["Authorization"])
        assertEquals("application/json", transport.lastHeaders!!["Content-Type"])
        assertEquals("{\"text\":\"tänd \\\"lampan\\\"\",\"language\":\"sv\"}", transport.lastBody)
        assertEquals(HaUtfall.Ok("{}"), resultat)
    }
    
    @Test
    fun test401Fel() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                return HttpSvar(401, "")
            }
        }
        
        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{}")
        
        assertEquals(HaUtfall.Fel("Token nekas, skapa en ny i Home Assistant."), resultat)
    }
    
    @Test
    fun testIOException() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                throw java.net.SocketTimeoutException()
            }
        }
        
        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{}")
        
        assertEquals(HaUtfall.Fel("Når inte Home Assistant just nu."), resultat)
    }
}
