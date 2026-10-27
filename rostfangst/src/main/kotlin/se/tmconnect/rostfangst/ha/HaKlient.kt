package se.tmconnect.rostfangst.ha

import se.tmconnect.rostfangst.tolking.HttpSvar
import se.tmconnect.rostfangst.tolking.HttpTransport

sealed interface HaUtfall {
    data class Ok(val kropp: String) : HaUtfall
    data class Fel(val text: String) : HaUtfall
}

interface HaKlient {
    fun skickaHandelse(json: String): HaUtfall
    fun laggTillTodo(json: String): HaUtfall
    fun assist(text: String): HaUtfall
}

class HaHttpKlient(
    private val adress: String,
    private val token: String,
    private val transport: HttpTransport = HttpURLConnectionTransport(),
) : HaKlient {
    
    override fun skickaHandelse(json: String): HaUtfall {
        val url = "$adress/api/events/rostfangst_fangst"
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json"
        )
        return skickaPost(url, headers, json)
    }
    
    override fun laggTillTodo(json: String): HaUtfall {
        val url = "$adress/api/services/todo/add_item"
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json"
        )
        return skickaPost(url, headers, json)
    }
    
    override fun assist(text: String): HaUtfall {
        val url = "$adress/api/conversation/process"
        val headers = mapOf(
            "Authorization" to "Bearer $token",
            "Content-Type" to "application/json"
        )
        val kropp = "{\"text\":${todoJsonText(text)},\"language\":\"sv\"}"
        return skickaPost(url, headers, kropp)
    }
    
    private fun skickaPost(url: String, headers: Map<String, String>, body: String): HaUtfall {
        return try {
            val svar = transport.post(url, headers, body)
            if (svar.statusKod in 200..299) {
                HaUtfall.Ok(svar.kropp)
            } else {
                HaUtfall.Fel(haFelText(svar.statusKod, null))
            }
        } catch (e: java.io.IOException) {
            HaUtfall.Fel(haFelText(null, e::class.simpleName ?: "IOException"))
        }
    }
    
    private fun todoJsonText(text: String): String {
        val sb = StringBuilder("\"")
        for (c in text) {
            when (c) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> if (c < ' ') sb.append("\\u%04x".format(c.code)) else sb.append(c)
            }
        }
        return sb.append('"').toString()
    }
}
