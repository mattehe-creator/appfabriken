package se.tmconnect.husloggen.data

import kotlinx.coroutines.flow.Flow

class HusRepository(private val dao: HusDao) {
    val uppgifter: Flow<List<Uppgift>> = dao.allaUppgifterFlow()
    val poster: Flow<List<Post>> = dao.allaPosterFlow()

    suspend fun sparaUppgift(uppgift: Uppgift): Long =
        if (uppgift.id == 0L) {
            dao.infogaUppgift(uppgift)
        } else {
            dao.uppdateraUppgift(uppgift)
            uppgift.id
        }

    suspend fun taBortUppgift(uppgift: Uppgift) = dao.taBortUppgift(uppgift)

    suspend fun sparaPost(post: Post): Long =
        if (post.id == 0L) {
            dao.infogaPost(post)
        } else {
            dao.uppdateraPost(post)
            post.id
        }

    suspend fun taBortPost(post: Post) = dao.taBortPost(post)

    suspend fun antalPoster(): Int = dao.antalPoster()
}
