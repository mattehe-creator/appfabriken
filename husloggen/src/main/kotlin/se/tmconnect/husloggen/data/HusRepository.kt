package se.tmconnect.husloggen.data

import android.net.Uri
import androidx.room.withTransaction
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import se.tmconnect.husloggen.export.sakerhetskopiaBostadTillBostad
import se.tmconnect.husloggen.export.sakerhetskopiaDokumentTillDokument
import se.tmconnect.husloggen.export.SakerhetskopiaImportResultat
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaPost
import se.tmconnect.husloggen.export.SakerhetskopiaUppgift
import se.tmconnect.husloggen.export.sakerhetskopiaAvlasningTillAvlasning
import se.tmconnect.husloggen.export.sakerhetskopiaMatareTillMatare
import se.tmconnect.husloggen.export.arSakerhetskopiaPostDubblett
import se.tmconnect.husloggen.export.arSakerhetskopiaPostNyckelLik
import se.tmconnect.husloggen.export.arSakerhetskopiaUppgiftDubblett
import se.tmconnect.husloggen.export.arSakerhetskopiaUppgiftLik
import se.tmconnect.husloggen.export.sakerhetskopiaPostNyckel
import se.tmconnect.husloggen.export.sakerhetskopiaPostTillPost
import se.tmconnect.husloggen.export.sakerhetskopiaUppgiftTillUppgift
import se.tmconnect.husloggen.export.uppgiftTillSakerhetskopia
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.regler.kanLaggaTillBostad
import se.tmconnect.husloggen.regler.kanLaggaTillPost
import se.tmconnect.husloggen.regler.tolkaBilder
import se.tmconnect.karna.bild.BildLager

sealed class SparaPostResult {
    data class Sparad(val id: Long) : SparaPostResult()

    data object GransNadd : SparaPostResult()
}

sealed class MarkeraGjordResult {
    data object MedPost : MarkeraGjordResult()

    /** Uppgiften uppdaterades men ingen loggpost sparades (gräns utan Pro). */
    data object UtanPostGransNadd : MarkeraGjordResult()
}

sealed class SparaBostadResult {
    data class Sparad(val id: Long) : SparaBostadResult()

    data object GransBostaderUtanPro : SparaBostadResult()
}

sealed class TaBortBostadResult {
    data object Borttagen : TaBortBostadResult()

    /** Minst en bostad måste finnas kvar. */
    data object SistaBostaden : TaBortBostadResult()
}

data class BostadRaderingInfo(
    val antalPoster: Int,
)

