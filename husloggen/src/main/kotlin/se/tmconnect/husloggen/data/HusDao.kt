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

    @Query("SELECT COUNT(*) FROM uppgift")
    suspend fun antalUppgifter(): Int

    @Transaction
    suspend fun markeraSomGjord(uppdateradUppgift: Uppgift, nyPost: Post) {
        uppdateraUppgift(uppdateradUppgift)
        infogaPost(nyPost)
    }

    @Query("SELECT * FROM matare ORDER BY namn COLLATE NOCASE, id")
    fun allaMatareFlow(): Flow<List<Matare>>

    @Query("SELECT * FROM matare ORDER BY id")
    suspend fun allaMatare(): List<Matare>

    @Query("SELECT * FROM matare WHERE id = :id")
    suspend fun hamtaMatare(id: Long): Matare?

    @Insert
    suspend fun infogaMatare(matare: Matare): Long

    @Update
    suspend fun uppdateraMatare(matare: Matare)

    @Delete
    suspend fun taBortMatare(matare: Matare)

    @Query("SELECT * FROM avlasning WHERE matare_id = :matareId ORDER BY datum DESC, id DESC")
    fun avlasningarForMatareFlow(matareId: Long): Flow<List<Avlasning>>

    @Query("SELECT * FROM avlasning WHERE matare_id = :matareId ORDER BY datum, id")
    suspend fun avlasningarForMatare(matareId: Long): List<Avlasning>

    @Query("SELECT * FROM avlasning ORDER BY id")
    suspend fun allaAvlasningar(): List<Avlasning>

    @Insert
    suspend fun infogaAvlasning(avlasning: Avlasning): Long

    @Update
    suspend fun uppdateraAvlasning(avlasning: Avlasning)

    @Delete
    suspend fun taBortAvlasning(avlasning: Avlasning)

    @Query("SELECT COUNT(*) FROM matare")
    suspend fun antalMatare(): Int

    @Query(
        """
        SELECT * FROM dokument
        ORDER BY
            CASE WHEN kategori IS NULL THEN 1 ELSE 0 END,
            kategori COLLATE NOCASE,
            titel COLLATE NOCASE,
            id
        """,
    )
    fun allaDokumentFlow(): Flow<List<Dokument>>

    @Query("SELECT * FROM dokument ORDER BY id")
    suspend fun allaDokument(): List<Dokument>

    @Query("SELECT * FROM dokument WHERE id = :id")
    suspend fun hamtaDokument(id: Long): Dokument?

    @Insert
    suspend fun infogaDokument(dokument: Dokument): Long

    @Update
    suspend fun uppdateraDokument(dokument: Dokument)

    @Delete
    suspend fun taBortDokument(dokument: Dokument)

    @Query(
        """
        SELECT * FROM planerad_kostnad
        ORDER BY planerat_ar, titel COLLATE NOCASE, id
        """,
    )
    fun allaPlaneradeKostnaderFlow(): Flow<List<PlaneradKostnad>>

    @Query("SELECT * FROM planerad_kostnad ORDER BY id")
    suspend fun allaPlaneradeKostnader(): List<PlaneradKostnad>

    @Query("SELECT * FROM planerad_kostnad WHERE id = :id")
    suspend fun hamtaPlaneradKostnad(id: Long): PlaneradKostnad?

    @Insert
    suspend fun infogaPlaneradKostnad(planeradKostnad: PlaneradKostnad): Long

    @Update
    suspend fun uppdateraPlaneradKostnad(planeradKostnad: PlaneradKostnad)

    @Delete
    suspend fun taBortPlaneradKostnad(planeradKostnad: PlaneradKostnad)

    @Transaction
    suspend fun importeraSakerhetskopiaData(uppgifter: List<Uppgift>, poster: List<Post>) {
        uppgifter.forEach { infogaUppgift(it) }
        poster.forEach { infogaPost(it) }
    }

}
