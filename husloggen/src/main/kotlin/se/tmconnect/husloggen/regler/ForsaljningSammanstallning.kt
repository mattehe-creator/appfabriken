package se.tmconnect.husloggen.regler

import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

enum class ForbattringMojlighet {
    Ja,
    KravBedomning,
    Nej,
}

/** Summa i öre per kalenderår och posttyp (poster utan belopp räknas inte). */
fun summeraPerAr(poster: List<Post>): Map<Int, Map<PostTyp, Long>> =
    poster
        .filter { it.beloppOre != null }
        .groupBy { it.datum.year }
        .mapValues { (_, arPoster) ->
            arPoster
                .groupBy { it.typ }
                .mapValues { (_, typPoster) -> typPoster.sumOf { it.beloppOre!! } }
        }

fun kanVaraForbattring(post: Post, forsaljningsar: Int): ForbattringMojlighet =
    when (post.typ) {
        PostTyp.OMBYGGNAD -> ForbattringMojlighet.Ja
        PostTyp.UNDERHALL, PostTyp.REPARATION -> {
            if (post.datum.year >= forsaljningsar - 5) {
                ForbattringMojlighet.KravBedomning
            } else {
                ForbattringMojlighet.Nej
            }
        }
    }

/** Sant när årets förbättringsutgifter (alla loggtyper med belopp) uppgår till minst 5 000 kr. */
fun arStodjerGransbelopp(poster: List<Post>, ar: Int): Boolean {
    val summa = poster
        .filter { it.datum.year == ar && it.beloppOre != null }
        .sumOf { it.beloppOre!! }
    return summa >= FORBATTRING_GRANS_ORE
}

fun arSverigeForSkatteInfo(landskod: String): Boolean =
    landskod.equals("SE", ignoreCase = true)

fun standardForsaljningsar(idag: LocalDate = LocalDate.now()): Int = idag.year
