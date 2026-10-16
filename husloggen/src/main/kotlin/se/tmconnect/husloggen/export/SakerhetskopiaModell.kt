package se.tmconnect.husloggen.export

import java.time.LocalDate
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.regler.tolkaBilder

const val HUSLOGGEN_SAKERHETSKOPIA_VERSION = 1
const val HUSLOGGEN_LOGG_JSON = "logg.json"
const val HUSLOGGEN_BILDER_MAPP = "bilder/"
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_UPPGIFTER = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_POSTER = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES = 20 * 1024 * 1024
const val HUSLOGGEN_AUTO_FILPREFIX = "husloggen-auto-"

data class SakerhetskopiaUppgift(
    val namn: String,
    val intervallManader: Int,
    val startDatum: LocalDate,
    val senastGjord: LocalDate? = null,
    val anteckning: String? = null,
)

data class SakerhetskopiaPost(
    val datum: LocalDate,
    val vad: String,
    val typ: String,
    val beloppOre: Long? = null,
    val utforare: String? = null,
    val anteckning: String? = null,
    val uppgiftIndex: Int? = null,
    val bilder: List<String> = emptyList(),
)

data class SakerhetskopiaInnehall(
    val version: Int,
    val skapad: String,
    val uppgifter: List<SakerhetskopiaUppgift>,
    val poster: List<SakerhetskopiaPost>,
)

data class SakerhetskopiaImportResultat(
    val tillagda: Int,
    val hoppadeOver: Int,
)

fun uppgiftTillSakerhetskopia(uppgift: Uppgift): SakerhetskopiaUppgift =
    SakerhetskopiaUppgift(
        namn = uppgift.namn,
        intervallManader = uppgift.intervallManader,
        startDatum = uppgift.startDatum,
        senastGjord = uppgift.senastGjord,
        anteckning = uppgift.anteckning,
    )

fun sakerhetskopiaUppgiftTillUppgift(export: SakerhetskopiaUppgift): Uppgift =
    Uppgift(
        namn = export.namn.trim(),
        intervallManader = export.intervallManader,
        startDatum = export.startDatum,
        senastGjord = export.senastGjord,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
    )

fun postTillSakerhetskopia(post: Post, uppgiftIdTillIndex: Map<Long, Int>): SakerhetskopiaPost {
    val bilder = buildList {
        post.kvittoFil?.let { add(it) }
        addAll(tolkaBilder(post.bilder))
    }.distinct()
    return SakerhetskopiaPost(
        datum = post.datum,
        vad = post.vad,
        typ = post.typ.name,
        beloppOre = post.beloppOre,
        utforare = post.utforare,
        anteckning = post.anteckning,
        uppgiftIndex = post.uppgiftId?.let { uppgiftIdTillIndex[it] },
        bilder = bilder,
    )
}

fun sakerhetskopiaPostTillPost(
    export: SakerhetskopiaPost,
    uppgiftId: Long?,
    bildFilnamn: List<String>,
): Post {
    val kvitto = bildFilnamn.firstOrNull()
    val rest = bildFilnamn.drop(1)
    return Post(
        datum = export.datum,
        vad = export.vad.trim(),
        typ = PostTyp.franNamn(export.typ) ?: PostTyp.UNDERHALL,
        beloppOre = export.beloppOre,
        utforare = export.utforare?.trim()?.takeIf { it.isNotEmpty() },
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
        kvittoFil = kvitto,
        bilder = formateraBilder(rest),
        uppgiftId = uppgiftId,
    )
}

fun arSakerhetskopiaUppgiftLik(a: SakerhetskopiaUppgift, b: SakerhetskopiaUppgift): Boolean =
    a.namn == b.namn &&
        a.intervallManader == b.intervallManader &&
        a.startDatum == b.startDatum &&
        a.senastGjord == b.senastGjord &&
        a.anteckning == b.anteckning

data class SakerhetskopiaPostNyckel(
    val post: SakerhetskopiaPost,
    val uppgift: SakerhetskopiaUppgift?,
)

fun sakerhetskopiaPostNyckel(
    post: SakerhetskopiaPost,
    uppgifter: List<SakerhetskopiaUppgift>,
): SakerhetskopiaPostNyckel =
    SakerhetskopiaPostNyckel(
        post = post,
        uppgift = post.uppgiftIndex?.let { uppgifter.getOrNull(it) },
    )

fun arSakerhetskopiaPostNyckelLik(a: SakerhetskopiaPostNyckel, b: SakerhetskopiaPostNyckel): Boolean {
    val p1 = a.post
    val p2 = b.post
    return p1.datum == p2.datum &&
        p1.vad == p2.vad &&
        p1.typ == p2.typ &&
        p1.beloppOre == p2.beloppOre &&
        p1.utforare == p2.utforare &&
        p1.anteckning == p2.anteckning &&
        p1.bilder.size == p2.bilder.size &&
        when {
            a.uppgift == null && b.uppgift == null -> true
            a.uppgift != null && b.uppgift != null -> arSakerhetskopiaUppgiftLik(a.uppgift, b.uppgift)
            else -> false
        }
}

fun arSakerhetskopiaUppgiftDubblett(export: SakerhetskopiaUppgift, befintlig: Uppgift): Boolean =
    arSakerhetskopiaUppgiftLik(export, uppgiftTillSakerhetskopia(befintlig))

fun arSakerhetskopiaPostDubblett(
    export: SakerhetskopiaPost,
    exportUppgifter: List<SakerhetskopiaUppgift>,
    befintlig: Post,
    befintligUppgift: Uppgift?,
): Boolean {
    val exportNyckel = sakerhetskopiaPostNyckel(export, exportUppgifter)
    val befintligPost =
        SakerhetskopiaPost(
            datum = befintlig.datum,
            vad = befintlig.vad,
            typ = befintlig.typ.name,
            beloppOre = befintlig.beloppOre,
            utforare = befintlig.utforare,
            anteckning = befintlig.anteckning,
            uppgiftIndex = null,
            bilder = buildList {
                befintlig.kvittoFil?.let { add(it) }
                addAll(tolkaBilder(befintlig.bilder))
            }.distinct(),
        )
    val befintligNyckel =
        SakerhetskopiaPostNyckel(
            post = befintligPost,
            uppgift = befintligUppgift?.let { uppgiftTillSakerhetskopia(it) },
        )
    return arSakerhetskopiaPostNyckelLik(exportNyckel, befintligNyckel)
}
