package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post

data class ArGrupp(
    val ar: Int,
    val poster: List<Post>,
    val summaOre: Long,
)

fun grupperaPerAr(poster: List<Post>): List<ArGrupp> {
    val grupperad = poster.groupBy { it.datum.year }
    
    return grupperad.entries
        .map { (ar, posterForAr) ->
            val summa = posterForAr
                .sumOf { postBeloppForSammanstallningOre(it) ?: 0L }
            
            ArGrupp(ar, posterForAr, summa)
        }
        .sortedByDescending { it.ar }
}
