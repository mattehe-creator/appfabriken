package se.tmconnect.styrelserummet.regler

data class StammaRad(val typ: String, val ar: Int, val varde: Long, val enhet: String, val kalla: String)

fun lasStammaData(rader: List<String>): Map<String, StammaRad> {
    val resultat = mutableMapOf<String, StammaRad>()
    
    rader.forEachIndexed { index, rad ->
        // Ignorera tomma rader
        if (rad.isBlank()) {
            return@forEachIndexed
        }
        
        // Ignorera rubrikrad om den börjar med "typ"
        if (index == 0 && rad.startsWith("typ")) {
            return@forEachIndexed
        }
        
        val fält = rad.split(";")
        
        // Kontrollera att raden har exakt fem fält
        if (fält.size != 5) {
            throw IllegalArgumentException("Ogiltig rad: fel antal fält. Rad: $rad")
        }
        
        val typ = fält[0]
        val arStr = fält[1]
        val vardeStr = fält[2]
        val enhet = fält[3]
        val kalla = fält[4]
        
        // Kontrollera att år är ett giltigt tal
        val ar = try {
            arStr.toInt()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Ogiltigt år: $arStr. Rad: $rad")
        }
        
        // Kontrollera att år är inom giltigt intervall
        if (ar < 0 || ar > 9999) {
            throw IllegalArgumentException("År utanför giltigt intervall: $ar. Rad: $rad")
        }
        
        // Kontrollera att värde är ett giltigt tal
        val varde = try {
            vardeStr.toLong()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Ogiltigt värde: $vardeStr. Rad: $rad")
        }
        
        // Kontrollera att värde är positivt
        if (varde < 0) {
            throw IllegalArgumentException("Negativt värde: $varde. Rad: $rad")
        }
        
        // Kontrollera att kalla inte är tom
        if (kalla.isBlank()) {
            throw IllegalArgumentException("Tom källa. Rad: $rad")
        }
        
        val nyckel = "$typ_$ar"
        resultat[nyckel] = StammaRad(typ, ar, varde, enhet, kalla)
    }
    
    return resultat
}
