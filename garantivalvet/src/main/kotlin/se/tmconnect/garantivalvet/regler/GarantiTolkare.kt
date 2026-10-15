package se.tmconnect.garantivalvet.regler

import java.util.regex.Pattern

/**
 * Tolkar garantitiden i månader ur kvittotexten.
 *
 * Regler:
 * 1. Dela texten i rader och gör dem till gemener. Bara rader som innehåller något av orden `garanti` eller `warranty`
 *    (som delsträng, så "garantitid" och "garantin" räknas) kan ge ett svar. Hoppa över en rad som innehåller
 *    `reklamationsrätt`, `ingen garanti`, `utan garanti` eller `garanti ingår ej`.
 * 2. På en sådan rad letar du efter alla förekomster av ett **antal** följt av en **enhet**, med högst ett blanksteg
 *    eller en punkt emellan:
 *    - antal: ett heltal (siffror), eller orden `ett`, `en`, `två`, `tre`, `fyra`, `fem`, `sex`, `sju`, `åtta`, `nio`, `tio` (1 till 10).
 *    - enhet i år: `år`, `års`, `ar`, `year`, `years`, `yr`, `yrs`.
 *    - enhet i månader: `månad`, `månader`, `månaders`, `manader`, `mån`, `month`, `months`.
 *    - Enheten ska vara ett helt ord (efter enheten kommer inget bokstavstecken). Enheten "dagar" och andra enheter ger ingen träff.
 * 3. Antal år blir antal × 12 månader; antal månader är månader. Ett resultat är giltigt bara om det är mellan 1 och 120 månader,
 *    annars ignoreras träffen.
 * 4. Finns flera giltiga träffar väljs den som ligger **närmast** ordet `garanti`/`warranty` på samma rad (avstånd i tecken
 *    mellan träffen och ordet, ignorera vilket håll). Vid lika avstånd väljs den första raden och den första träffen.
 *    Svaret är den bästa träffen över alla rader.
 * 5. Returnerar `null` om inget tydligt anges.
 */
fun tolkaGarantiManader(text: String): Int? {
    val rader = text.lines().map { it.lowercase() }.filter { it.isNotEmpty() }
    
    // Filtera rader som innehåller garanti eller warranty men inte reklamationsrätt, ingen garanti, utan garanti eller garanti ingår ej
    val relevantaRader = rader.filter { rad ->
        val innehallerGaranti = Pattern.compile(".*garanti.*|.*warranty.*").matcher(rad).matches()
        val innehallerUteslut = Pattern.compile(".*reklamationsrätt.*|.*ingen garanti.*|.*utan garanti.*|.*garanti ingår ej.*").matcher(rad).matches()
        innehallerGaranti && !innehallerUteslut
    }
    
    if (relevantaRader.isEmpty()) return null
    
    val enhetsMönster = Regex("""(\d+|ett|en|två|tre|fyra|fem|sex|sju|åtta|nio|tio)\s*(?:\.\s*)?(\w+)""")
    
    // Mappning av ord till siffror
    val ordTillSiffra = mapOf(
        "ett" to 1, "en" to 1,
        "två" to 2, "tre" to 3,
        "fyra" to 4, "fem" to 5,
        "sex" to 6, "sju" to 7,
        "åtta" to 8, "nio" to 9,
        "tio" to 10
    )
    
    // Enheter i månader och år
    val månaderEnheter = setOf("månad", "månader", "månaders", "manader", "mån", "month", "months")
    val årEnheter = setOf("år", "års", "ar", "year", "years", "yr", "yrs")
    
    var bästaResultat: Int? = null
    var minstaAvstånd = Int.MAX_VALUE
    
    for (rad in relevantaRader) {
        // Hitta positionen för garanti eller warranty i raden
        val garantiMatch = Regex("garanti|warranty").find(rad)
        if (garantiMatch == null) continue
        
        val garantiPosition = garantiMatch.range.first
        
        // Hitta alla möjliga enheter i raden
        enhetsMönster.findAll(rad).forEach { match ->
            val antalText = match.groupValues[1]
            val enhet = match.groupValues[2]
            
            // Konvertera antal till siffra
            val antal = if (ordTillSiffra.containsKey(antalText)) {
                ordTillSiffra[antalText]!!
            } else {
                antalText.toIntOrNull() ?: continue
            }
            
            // Beräkna månader
            val månader = when {
                enhet in månaderEnheter -> antal
                enhet in årEnheter -> antal * 12
                else -> continue // Ogiltig enhet
            }
            
            // Kontrollera att månader är giltigt (mellan 1 och 120)
            if (månader < 1 || månader > 120) continue
            
            // Beräkna avstånd till garanti eller warranty
            val matchPosition = match.range.first
            val avstånd = Math.abs(matchPosition - garantiPosition)
            
            // Uppdatera bästa resultat om detta är närmare
            if (avstånd < minstaAvstånd) {
                minstaAvstånd = avstånd
                bästaResultat = månader
            }
        }
    }
    
    return bästaResultat
}
