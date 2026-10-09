package se.tmconnect.garantivalvet.regler

import java.time.LocalDate

enum class FormularFel {
    VAD_SAKNAS,
    GARANTI_OGILTIG,
    KOPDATUM_FRAMTID,
    PRIS_OGILTIGT
}

fun tolkaPrisTillOre(text: String?): Long? {
    if (text == null || text.isBlank()) {
        return null
    }
    
    // Ta bort mellanslag och ersätt kommatecken med punkt för att få ett giltigt decimaltal
    val rensatText = text.replace(" ", "").replace(",", ".")
    
    return try {
        val pris = rensatText.toDouble()
        Math.round(pris * 100)
    } catch (e: NumberFormatException) {
        null
    }
}

fun valideraKopFormular(vad: String, garantiManader: Int, kopdatum: LocalDate, idag: LocalDate, prisText: String?): List<FormularFel> {
    val fel = mutableListOf<FormularFel>()
    
    // Kontrollera att "vad" inte är tomt
    if (vad.isBlank()) {
        fel.add(FormularFel.VAD_SAKNAS)
    }
    
    // Kontrollera att garanti är giltig (0-120 månader)
    if (garantiManader < 0 || garantiManader > 120) {
        fel.add(FormularFel.GARANTI_OGILTIG)
    }
    
    // Kontrollera att kopdatum inte är efter idag
    if (kopdatum.isAfter(idag)) {
        fel.add(FormularFel.KOPDATUM_FRAMTID)
    }
    
    // Kontrollera att pris är giltigt om det finns
    if (!prisText.isNullOrEmpty() && tolkaPrisTillOre(prisText) == null) {
        fel.add(FormularFel.PRIS_OGILTIGT)
    }
    
    return fel
}
