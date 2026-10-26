package se.tmconnect.rostfangst.lokal

fun byggOllamaChat(modell: String, systemprompt: String, text: String, typer: List<String>): String {
    val modelJson = jsonCitera(modell)
    val systempromptJson = jsonCitera(systemprompt)
    val textJson = jsonCitera(text)
    
    val enumValues = typer.joinToString(",") { jsonCitera(it) }
    
    return """{"model":$modelJson,"stream":false,"messages":[{"role":"system","content":$systempromptJson},{"role":"user","content":$textJson}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":[$enumValues]},"rubrik":{"type":"string"},"start":{"type":"string"},"text":{"type":"string"}},"required":["typ","rubrik"]}}"""
}
