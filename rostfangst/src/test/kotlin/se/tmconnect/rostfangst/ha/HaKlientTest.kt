package se.tmconnect.rostfangst.ha

import org.junit.Test
import se.tmconnect.rostfangst.tolking.HttpSvar
import se.tmconnect.rostfangst.tolking.HttpTransport
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
        assertEquals("application/json", transport.headers!!["Content-Type"])
        assertEquals("{\"typ\":\"KALENDER\"}", transport.body)
        assertIs<HaUtfall.Ok>(resultat)
        assertEquals("[]", resultat.kropp)
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
        assertEquals("application/json", transport.headers!!["Content-Type"])
        assertEquals("{\"item\":\"test\"}", transport.body)
        assertIs<HaUtfall.Ok>(resultat)
        assertEquals("[]", resultat.kropp)
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
                return HttpSvar(200, "{\"response\":\"Hej\"}")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.assist("tänd \"lampan\"")

        assertEquals("http://ha.local:8123/api/conversation/process", transport.url)
        assertEquals("Bearer abc", transport.headers!!["Authorization"])
        assertEquals("application/json", transport.headers!!["Content-Type"])
        assertEquals("{\"text\":\"tänd \\\"lampan\\\"\",\"language\":\"sv\"}", transport.body)
        assertIs<HaUtfall.Ok>(resultat)
        assertEquals("{\"response\":\"Hej\"}", resultat.kropp)
    }

    @Test
    fun `401 returnerar Fel med rätt text`() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                return HttpSvar(401, "")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{}")

        assertIs<HaUtfall.Fel>(resultat)
        assertEquals("Token nekas, skapa en ny i Home Assistant.", resultat.text)
    }

    @Test
    fun `IOException returnerar Fel`() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                throw IOException("Nätverksfel")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{}")

        assertIs<HaUtfall.Fel>(resultat)
        assertEquals("Når inte Home Assistant just nu.", resultat.text)
    }

    @Test
    fun `SocketTimeoutException returnerar Fel`() {
        val transport = object : HttpTransport {
            override fun post(url: String, headers: Map<String, String>, body: String): HttpSvar {
                throw SocketTimeoutException("Timeout")
            }
        }

        val klient = HaHttpKlient("http://ha.local:8123", "abc", transport)
        val resultat = klient.skickaHandelse("{}")

        assertIs<HaUtfall.Fel>(resultat)
        assertEquals("Når inte Home Assistant just nu.", resultat.text)
    }
}
