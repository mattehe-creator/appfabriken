package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post

/** Senaste kostnad (netto öre) per uppgift, baserat på post med senast datum. */
fun senasteKostnadPerUppgift(poster: List<Post>): Map<Long, Long> {
    return poster
        .filter { it.uppgiftId != null && postBeloppForSammanstallningOre(it) != null }
        .groupBy { it.uppgiftId!! }
        .mapValues { (_, grupposter) ->
            val senaste =
                grupposter.maxWith(compareBy<Post> { it.datum }.thenBy { it.id })
            postBeloppForSammanstallningOre(senaste)!!
        }
}
