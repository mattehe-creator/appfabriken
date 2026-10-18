package se.tmconnect.rostfangst.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import se.tmconnect.rostfangst.regler.ForslagTyp

@Dao
interface RostfangstDao {
    @Insert
    suspend fun insert(fangst: FangstEntitet): Long

    @Update
    suspend fun update(fangst: FangstEntitet)

    @Query("SELECT * FROM fangst ORDER BY skapadEpochMillis DESC")
    fun alla(): Flow<List<FangstEntitet>>

    @Query("SELECT * FROM fangst WHERE typ = :typ ORDER BY skapadEpochMillis DESC")
    fun perTyp(typ: ForslagTyp): Flow<List<FangstEntitet>>

    @Query("SELECT * FROM fangst WHERE id = :id")
    suspend fun hamta(id: Long): FangstEntitet?

    @Query("SELECT skapadEpochMillis FROM fangst ORDER BY skapadEpochMillis DESC")
    suspend fun allaTider(): List<Long>

    @Query("SELECT * FROM fangst WHERE vantarTolkning = 1 ORDER BY skapadEpochMillis ASC")
    suspend fun vantarTolkning(): List<FangstEntitet>
}