class HusRepository(
    private val databas: HusDatabas,
    private val dao: HusDao,
    private val bildLager: BildLager,
    private val dokumentLager: DokumentLager,
) {
    val bostader: Flow<List<Bostad>> = dao.allaBostaderFlow()

    fun uppgifterForBostad(bostadId: Long): Flow<List<Uppgift>> = dao.uppgifterForBostadFlow(bostadId)

    fun posterForBostad(bostadId: Long): Flow<List<Post>> = dao.posterForBostadFlow(bostadId)

    fun matareForBostad(bostadId: Long): Flow<List<Matare>> = dao.matareForBostadFlow(bostadId)

    fun dokumentForBostad(bostadId: Long): Flow<List<Dokument>> = dao.dokumentForBostadFlow(bostadId)

    suspend fun hamtaBostad(id: Long): Bostad? = dao.hamtaBostad(id)

    suspend fun allaBostader(): List<Bostad> = dao.allaBostader()

    suspend fun antalBostader(): Int = dao.antalBostader()

    suspend fun hamtaForvaldBostadId(): Long {
        val lista = dao.allaBostader()
        return lista.firstOrNull()?.id ?: error("Ingen bostad i databasen")
    }

    suspend fun sparaBostad(bostad: Bostad, arPro: Boolean): SparaBostadResult {
        if (bostad.id == 0L) {
            if (!kanLaggaTillBostad(dao.antalBostader(), arPro)) {
                return SparaBostadResult.GransBostaderUtanPro
            }
            return SparaBostadResult.Sparad(
                dao.infogaBostad(
                    bostad.copy(
                        namn = bostad.namn.trim(),
                        adress = bostad.adress?.trim()?.takeIf { it.isNotEmpty() },
                        anteckning = bostad.anteckning?.trim()?.takeIf { it.isNotEmpty() },
                    ),
                ),
            )
        }
        dao.uppdateraBostad(
            bostad.copy(
                namn = bostad.namn.trim(),
                adress = bostad.adress?.trim()?.takeIf { it.isNotEmpty() },
                anteckning = bostad.anteckning?.trim()?.takeIf { it.isNotEmpty() },
            ),
        )
        return SparaBostadResult.Sparad(bostad.id)
    }

    suspend fun bostadRaderingInfo(bostadId: Long): BostadRaderingInfo =
        BostadRaderingInfo(antalPoster = dao.antalPosterISammanhangForBostad(bostadId))

    suspend fun taBortBostad(bostad: Bostad): TaBortBostadResult {
        if (dao.antalBostader() <= 1) {
            return TaBortBostadResult.SistaBostaden
        }
        dao.taBortBostad(bostad)
        return TaBortBostadResult.Borttagen
    }

    fun avlasningarForMatare(matareId: Long): Flow<List<Avlasning>> =
        dao.avlasningarForMatareFlow(matareId)

    suspend fun hamtaUppgift(id: Long): Uppgift? = dao.hamtaUppgift(id)

    suspend fun sparaUppgift(uppgift: Uppgift): Long =
        if (uppgift.id == 0L) {
            dao.infogaUppgift(uppgift)
        } else {
            dao.uppdateraUppgift(uppgift)
            uppgift.id
        }

    suspend fun uppdateraUppgift(uppgift: Uppgift) = dao.uppdateraUppgift(uppgift)

    suspend fun taBortUppgift(uppgift: Uppgift) = dao.taBortUppgift(uppgift)

    suspend fun markeraSomGjord(uppgift: Uppgift, idag: LocalDate, arPro: Boolean): MarkeraGjordResult {
        val uppdaterad = uppgift.copy(senastGjord = idag)
        if (!kanLaggaTillPost(dao.antalPoster(), arPro)) {
            dao.uppdateraUppgift(uppdaterad)
            return MarkeraGjordResult.UtanPostGransNadd
        }
        val post = Post(
            bostadId = uppgift.bostadId,
            datum = idag,
            vad = uppgift.namn,
            typ = PostTyp.UNDERHALL,
            uppgiftId = uppgift.id,
        )
        dao.markeraSomGjord(uppdaterad, post)
        return MarkeraGjordResult.MedPost
    }

    suspend fun hamtaPost(id: Long): Post? = dao.hamtaPost(id)

    fun lasBilder(post: Post): List<String> = lasBilder(post.bilder)

    fun lasBilder(bilder: String?): List<String> = tolkaBilder(bilder)

    suspend fun laggTillBild(postId: Long, kallaUri: Uri): Boolean {
        val filnamn = bildLager.kopieraFranUri(kallaUri) ?: return false
        val post = dao.hamtaPost(postId)
        if (post == null) {
            bildLager.taBort(filnamn)
            return false
        }
        val lista = lasBilder(post)
        if (lista.size >= MAX_BILDER_PER_POST) {
            bildLager.taBort(filnamn)
            return false
        }
        dao.uppdateraPost(post.copy(bilder = formateraBilder(lista + filnamn)))
        return true
    }

    suspend fun taBortBild(postId: Long, filnamn: String) {
        val post = dao.hamtaPost(postId) ?: return
        val kvar = lasBilder(post) - filnamn
        dao.uppdateraPost(post.copy(bilder = formateraBilder(kvar)))
        bildLager.taBort(filnamn)
    }

    suspend fun sparaPost(post: Post, arPro: Boolean): SparaPostResult {
        if (post.id == 0L) {
            if (!kanLaggaTillPost(dao.antalPoster(), arPro)) {
                return SparaPostResult.GransNadd
            }
            return SparaPostResult.Sparad(dao.infogaPost(post))
        }
        dao.uppdateraPost(post)
        return SparaPostResult.Sparad(post.id)
    }

    suspend fun uppdateraPost(post: Post) = dao.uppdateraPost(post)

    suspend fun taBortPost(post: Post) {
        lasBilder(post).forEach { bildLager.taBort(it) }
        dao.taBortPost(post)
    }

    suspend fun antalPoster(): Int = dao.antalPoster()

    fun totaltAntalPosterFlow(): Flow<Int> = dao.antalPosterFlow()

    suspend fun harData(): Boolean =
        dao.antalPoster() > 0 || dao.antalUppgifter() > 0 || dao.antalMatare() > 0

    suspend fun hamtaMatare(id: Long): Matare? = dao.hamtaMatare(id)

    suspend fun sparaMatare(matare: Matare): Long =
        if (matare.id == 0L) {
            dao.infogaMatare(matare)
        } else {
            dao.uppdateraMatare(matare)
            matare.id
        }

    suspend fun taBortMatare(matare: Matare) = dao.taBortMatare(matare)

    suspend fun sparaAvlasning(avlasning: Avlasning): Long =
        if (avlasning.id == 0L) {
            dao.infogaAvlasning(avlasning)
        } else {
            dao.uppdateraAvlasning(avlasning)
            avlasning.id
        }

    suspend fun taBortAvlasning(avlasning: Avlasning) = dao.taBortAvlasning(avlasning)

    suspend fun avlasningarForMatareLista(matareId: Long): List<Avlasning> =
        dao.avlasningarForMatare(matareId)

    suspend fun allaMatare(): List<Matare> = dao.allaMatare()

    suspend fun allaAvlasningar(): List<Avlasning> = dao.allaAvlasningar()

    suspend fun allaUppgifter(): List<Uppgift> = dao.allaUppgifter()

    suspend fun allaPoster(): List<Post> = dao.allaPoster()

    suspend fun allaDokument(): List<Dokument> = dao.allaDokument()

    suspend fun hamtaDokument(id: Long): Dokument? = dao.hamtaDokument(id)

    suspend fun sparaDokument(dokument: Dokument): Long =
        if (dokument.id == 0L) {
            dao.infogaDokument(dokument)
        } else {
            dao.uppdateraDokument(dokument)
            dokument.id
        }

    suspend fun taBortDokument(dokument: Dokument) {
        dokumentLager.taBort(dokument.filnamn)
        dao.taBortDokument(dokument)
    }

    fun kopieraDokumentFranUri(kallaUri: Uri): KopieraDokumentResult =
        dokumentLager.kopieraFranUri(kallaUri)

    fun taBortDokumentfil(filnamn: String) = dokumentLager.taBort(filnamn)

    fun dokumentUri(filnamn: String) = dokumentLager.innehallUriForFilnamn(filnamn)

    fun dokumentfilFinns(filnamn: String) = dokumentLager.filFinns(filnamn)

    suspend fun importeraSakerhetskopia(
        innehall: SakerhetskopiaInnehall,
        bildFiler: Map<String, ByteArray>,
        dokumentFiler: Map<String, ByteArray> = emptyMap(),
    ): SakerhetskopiaImportResultat {
        val befintligaUppgifter = dao.allaUppgifter()
        val befintligaPoster = dao.allaPoster()
        val uppgiftLookup = befintligaUppgifter.associateBy { it.id }

        val posterAttInfoga = mutableListOf<Pair<SakerhetskopiaPost, List<String>>>()
        var hoppadeOver = 0
        val tillagdaPostNycklar = mutableListOf<se.tmconnect.husloggen.export.SakerhetskopiaPostNyckel>()

        for (export in innehall.poster) {
            val arDubblettMotDb =
                befintligaPoster.any { post ->
                    arSakerhetskopiaPostDubblett(
                        export,
                        innehall.uppgifter,
                        post,
                        post.uppgiftId?.let { uppgiftLookup[it] },
                    )
                }
            val exportNyckel = sakerhetskopiaPostNyckel(export, innehall.uppgifter)
            val arDubblettMotFil = tillagdaPostNycklar.any { arSakerhetskopiaPostNyckelLik(exportNyckel, it) }
            if (arDubblettMotDb || arDubblettMotFil) {
                hoppadeOver++
                continue
            }
            export.bilder.forEach { filnamn ->
                if (filnamn !in bildFiler) {
                    error("Saknad bild i säkerhetskopia: $filnamn")
                }
            }
            posterAttInfoga.add(export to export.bilder.map { bildLager.sparaFranBytes(bildFiler[it]!!, it) })
            tillagdaPostNycklar.add(exportNyckel)
        }

        var tillagda = 0
        val forvaldBostadId = hamtaForvaldBostadId()
        databas.withTransaction {
            val bostadIndexTillId = mutableMapOf<Int, Long>()
            if (innehall.bostader.isEmpty()) {
                bostadIndexTillId[0] = forvaldBostadId
            } else {
                innehall.bostader.forEachIndexed { index, export ->
                    val nyttId = dao.infogaBostad(sakerhetskopiaBostadTillBostad(export))
                    bostadIndexTillId[index] = nyttId
                }
            }
            fun bostadIdForIndex(index: Int): Long =
                bostadIndexTillId[index] ?: bostadIndexTillId[0] ?: forvaldBostadId

            val indexTillUppgiftId = mutableMapOf<Int, Long>()
            innehall.uppgifter.forEachIndexed { index, export ->
                befintligaUppgifter.firstOrNull { arSakerhetskopiaUppgiftDubblett(export, it) }?.let {
                    indexTillUppgiftId[index] = it.id
                    return@forEachIndexed
                }
                val tidigareIndex =
                    innehall.uppgifter.withIndex().firstOrNull { (i, u) ->
                        i < index && arSakerhetskopiaUppgiftLik(export, u) && indexTillUppgiftId.containsKey(i)
                    }?.index
                if (tidigareIndex != null) {
                    indexTillUppgiftId[index] = indexTillUppgiftId[tidigareIndex]!!
                    return@forEachIndexed
                }
                val nyttId =
                    dao.infogaUppgift(
                        sakerhetskopiaUppgiftTillUppgift(export, bostadIdForIndex(export.bostadIndex)),
                    )
                indexTillUppgiftId[index] = nyttId
            }
            for ((export, nyaBilder) in posterAttInfoga) {
                val uppgiftId = export.uppgiftIndex?.let { indexTillUppgiftId[it] }
                dao.infogaPost(
                    sakerhetskopiaPostTillPost(
                        export,
                        uppgiftId,
                        nyaBilder,
                        bostadIdForIndex(export.bostadIndex),
                    ),
                )
                tillagda++
            }
            val matareIndexTillId = mutableMapOf<Int, Long>()
            innehall.matare.forEachIndexed { index, export ->
                val nyttId =
                    dao.infogaMatare(
                        sakerhetskopiaMatareTillMatare(export, bostadIdForIndex(export.bostadIndex)),
                    )
                matareIndexTillId[index] = nyttId
            }
            for (export in innehall.avlasningar) {
                val matareId = matareIndexTillId[export.matareIndex] ?: continue
                dao.infogaAvlasning(sakerhetskopiaAvlasningTillAvlasning(export, matareId))
            }
            for (export in innehall.dokument) {
                export.filnamn.let { filnamn ->
                    if (filnamn !in dokumentFiler) {
                        error("Saknad dokumentfil i säkerhetskopia: $filnamn")
                    }
                }
                val nyttFilnamn =
                    dokumentLager.sparaFranBytes(
                        dokumentFiler.getValue(export.filnamn),
                        export.filnamn,
                    )
                dao.infogaDokument(
                    sakerhetskopiaDokumentTillDokument(
                        export,
                        nyttFilnamn,
                        bostadIdForIndex(export.bostadIndex),
                    ),
                )
            }
        }
        return SakerhetskopiaImportResultat(tillagda = tillagda, hoppadeOver = hoppadeOver)
    }

    fun kopieraBildTillLagring(kallaUri: Uri): String? = bildLager.kopieraFranUri(kallaUri)

    fun taBortBildfil(filnamn: String) = bildLager.taBort(filnamn)

    fun bildUri(filnamn: String) = bildLager.innehallUriForFilnamn(filnamn)

    fun skapaKameraFotoUri() = bildLager.skapaKameraFotoUri()

    fun bildfilFinns(filnamn: String) = bildLager.filFinns(filnamn)

    companion object {
        const val MAX_BILDER_PER_POST = se.tmconnect.husloggen.regler.MAX_BILDER_PER_POST
    }
}

fun skapaHusRepository(context: android.content.Context, databas: HusDatabas): HusRepository =
    HusRepository(
        databas = databas,
        dao = databas.husDao(),
        bildLager = skapaHusBildLager(context),
        dokumentLager = skapaHusDokumentLager(context),
    )
