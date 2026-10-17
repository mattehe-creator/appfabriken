package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post

fun summaPerAr(poster: List<Post>): Map<Int, Long> {
    return poster
        .mapNotNull { post -> postBeloppForSammanstallningOre(post)?.let { post.datum.year to it } }
        .groupBy({ it.first }, { it.second })
        .mapValues { entry -> entry.value.sum() }
}
