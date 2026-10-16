package se.tmconnect.husloggen.data

import android.net.Uri
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import se.tmconnect.husloggen.regler.formateraBilder
import se.tmconnect.husloggen.regler.tolkaBilder
import se.tmconnect.karna.bild.BildLager

class HusRepository(
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

    suspend fun markeraSomGjord(uppgift: Uppgift, idag: LocalDate) {
        val uppdaterad = uppgift.copy(senastGjord = idag)
        val post = Post(
            datum = idag,
            vad = uppgift.namn,
            typ = PostTyp.UNDERHALL,
            uppgiftId = uppgift.id,
        )
        dao.markeraSomGjord(uppdaterad, post)
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

    suspend fun sparaPost(post: Post): Long =
        if (post.id == 0L) {
            dao.infogaPost(post)
        } else {
            dao.uppdateraPost(post)
            post.id
        }

    suspend fun uppdateraPost(post: Post) = dao.uppdateraPost(post)

    suspend fun taBortPost(post: Post) {
        lasBilder(post).forEach { bildLager.taBort(it) }
        dao.taBortPost(post)
    }

    suspend fun antalPoster(): Int = dao.antalPoster()

    fun kopieraBildTillLagring(kallaUri: Uri): String? = bildLager.kopieraFranUri(kallaUri)

    fun taBortBildfil(filnamn: String) = bildLager.taBort(filnamn)

    fun bildUri(filnamn: String) = bildLager.innehallUriForFilnamn(filnamn)

    fun skapaKameraFotoUri() = bildLager.skapaKameraFotoUri()

    fun bildfilFinns(filnamn: String) = bildLager.filFinns(filnamn)

    companion object {
        const val MAX_BILDER_PER_POST = 10
    }
}
