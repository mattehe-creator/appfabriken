package se.tmconnect.rostfangst.ha

import se.tmconnect.rostfangst.regler.Forslag

data class HaResultat(val svar: String?, val fel: List<String>)

fun korHaSteg(steg: List<HaSteg>, klient: HaKlient, konfig: HaKonfig, forslag: Forslag, original: String): HaResultat {
    if (steg.isEmpty()) return HaResultat(null, emptyList())
    
    val fel = mutableListOf<String>()
    var svar: String? = null
    
    for (s in steg) {
        when (s) {
            HaSteg.HANDELSE -> {
                val json = bygghaHandelseJson(forslag, original)
                val utfall = klient.skickaHandelse(json)
                if (utfall is HaUtfall.Fel) {
                    fel.add(utfall.text)
                }
            }
            HaSteg.TODO -> {
                val todoJson = byggTodoJson(konfig.todoEntity, forslag.titel)
                if (todoJson != null) {
                    val utfall = klient.laggTillTodo(todoJson)
                    if (utfall is HaUtfall.Fel) {
                        fel.add(utfall.text)
                    }
                }
            }
            HaSteg.ASSIST -> {
                val utfall = klient.assist(forslag.titel)
                if (utfall is HaUtfall.Ok) {
                    svar = lasAssistSvar(utfall.kropp) ?: "Klart."
                } else if (utfall is HaUtfall.Fel) {
                    fel.add(utfall.text)
                }
            }
        }
    }
    
    return HaResultat(svar, fel.distinct())
}
