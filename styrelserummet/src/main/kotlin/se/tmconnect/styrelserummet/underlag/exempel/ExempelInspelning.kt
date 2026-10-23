package se.tmconnect.styrelserummet.underlag.exempel

import se.tmconnect.styrelserummet.modell.Komponentgrupp
import se.tmconnect.styrelserummet.tolking.Tolkare
import se.tmconnect.styrelserummet.underlag.gissaGrupp

private const val INSPIELAD_KALLA = "exempel-underhallsplan.pdf, sida 2"

private fun gruppForExempelAtgard(namn: String): Komponentgrupp {
    return gissaGrupp(namn) ?: when (namn) {
        "Entréparti" -> Komponentgrupp.FONSTER_DORRAR
        "Brandlarm" -> Komponentgrupp.OVRIGT
        "Balkonger" -> Komponentgrupp.FASAD
        else -> throw IllegalArgumentException("Okänd åtgärd: $namn")
    }
}

private fun nyttjandeperiodArForGrupp(grupp: Komponentgrupp): Int =
    when (grupp) {
        Komponentgrupp.STOMME -> 100
        Komponentgrupp.TAK -> 50
        Komponentgrupp.FASAD -> 40
        Komponentgrupp.FONSTER_DORRAR -> 40
        Komponentgrupp.STAMMAR -> 50
        Komponentgrupp.EL -> 50
        Komponentgrupp.VARME -> 80
        Komponentgrupp.VENTILATION -> 20
        Komponentgrupp.HISS -> 25
        Komponentgrupp.TVATTSTUGA -> 30
        Komponentgrupp.MARK_OCH_GARAGE -> 0
        Komponentgrupp.OVRIGT -> 30
    }

private fun sakerhetJsonForAtgard(namn: String): String =
    if (namn == "Balkonger") "LAG" else "HOG"

private fun komponentJsonForAtgard(atgard: ExempelAtgard): String {
    val grupp = gruppForExempelAtgard(atgard.namn)
    val nyttjandeperiodAr = nyttjandeperiodArForGrupp(grupp)
    val sakerhet = sakerhetJsonForAtgard(atgard.namn)
    return if (atgard.namn == "Balkonger") {
        """{"namn":"Balkonger","grupp":"${grupp.name}","anskaffningsvarde":null,"anskaffningsar":null,"nyttjandeperiodAr":$nyttjandeperiodAr,"kalla":"$INSPIELAD_KALLA","sakerhet":"$sakerhet"}"""
    } else {
        val varde = atgard.kostnadKronor!!
        """{"namn":"${atgard.namn}","grupp":"${grupp.name}","anskaffningsvarde":$varde,"anskaffningsar":${atgard.ar},"nyttjandeperiodAr":$nyttjandeperiodAr,"kalla":"$INSPIELAD_KALLA","sakerhet":"$sakerhet"}"""
    }
}

fun inspeladTolkareJson(): String {
    val komponenter = EXEMPEL_ATGARDER.joinToString(",") { komponentJsonForAtgard(it) }
    return """{"komponenter":[$komponenter],"anmarkningar":["Kostnad saknas i planen för Balkonger."]}"""
}

fun inspeladTolkare(): Tolkare =
    object : Tolkare {
        override fun foreslaKomponenter(
            underhallsplanUtdrag: String,
            sidor: List<Int>,
            filnamn: String,
        ): Result<String> = Result.success(inspeladTolkareJson())
    }
