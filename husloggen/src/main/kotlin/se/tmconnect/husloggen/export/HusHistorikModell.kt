package se.tmconnect.husloggen.export

import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.regler.ForbattringMojlighet
import se.tmconnect.husloggen.regler.arStodjerGransbelopp
import se.tmconnect.husloggen.regler.kanVaraForbattring
import se.tmconnect.husloggen.regler.summeraPerAr

data class HusHistorikPostRad(
    val post: Post,
    val typEtikett: String,
    val datumEtikett: String,
    val beloppEtikett: String,
    val forbattringEtikett: String?,
)

data class HusHistorikArBlock(
    val ar: Int,
    val typSummor: List<Pair<String, Long>>,
    val totalOre: Long,
    val gransUppfylld: Boolean?,
    val gransEtikett: String?,
    val poster: List<HusHistorikPostRad>,
)

data class HusHistorikData(
    val arBlock: List<HusHistorikArBlock>,
    val inkluderaSkatteAvsnitt: Boolean,
)

data class HusHistorikTexter(
    val rubrik: String,
    val kolumnDatum: String,
    val kolumnBeskrivning: String,
    val kolumnTyp: String,
    val kolumnBelopp: String,
    val kolumnForbattring: String,
    val arSummaRubrik: (Int) -> String,
    val typSummaRad: (String, String) -> String,
    val totalt: String,
    val gransJa: String,
    val gransNej: String,
    val skattRubrik: String,
    val skattText: String,
    val kallorRubrik: String,
    val kallorText: String,
)

fun byggHusHistorik(
    poster: List<Post>,
    forsaljningsar: Int,
    localeLand: String,
    typEtikett: (PostTyp) -> String,
    datumEtikett: (Post) -> String,
    beloppEtikett: (Post) -> String,
    forbattringEtikett: (ForbattringMojlighet) -> String,
    gransJa: String,
    gransNej: String,
): HusHistorikData {
    val inkluderaSkatt = localeLand.equals("SE", ignoreCase = true)
    val summor = summeraPerAr(poster)
    val arLista = (poster.map { it.datum.year } + summor.keys).distinct().sortedDescending()
    val posterPerAr = poster.groupBy { it.datum.year }

    val block = arLista.map { ar ->
        val typMap = summor[ar].orEmpty()
        val typSummor = PostTyp.entries.mapNotNull { typ ->
            typMap[typ]?.let { typEtikett(typ) to it }
        }
        val total = typMap.values.sum()
        HusHistorikArBlock(
            ar = ar,
            typSummor = typSummor,
            totalOre = total,
            gransUppfylld = if (inkluderaSkatt) arStodjerGransbelopp(poster, ar) else null,
            gransEtikett = if (inkluderaSkatt) {
                if (arStodjerGransbelopp(poster, ar)) gransJa else gransNej
            } else {
                null
            },
            poster = (posterPerAr[ar].orEmpty())
                .sortedByDescending { it.datum }
                .map { post ->
                    HusHistorikPostRad(
                        post = post,
                        typEtikett = typEtikett(post.typ),
                        datumEtikett = datumEtikett(post),
                        beloppEtikett = beloppEtikett(post),
                        forbattringEtikett = if (inkluderaSkatt) {
                            forbattringEtikett(kanVaraForbattring(post, forsaljningsar))
                        } else {
                            null
                        },
                    )
                },
        )
    }
    return HusHistorikData(arBlock = block, inkluderaSkatteAvsnitt = inkluderaSkatt)
}
