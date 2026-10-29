package se.tmconnect.rostfangst.ha

import se.tmconnect.rostfangst.regler.ForslagTyp

data class HaKonfig(val adress: String, val token: String, val todoEntity: String)

enum class HaSteg { HANDELSE, TODO, ASSIST }

fun haKonfig(adress: String, token: String, todoEntity: String): HaKonfig? {
    val ren = normaliseraHaAdress(adress) ?: return null
    val renToken = token.trim()
    if (renToken.isEmpty()) return null
    return HaKonfig(ren, renToken, todoEntity.trim())
}

fun haSteg(typ: ForslagTyp, konfig: HaKonfig?): List<HaSteg> {
    if (konfig == null) return emptyList()
    return when (typ) {
        ForslagTyp.ANTECKNING -> emptyList()
        ForslagTyp.HEM -> listOf(HaSteg.ASSIST)
        ForslagTyp.ATT_GORA ->
            if (konfig.todoEntity.isNotBlank()) listOf(HaSteg.HANDELSE, HaSteg.TODO)
            else listOf(HaSteg.HANDELSE)
        ForslagTyp.KALENDER, ForslagTyp.PAMINNELSE, ForslagTyp.FRAGA -> listOf(HaSteg.HANDELSE)
    }
}
