package se.tmconnect.husloggen.export

import java.time.LocalDate
import se.tmconnect.husloggen.data.Avlasning
import se.tmconnect.husloggen.data.Bostad
import se.tmconnect.husloggen.data.Dokument
import se.tmconnect.husloggen.data.DokumentKategori
import se.tmconnect.husloggen.data.Matare
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.regler.tolkaBilder

const val HUSLOGGEN_SAKERHETSKOPIA_VERSION = 5
const val HUSLOGGEN_SAKERHETSKOPIA_MIN_VERSION = 1
const val HUSLOGGEN_LOGG_JSON = "logg.json"
const val HUSLOGGEN_BILDER_MAPP = "bilder/"
const val HUSLOGGEN_DOKUMENT_MAPP = "dokument/"
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_BOSTADER = 50
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_UPPGIFTER = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_POSTER = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_MATARE = 500
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_AVLASNINGAR = 50_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_DOKUMENT = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_PLANERADE_KOSTNADER = 5_000
const val HUSLOGGEN_SAKERHETSKOPIA_MAX_FIL_BYTES = 25 * 1024 * 1024
const val HUSLOGGEN_AUTO_FILPREFIX = "husloggen-auto-"

data class SakerhetskopiaBostad(
    val namn: String,
    val adress: String? = null,
    val anteckning: String? = null,
)

data class SakerhetskopiaUppgift(
    val namn: String,
    val intervallManader: Int,
    val startDatum: LocalDate,
    val senastGjord: LocalDate? = null,
    val anteckning: String? = null,
    val bostadIndex: Int = 0,
)

data class SakerhetskopiaPost(
    val datum: LocalDate,
    val vad: String,
    val typ: String,
    val beloppOre: Long? = null,
    val rotRutAvdragOre: Long? = null,
    val utforare: String? = null,
    val anteckning: String? = null,
    val uppgiftIndex: Int? = null,
    val bilder: List<String> = emptyList(),
    val bostadIndex: Int = 0,
)

data class SakerhetskopiaMatare(
    val namn: String,
    val enhet: String,
    val anteckning: String? = null,
    val prisOrePerEnhet: Long? = null,
    val bostadIndex: Int = 0,
)

data class SakerhetskopiaAvlasning(
    val matareIndex: Int,
    val datum: LocalDate,
    val vardeTusendel: Long,
)

data class SakerhetskopiaDokument(
    val titel: String,
    val kategori: String? = null,
    val anteckning: String? = null,
    val filnamn: String,
    val mimeTyp: String,
    val tillagd: LocalDate,
    val bostadIndex: Int = 0,
)

data class SakerhetskopiaPlaneradKostnad(
    val titel: String,
    val beloppOre: Long,
    val planeratAr: Int,
    val anteckning: String? = null,
    val bostadIndex: Int = 0,
)

data class SakerhetskopiaInnehall(
    val version: Int,
    val skapad: String,
    val bostader: List<SakerhetskopiaBostad> = emptyList(),
    val uppgifter: List<SakerhetskopiaUppgift>,
    val poster: List<SakerhetskopiaPost>,
    val matare: List<SakerhetskopiaMatare> = emptyList(),
    val avlasningar: List<SakerhetskopiaAvlasning> = emptyList(),
    val dokument: List<SakerhetskopiaDokument> = emptyList(),
    val planeradeKostnader: List<SakerhetskopiaPlaneradKostnad> = emptyList(),
)

data class SakerhetskopiaImportResultat(
    val tillagda: Int,
    val hoppadeOver: Int,
)

fun bostadTillSakerhetskopia(bostad: Bostad): SakerhetskopiaBostad =
    SakerhetskopiaBostad(
        namn = bostad.namn,
        adress = bostad.adress,
        anteckning = bostad.anteckning,
    )

fun sakerhetskopiaBostadTillBostad(export: SakerhetskopiaBostad): Bostad =
    Bostad(
        namn = export.namn.trim(),
        adress = export.adress?.trim()?.takeIf { it.isNotEmpty() },
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
    )

