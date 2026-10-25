package se.tmconnect.rostfangst.ha

/** Bygger JSON för Home Assistants todo.add_item. Ren Kotlin: org.json finns inte i enhetstester. */
fun byggTodoJson(entityId: String, titel: String): String? {
    val id = entityId.trim()
    val item = titel.trim()
    if (id.isEmpty() || item.isEmpty()) return null
    return "{\"entity_id\":" + todoJsonText(id) + ",\"item\":" + todoJsonText(item) + "}"
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
