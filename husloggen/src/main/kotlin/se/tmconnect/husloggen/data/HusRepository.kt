package se.tmconnect.husloggen.data

import android.net.Uri
import androidx.room.withTransaction
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import se.tmconnect.husloggen.export.SakerhetskopiaImportResultat
import se.tmconnect.husloggen.export.SakerhetskopiaInnehall
import se.tmconnect.husloggen.export.SakerhetskopiaPost
import se.tmconnect.husloggen.export.SakerhetskopiaUppgift
import se.tmconnect.husloggen.export.arSakerhetskopiaPostDubblett
import se.tmconnect.husloggen.export.arSakerhetskopiaPostNyckelLik
import se.tmconnect.husloggen.export.arSakerhetskopiaUppgiftDubblett
import se.tmconnect.husloggen.export.arSakerhetskopiaUppgiftLik
import se.tmconnect.husloggen.export.sakerhetskopiaPostNyckel
import se.tmconnect.husloggen.export.sakerhetskopiaPostTillPost
import se.tmconnect.husloggen.export.sakerhetskopiaUppgiftTillUppgift
import se.tmconnect.husloggen.export.uppgiftTillSakerhetskopia
import se.tmconnect.husloggen.regler.formateraBilder
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

class HusRepository(
    private val databas: HusDatabas,
    private val dao: HusDao,
    private val bildLager: BildLager,
) {
    val uppgifter: Flow<List<Uppgift>> = dao.allaUppgifterFlow()
    val poster: Flow<List<Post>> = dao.allaPosterFlow()

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

    suspend fun harData(): Boolean = dao.antalPoster() > 0 || dao.antalUppgifter() > 0

    suspend fun allaUppgifter(): List<Uppgift> = dao.allaUppgifter()

    suspend fun allaPoster(): List<Post> = dao.allaPoster()

    suspend fun importeraSakerhetskopia(
        innehall: SakerhetskopiaInnehall,
        bildFiler: Map<String, ByteArray>,
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
        databas.withTransaction {
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
                val nyttId = dao.infogaUppgift(sakerhetskopiaUppgiftTillUppgift(export))
                indexTillUppgiftId[index] = nyttId
            }
            for ((export, nyaBilder) in posterAttInfoga) {
                val uppgiftId = export.uppgiftIndex?.let { indexTillUppgiftId[it] }
                dao.infogaPost(sakerhetskopiaPostTillPost(export, uppgiftId, nyaBilder))
                tillagda++
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
