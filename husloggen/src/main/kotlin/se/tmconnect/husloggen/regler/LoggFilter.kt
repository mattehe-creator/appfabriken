package se.tmconnect.husloggen.regler

import java.util.Locale
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp

fun filtreraPoster(poster: List<Post>, sok: String, typ: PostTyp?): List<Post> {
    val ord = sok.trim().lowercase(Locale.ROOT).split(Regex("\\s+")).filter { it.isNotEmpty() }
    return poster.filter { post ->
        if (typ != null && post.typ != typ) return@filter false
        if (ord.isEmpty()) return@filter true
        val text = listOfNotNull(post.vad, post.utforare, post.anteckning).joinToString(" ").lowercase(Locale.ROOT)
        ord.all { text.contains(it) }
    }
}
