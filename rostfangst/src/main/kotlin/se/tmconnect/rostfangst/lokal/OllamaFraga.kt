package se.tmconnect.rostfangst.lokal

/**
 * Bygger JSON för Ollamas /api/chat med strukturerad utdata. Schemat matchar `tolkaSvar`:
 * typ och titel krävs; start och slut är ISO-8601 (Instant), plats och svar är fritext.
 */
fun byggOllamaChat(modell: String, systemprompt: String, text: String, typer: List<String>): String {
    val modelJson = jsonCitera(modell)
    val systemJson = jsonCitera(systemprompt)
    val textJson = jsonCitera(text)
    val enumJson = typer.joinToString(",", "[", "]") { jsonCitera(it) }
    return """{"model":$modelJson,"stream":false,"messages":[{"role":"system","content":$systemJson},{"role":"user","content":$textJson}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":$enumJson},"titel":{"type":"string"},"start":{"type":"string"},"slut":{"type":"string"},"plats":{"type":"string"},"svar":{"type":"string"}},"required":["typ","titel"]}}"""
}
