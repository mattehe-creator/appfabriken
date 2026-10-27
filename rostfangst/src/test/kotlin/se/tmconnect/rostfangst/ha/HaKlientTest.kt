package se.tmconnect.rostfangst.ha

import org.junit.Test
import org.junit.Assert.assertEquals
import se.tmconnect.rostfangst.tolking.HttpSvar
import se.tmconnect.rostfangst.tolking.HttpTransport

class HaKlientTest {

    @Test
    fun `skickaHandelse med 200 returnerar Ok`() {
        val transport = object : HttpTransport {
            var url: String? = null
            var headers: Map<String, String>? = null
            var body: String? = null

            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                this.url = url
                this.headers = headers
                this.body = body
                return HttpSvar(200, "[]")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{\"typ\":\"KALENDER\"}")

        assertEquals("http://ha.local:8123/api/events/rostfangst_fangst", transport.url)
        assertEquals("Bearer abc", transport.headers!!["Authorization"])
        assertEquals("{\"typ\":\"KALENDER\"}", transport.body)
        assertEquals(HaUtfall.Ok("[]"), resultat)
    }

    @Test
    fun `laggTillTodo med 200 returnerar Ok`() {
        val transport = object : HttpTransport {
            var url: String? = null
            var headers: Map<String, String>? = null
            var body: String? = null

            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                this.url = url
                this.headers = headers
                this.body = body
                return HttpSvar(200, "[]")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.laggTillTodo("{\"item\":\"test\"}")

        assertEquals("http://ha.local:8123/api/services/todo/add_item", transport.url)
        assertEquals("Bearer abc", transport.headers!!["Authorization"])
        assertEquals("{\"item\":\"test\"}", transport.body)
        assertEquals(HaUtfall.Ok("[]"), resultat)
    }

    @Test
    fun `assist med 200 returnerar Ok`() {
        val transport = object : HttpTransport {
            var url: String? = null
            var headers: Map<String, String>? = null
            var body: String? = null

            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                this.url = url
                this.headers = headers
                this.body = body
                return HttpSvar(200, "[]")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.assist("tänd \"lampan\"")

        assertEquals("http://ha.local:8123/api/conversation/process", transport.url)
        assertEquals("Bearer abc", transport.headers!!["Authorization"])
        assertEquals("{\"text\":\"tänd \\\"lampan\\\"\",\"language\":\"sv\"}", transport.body)
        assertEquals(HaUtfall.Ok("[]"), resultat)
    }

    @Test
    fun `401 returnerar Fel med rätt text`() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                return HttpSvar(401, "")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{\"typ\":\"KALENDER\"}")

        assertEquals(HaUtfall.Fel("Token nekas, skapa en ny i Home Assistant."), resultat)
    }

    @Test
    fun `IOException returnerar Fel med rätt text`() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                throw java.net.SocketTimeoutException()
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{\"typ\":\"KALENDER\"}")

        assertEquals(HaUtfall.Fel("Når inte Home Assistant just nu."), resultat)
    }
}
