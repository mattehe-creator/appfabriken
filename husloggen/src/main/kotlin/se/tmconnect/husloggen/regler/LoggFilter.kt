package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.util.Locale

fun filtreraPoster(poster: List<Post>, sok: String, typ: PostTyp?): List<Post> {
    val trimmadSok = sok.trim()
    val sokOrd = if (trimmadSok.isEmpty()) {
        emptyList()
    } else {
        trimmadSok.lowercase(Locale.ROOT).split("\\s+".toRegex())
    }
    
    return poster.filter { post ->
        // Filtrera på typ om typ är angiven
        if (typ != null && post.typ != typ) {
            return@filter false
        }
        
        // Om sökning är tom, matcha alla
        if (sokOrd.isEmpty()) {
            return@filter true
        }
        
        // Kontrollera att alla ord finns i något av fälten
        val faltInnehall = listOf(
            post.vad.lowercase(Locale.ROOT),
            post.utforare?.lowercase(Locale.ROOT) ?: "",
            post.anteckning?.lowercase(Locale.ROOT) ?: ""
        ).joinToString(" ")
        
        sokOrd.all { ord -> faltInnehall.contains(ord) }
    }
}
