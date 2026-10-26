package se.tmconnect.rostfangst.lokal

fun byggOllamaChat(modell: String, systemprompt: String, text: String, typer: List<String>): String {
    val modelJson = jsonCitera(modell)
    val systemJson = jsonCitera(systemprompt)
    val textJson = jsonCitera(text)
    
    val enumJson = if (typer.isEmpty()) {
        "[]"
    } else {
        val enumElements = typer.map { jsonCitera(it) }
        "[${enumElements.joinToString(",")}]"
    }
    
    return """{"model":$modelJson,"stream":false,"messages":[{"role":"system","content":$systemJson},{"role":"user","content":$textJson}],"format":{"type":"object","properties":{"typ":{"type":"string","enum":$enumJson},"rubrik":{"type":"string"},"start":{"type":"string"},"text":{"type":"string"}},"required":["typ","rubrik"]}}"""
}
