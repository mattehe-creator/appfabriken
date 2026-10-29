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
                val resultat = klient.skickaHandelse(json)
                if (resultat is HaUtfall.Fel) {
                    fel.add(resultat.text)
                }
            }
            HaSteg.TODO -> {
                val todoJson = byggTodoJson(konfig.todoEntity, forslag.titel)
                if (todoJson != null) {
                    val resultat = klient.laggTillTodo(todoJson)
                    if (resultat is HaUtfall.Fel) {
                        fel.add(resultat.text)
                    }
                }
            }
            HaSteg.ASSIST -> {
                val resultat = klient.assist(forslag.titel)
                if (resultat is HaUtfall.Ok) {
                    svar = lasAssistSvar(resultat.kropp) ?: "Klart."
                } else if (resultat is HaUtfall.Fel) {
                    fel.add(resultat.text)
                }
            }
        }
    }
    
    return HaResultat(svar, fel.distinct())
}
