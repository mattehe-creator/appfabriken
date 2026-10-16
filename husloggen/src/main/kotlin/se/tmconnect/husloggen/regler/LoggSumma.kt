package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post

fun summaPerAr(poster: List<Post>): Map<Int, Long> {
    return poster
        .filter { it.beloppOre != null }
        .groupBy { it.datum.year }
        .mapValues { entry ->
            entry.value.sumOf { it.beloppOre!! }
        }
}
