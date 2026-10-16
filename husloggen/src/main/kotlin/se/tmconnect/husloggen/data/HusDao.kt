package se.tmconnect.husloggen.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HusDao {
    @Query("SELECT * FROM uppgift")
    fun allaUppgifterFlow(): Flow<List<Uppgift>>

    @Query("SELECT * FROM uppgift ORDER BY id")
    suspend fun allaUppgifter(): List<Uppgift>

    @Query("SELECT * FROM uppgift WHERE id = :id")
    suspend fun hamtaUppgift(id: Long): Uppgift?

    @Insert
    suspend fun infogaUppgift(uppgift: Uppgift): Long

    @Update
    suspend fun uppdateraUppgift(uppgift: Uppgift)

    @Delete
    suspend fun taBortUppgift(uppgift: Uppgift)

    @Query("SELECT * FROM post ORDER BY datum DESC, skapad DESC")
    fun allaPosterFlow(): Flow<List<Post>>

    @Query("SELECT * FROM post ORDER BY id")
    suspend fun allaPoster(): List<Post>

    @Query("SELECT * FROM post WHERE id = :id")
    suspend fun hamtaPost(id: Long): Post?

    @Insert
    suspend fun infogaPost(post: Post): Long

    @Update
    suspend fun uppdateraPost(post: Post)

    @Delete
    suspend fun taBortPost(post: Post)

    @Query("SELECT COUNT(*) FROM post")
    suspend fun antalPoster(): Int

    @Transaction
    suspend fun markeraSomGjord(uppdateradUppgift: Uppgift, nyPost: Post) {
        uppdateraUppgift(uppdateradUppgift)
        infogaPost(nyPost)
    }

    @Transaction
    suspend fun importeraSakerhetskopiaData(uppgifter: List<Uppgift>, poster: List<Post>) {
        uppgifter.forEach { infogaUppgift(it) }
        poster.forEach { infogaPost(it) }
    }
}
