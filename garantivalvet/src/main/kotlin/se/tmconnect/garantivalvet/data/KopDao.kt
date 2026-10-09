package se.tmconnect.garantivalvet.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KopDao {
    @Query("SELECT * FROM kop ORDER BY skapad DESC")
    fun allaFlow(): Flow<List<Kop>>

    @Query("SELECT * FROM kop")
    suspend fun alla(): List<Kop>

    @Query("SELECT * FROM kop WHERE id = :id")
    suspend fun hamta(id: Long): Kop?

    @Insert
    suspend fun infoga(kop: Kop): Long

    @Update
    suspend fun uppdatera(kop: Kop)

    @Delete
    suspend fun taBort(kop: Kop)

    @Query("SELECT COUNT(*) FROM kop")
    suspend fun antal(): Int
}
