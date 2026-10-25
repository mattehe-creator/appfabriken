package se.tmconnect.rostfangst.ha

fun byggTodoJson(entityId: String, titel: String): String? {
    val trimmedEntityId = entityId.trim()
    val trimmedTitel = titel.trim()
    
    if (trimmedEntityId.isEmpty() || trimmedTitel.isEmpty()) {
        return null
    }
    
    // Simple string building without JSONObject
    return "{\"entity_id\":\"$trimmedEntityId\",\"item\":\"$trimmedTitel\"}"
}
