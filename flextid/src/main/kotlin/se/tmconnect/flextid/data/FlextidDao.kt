package se.tmconnect.flextid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface FlextidDao {
    @Query("SELECT * FROM dag WHERE datum = :datum LIMIT 1")
    suspend fun hamtaDag(datum: LocalDate): Dag?

    @Query("SELECT * FROM dag WHERE datum = :datum LIMIT 1")
    fun observeraDag(datum: LocalDate): Flow<Dag?>

    @Query("SELECT * FROM dag ORDER BY datum")
    fun observeraAllaDagar(): Flow<List<Dag>>

    @Query("SELECT * FROM dag WHERE datum BETWEEN :fran AND :till ORDER BY datum")
    fun observeraDagarMellan(fran: LocalDate, till: LocalDate): Flow<List<Dag>>

    @Query("SELECT * FROM dag ORDER BY datum")
    suspend fun hamtaAllaDagar(): List<Dag>

    @Query("SELECT COUNT(*) FROM dag")
    suspend fun antalDagar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sparaDag(dag: Dag)

    @Query("DELETE FROM dag WHERE datum = :datum")
    suspend fun taBortDag(datum: LocalDate)

    @Query("DELETE FROM dag")
    suspend fun taBortAllaDagar()

    @Query("SELECT * FROM installning WHERE id = 1 LIMIT 1")
    fun observeraInstallning(): Flow<Installning?>

    @Query("SELECT * FROM installning WHERE id = 1 LIMIT 1")
    suspend fun hamtaInstallning(): Installning?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun sparaInstallning(installning: Installning)

    @Update
    suspend fun uppdateraInstallning(installning: Installning)

    /** Flera dagar på en gång (ledighet för en period): allt eller inget. */
    @Transaction
    suspend fun sparaDagar(dagar: List<Dag>) {
        dagar.forEach { sparaDag(it) }
    }

    /** Import: allt eller inget, så att ett avbrott inte lämnar en tom databas. */
    @Transaction
    suspend fun ersattAllData(dagar: List<Dag>, installning: Installning) {
        taBortAllaDagar()
        dagar.forEach { sparaDag(it) }
        sparaInstallning(installning)
    }
}
