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
    @Query("SELECT * FROM bostad ORDER BY id")
    fun allaBostaderFlow(): Flow<List<Bostad>>

    @Query("SELECT * FROM bostad ORDER BY id")
    suspend fun allaBostader(): List<Bostad>

    @Query("SELECT * FROM bostad WHERE id = :id")
    suspend fun hamtaBostad(id: Long): Bostad?

    @Insert
    suspend fun infogaBostad(bostad: Bostad): Long

    @Update
    suspend fun uppdateraBostad(bostad: Bostad)

    @Delete
    suspend fun taBortBostad(bostad: Bostad)

    @Query("SELECT COUNT(*) FROM bostad")
    suspend fun antalBostader(): Int

    @Query(
        """
        SELECT COUNT(*) FROM (
            SELECT id FROM post WHERE bostad_id = :bostadId
            UNION ALL
            SELECT id FROM uppgift WHERE bostad_id = :bostadId
            UNION ALL
            SELECT id FROM matare WHERE bostad_id = :bostadId
            UNION ALL
            SELECT id FROM dokument WHERE bostad_id = :bostadId
        )
        """,
    )
    suspend fun antalPosterISammanhangForBostad(bostadId: Long): Int

    @Query("SELECT * FROM uppgift WHERE bostad_id = :bostadId")
    fun uppgifterForBostadFlow(bostadId: Long): Flow<List<Uppgift>>

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

    @Query("SELECT * FROM post WHERE bostad_id = :bostadId ORDER BY datum DESC, skapad DESC")
    fun posterForBostadFlow(bostadId: Long): Flow<List<Post>>

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

    @Query("SELECT COUNT(*) FROM post")
    fun antalPosterFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM uppgift")
    suspend fun antalUppgifter(): Int

    @Transaction
    suspend fun markeraSomGjord(uppdateradUppgift: Uppgift, nyPost: Post) {
        uppdateraUppgift(uppdateradUppgift)
        infogaPost(nyPost)
    }

    @Query("SELECT * FROM matare WHERE bostad_id = :bostadId ORDER BY namn COLLATE NOCASE, id")
    fun matareForBostadFlow(bostadId: Long): Flow<List<Matare>>

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
        WHERE bostad_id = :bostadId
        ORDER BY
            CASE WHEN kategori IS NULL THEN 1 ELSE 0 END,
            kategori COLLATE NOCASE,
            titel COLLATE NOCASE,
            id
        """,
    )
    fun dokumentForBostadFlow(bostadId: Long): Flow<List<Dokument>>

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

    @Transaction
    suspend fun importeraSakerhetskopiaData(uppgifter: List<Uppgift>, poster: List<Post>) {
        uppgifter.forEach { infogaUppgift(it) }
        poster.forEach { infogaPost(it) }
    }

}