fun uppgiftTillSakerhetskopia(uppgift: Uppgift, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaUppgift =
    SakerhetskopiaUppgift(
        namn = uppgift.namn,
        intervallManader = uppgift.intervallManader,
        startDatum = uppgift.startDatum,
        senastGjord = uppgift.senastGjord,
        anteckning = uppgift.anteckning,
        bostadIndex = bostadIdTillIndex.getValue(uppgift.bostadId),
    )

fun sakerhetskopiaUppgiftTillUppgift(export: SakerhetskopiaUppgift, bostadId: Long): Uppgift =
    Uppgift(
        bostadId = bostadId,
        namn = export.namn.trim(),
        intervallManader = export.intervallManader,
        startDatum = export.startDatum,
        senastGjord = export.senastGjord,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
    )

fun postTillSakerhetskopia(
    post: Post,
    uppgiftIdTillIndex: Map<Long, Int>,
    bostadIdTillIndex: Map<Long, Int>,
): SakerhetskopiaPost {
    val bilder = buildList {
        post.kvittoFil?.let { add(it) }
        addAll(tolkaBilder(post.bilder))
    }.distinct()
    return SakerhetskopiaPost(
        datum = post.datum,
        vad = post.vad,
        typ = post.typ.name,
        beloppOre = post.beloppOre,
        rotRutAvdragOre = post.rotRutAvdragOre,
        utforare = post.utforare,
        anteckning = post.anteckning,
        uppgiftIndex = post.uppgiftId?.let { uppgiftIdTillIndex[it] },
        bilder = bilder,
        bostadIndex = bostadIdTillIndex.getValue(post.bostadId),
    )
}

fun sakerhetskopiaPostTillPost(
    export: SakerhetskopiaPost,
    uppgiftId: Long?,
    bildFilnamn: List<String>,
    bostadId: Long,
): Post {
    val kvitto = bildFilnamn.firstOrNull()
    val rest = bildFilnamn.drop(1)
    return Post(
        bostadId = bostadId,
        datum = export.datum,
        vad = export.vad.trim(),
        typ = PostTyp.franNamn(export.typ) ?: PostTyp.UNDERHALL,
        beloppOre = export.beloppOre,
        rotRutAvdragOre = export.rotRutAvdragOre,
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
        p1.rotRutAvdragOre == p2.rotRutAvdragOre &&
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
    arSakerhetskopiaUppgiftLik(
        export,
        uppgiftTillSakerhetskopia(befintlig, mapOf(befintlig.bostadId to 0)),
    )

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
            rotRutAvdragOre = befintlig.rotRutAvdragOre,
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
            uppgift = befintligUppgift?.let {
                uppgiftTillSakerhetskopia(it, mapOf(it.bostadId to 0))
            },
        )
    return arSakerhetskopiaPostNyckelLik(exportNyckel, befintligNyckel)
}

fun matareTillSakerhetskopia(matare: Matare, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaMatare =
    SakerhetskopiaMatare(
        namn = matare.namn,
        enhet = matare.enhet,
        anteckning = matare.anteckning,
        prisOrePerEnhet = matare.prisOrePerEnhet,
        bostadIndex = bostadIdTillIndex.getValue(matare.bostadId),
    )

fun sakerhetskopiaMatareTillMatare(export: SakerhetskopiaMatare, bostadId: Long): Matare =
    Matare(
        bostadId = bostadId,
        namn = export.namn.trim(),
        enhet = export.enhet.trim(),
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
        prisOrePerEnhet = export.prisOrePerEnhet,
    )

fun avlasningTillSakerhetskopia(avlasning: Avlasning, matareIdTillIndex: Map<Long, Int>): SakerhetskopiaAvlasning =
    SakerhetskopiaAvlasning(
        matareIndex = matareIdTillIndex.getValue(avlasning.matareId),
        datum = avlasning.datum,
        vardeTusendel = avlasning.varde,
    )

fun sakerhetskopiaAvlasningTillAvlasning(export: SakerhetskopiaAvlasning, matareId: Long): Avlasning =
    Avlasning(
        matareId = matareId,
        datum = export.datum,
        varde = export.vardeTusendel,
    )

fun arSakerhetskopiaMatareLik(a: SakerhetskopiaMatare, b: SakerhetskopiaMatare): Boolean =
    a.namn == b.namn &&
        a.enhet == b.enhet &&
        a.anteckning == b.anteckning &&
        a.prisOrePerEnhet == b.prisOrePerEnhet

fun arSakerhetskopiaAvlasningLik(a: SakerhetskopiaAvlasning, b: SakerhetskopiaAvlasning): Boolean =
    a.matareIndex == b.matareIndex && a.datum == b.datum && a.vardeTusendel == b.vardeTusendel

fun dokumentTillSakerhetskopia(dokument: Dokument, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaDokument =
    SakerhetskopiaDokument(
        titel = dokument.titel,
        kategori = dokument.kategori?.name,
        anteckning = dokument.anteckning,
        filnamn = dokument.filnamn,
        mimeTyp = dokument.mimeTyp,
        tillagd = dokument.tillagd,
        bostadIndex = bostadIdTillIndex.getValue(dokument.bostadId),
    )

fun sakerhetskopiaDokumentTillDokument(export: SakerhetskopiaDokument, filnamn: String, bostadId: Long): Dokument =
    Dokument(
        bostadId = bostadId,
        titel = export.titel.trim(),
        kategori = export.kategori?.let { runCatching { DokumentKategori.valueOf(it) }.getOrNull() },
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
        filnamn = filnamn,
        mimeTyp = export.mimeTyp,
        tillagd = export.tillagd,
    )

fun planeradKostnadTillSakerhetskopia(
    planeradKostnad: PlaneradKostnad,
    bostadIdTillIndex: Map<Long, Int>,
): SakerhetskopiaPlaneradKostnad =
    SakerhetskopiaPlaneradKostnad(
        titel = planeradKostnad.titel,
        beloppOre = planeradKostnad.beloppOre,
        planeratAr = planeradKostnad.planeratAr,
        anteckning = planeradKostnad.anteckning,
        bostadIndex = bostadIdTillIndex.getValue(planeradKostnad.bostadId),
    )

fun sakerhetskopiaPlaneradKostnadTillPlaneradKostnad(
    export: SakerhetskopiaPlaneradKostnad,
    bostadId: Long,
): PlaneradKostnad =
    PlaneradKostnad(
        bostadId = bostadId,
        titel = export.titel.trim(),
        beloppOre = export.beloppOre,
        planeratAr = export.planeratAr,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
    )
