package se.tmconnect.prov.aviseringar.export

import se.tmconnect.prov.aviseringar.data.AviseringEntitet
import java.time.Instant

fun skrivAviseringExportJson(rader: List<AviseringEntitet>, exporteradTid: Instant = Instant.now()): String =
    buildString {
        append('{')
        append("\"exporterad\":")
        append(jsonStrang(exporteradTid.toString()))
        append(',')
        append("\"aviseringar\":[")
        rader.forEachIndexed { index, rad ->
            if (index > 0) append(',')
            append(skrivAviseringObjekt(rad))
        }
        append("]}")
    }

private fun skrivAviseringObjekt(rad: AviseringEntitet): String =
    buildString {
        append('{')
        appendFalt("loggadTid", rad.loggadTid)
        append(',')
        appendFalt("postTime", rad.postTime)
        append(',')
        appendFaltStrang("nyckel", rad.nyckel)
        append(',')
        appendFaltStrang("paketNamn", rad.paketNamn)
        append(',')
        appendFaltStrang("appNamn", rad.appNamn)
        append(',')
        appendFaltStrangValfri("kanalId", rad.kanalId)
        append(',')
        appendFaltStrangValfri("title", rad.title)
        append(',')
        appendFaltStrangValfri("text", rad.text)
        append(',')
        appendFaltStrangValfri("bigText", rad.bigText)
        append(',')
        appendFaltStrangValfri("subText", rad.subText)
        append(',')
        appendFaltStrangValfri("textLines", rad.textLines)
        append(',')
        appendFaltStrangValfri("conversationTitle", rad.conversationTitle)
        append(',')
        appendFaltStrangValfri("messages", rad.messages)
        append('}')
    }

private fun StringBuilder.appendFalt(namn: String, varde: Long) {
    append('"')
    append(namn)
    append("\":")
    append(varde)
}

private fun StringBuilder.appendFaltStrang(namn: String, varde: String) {
    append('"')
    append(namn)
    append("\":")
    append(jsonStrang(varde))
}

private fun StringBuilder.appendFaltStrangValfri(namn: String, varde: String?) {
    append('"')
    append(namn)
    append("\":")
    if (varde == null) {
        append("null")
    } else {
        append(jsonStrang(varde))
    }
}

private fun jsonStrang(varde: String): String {
    val sb = StringBuilder(varde.length + 2)
    sb.append('"')
    varde.forEach { tecken ->
        when (tecken) {
            '\\' -> sb.append("\\\\")
            '"' -> sb.append("\\\"")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else -> sb.append(tecken)
        }
    }
    sb.append('"')
    return sb.toString()
}

fun aviseringExportFilnamn(): String {
    val stampel = Instant.now().toString().replace(':', '-').substringBefore('.')
    return "aviseringar-$stampel.json"
}
