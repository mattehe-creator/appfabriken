package se.tmconnect.rostfangst.ha

import se.tmconnect.rostfangst.regler.ForslagTyp

data class HaKonfig(val adress: String, val token: String, val todoEntity: String)

enum class HaSteg { HANDELSE, TODO, ASSIST }

fun haKonfig(adress: String, token: String, todoEntity: String): HaKonfig? {
    val normaliseradAdress = normaliseraHaAdress(adress)
    if (normaliseradAdress == null) {
        return null
    }
    
    val trimmadToken = token.trim()
    if (trimmadToken.isEmpty()) {
        return null
    }
    
    val trimmadTodoEntity = todoEntity.trim()
    
    return HaKonfig(normaliseradAdress, trimmadToken, trimmadTodoEntity)
}

fun haSteg(typ: ForslagTyp, konfig: HaKonfig?): List<HaSteg> {
    if (konfig == null) {
        return emptyList()
    }
    
    return when (typ) {
        ForslagTyp.ANTECKNING -> emptyList()
        ForslagTyp.HEM -> listOf(HaSteg.ASSIST)
        ForslagTyp.ATT_GORA -> {
            if (konfig.todoEntity.isNotEmpty()) {
                listOf(HaSteg.HANDELSE, HaSteg.TODO)
            } else {
                listOf(HaSteg.HANDELSE)
            }
        }
        else -> listOf(HaSteg.HANDELSE)
    }
}
