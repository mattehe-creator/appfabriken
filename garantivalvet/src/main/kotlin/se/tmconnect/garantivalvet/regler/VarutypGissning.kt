package se.tmconnect.garantivalvet.regler

/**
 * Gissar varutyp utifrån namnet på varan.
 *
 * Funktionen:
 * 1. Gör namnet till gemener, delar upp i ord (endast bokstäver) och ignorerar tomma ord
 * 2. Ett ord matchar ett nyckelord om det är lika med nyckelordet eller om nyckelordet har minst 4 bokstäver
 *    och ordet slutar på nyckelordet
 * 3. Kategorier kontrolleras i ordning: ELEKTRONIK, KLADER_SKOR, HEM_BYGG, VAXTER, MAT_DRYCK
 * 4. Returnerar null om ingen matchning hittas
 *
 * @param vad namnet på varan
 * @return den gissade varutypen eller null om ingen matchning
 */
fun gissaVarutyp(vad: String): Varutyp? {
    if (vad.isBlank()) return null
    
    val ord = vad.lowercase().split(Regex("[^\\p{L}]")).filter { it.isNotEmpty() }
    
    val kategorier = mapOf(
        Varutyp.ELEKTRONIK to listOf(
            "tv", "dator", "laptop", "telefon", "mobil", "surfplatta", "hörlurar", "kamera", "skärm", 
            "laddare", "högtalare", "bryggare", "vattenkokare", "brödrost", "mikrovågsugn", "dammsugare", 
            "diskmaskin", "tvättmaskin", "kyl", "frys", "kylskåp", "hårtork", "rakapparat", "dishwasher", 
            "kettle", "headphones", "speaker", "charger", "television", "phone"
        ),
        Varutyp.KLADER_SKOR to listOf(
            "jacka", "jeans", "byxor", "byxa", "skor", "sko", "kängor", "stövlar", "tröja", "klänning", 
            "kjol", "strumpor", "mossa", "handskar", "skjorta", "kavaj", "kappa", "jacket", "shoes", 
            "boots", "trousers", "sweater", "dress", "coat"
        ),
        Varutyp.HEM_BYGG to listOf(
            "borrmaskin", "skruvdragare", "hammare", "färg", "spik", "skruv", "kran", "toalettstol", 
            "golv", "laminat", "tapet", "lampa", "drill", "hammer", "paint", "screws"
        ),
        Varutyp.VAXTER to listOf(
            "planta", "växt", "tuja", "thuja", "buske", "träd", "ros", "palm", "kaktus", "orkidé", 
            "plant", "shrub", "tree"
        ),
        Varutyp.MAT_DRYCK to listOf(
            "mjölk", "bröd", "kaffe", "te", "kött", "fisk", "ost", "frukt", "grönsaker", "ägg", 
            "juice", "läsk", "vin", "öl", "choklad", "kaka", "yoghurt", "snittblommor", "bukett", 
            "milk", "bread", "coffee", "cheese", "fruit"
        )
    )
    
    for ((varutyp, nyckelord) in kategorier) {
        if (ord.any { ordText ->
            nyckelord.any { key ->
                key == ordText || (key.length >= 4 && ordText.endsWith(key))
            }
        }) {
            return varutyp
        }
    }
    
    return null
}
