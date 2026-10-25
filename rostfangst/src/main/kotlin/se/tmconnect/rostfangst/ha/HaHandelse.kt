package se.tmconnect.rostfangst.ha

import org.json.JSONObject
import se.tmconnect.rostfangst.regler.Forslag
import java.time.Instant

fun bygghaHandelseJson(forslag: Forslag, original: String): String {
    val obj = JSONObject()
    obj.put("typ", forslag.typ.name)
    obj.put("titel", forslag.titel)
    obj.put("start", forslag.start?.toString())
    obj.put("slut", forslag.slut?.toString())
    obj.put("plats", forslag.plats)
    obj.put("original", original)
    return obj.toString()
}
