package se.tmconnect.prov.aviseringar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AviseringDao {
    @Query("SELECT COUNT(*) FROM avisering_logg WHERE nyckel = :nyckel AND innehallHash = :innehallHash")
    suspend fun antalMedNyckelOchHash(nyckel: String, innehallHash: String): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun infogaAvisering(rad: AviseringEntitet): Long

    @Query("SELECT * FROM avisering_logg ORDER BY loggadTid DESC LIMIT 50")
    fun senasteFemtio(): Flow<List<AviseringEntitet>>

    @Query(
        """
        SELECT paketNamn, appNamn, COUNT(*) AS antal
        FROM avisering_logg
        GROUP BY paketNamn
        ORDER BY antal DESC
        """,
    )
    fun antalSparadePerApp(): Flow<List<AppAntalRad>>

    @Query("SELECT COUNT(*) FROM avisering_logg")
    suspend fun antalSparade(): Int

    @Query("SELECT * FROM avisering_logg ORDER BY loggadTid DESC")
    suspend fun allaSparade(): List<AviseringEntitet>

    @Query("DELETE FROM avisering_logg")
    suspend fun rensaAllaAviseringar()

    @Query("SELECT * FROM paket_statistik ORDER BY antal DESC, appNamn ASC")
    fun allaPaketStatistik(): Flow<List<PaketStatistik>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun infogaPaketStatistik(rad: PaketStatistik): Long

    @Query(
        """
        UPDATE paket_statistik
        SET antal = antal + 1, appNamn = :appNamn
        WHERE paketNamn = :paketNamn
        """,
    )
    suspend fun okaPaketAntal(paketNamn: String, appNamn: String): Int

    @Transaction
    suspend fun registreraPaketAvisering(paketNamn: String, appNamn: String) {
        if (okaPaketAntal(paketNamn, appNamn) == 0) {
            infogaPaketStatistik(PaketStatistik(paketNamn = paketNamn, appNamn = appNamn, antal = 1))
        }
    }

    @Transaction
    suspend fun sparaAviseringOmNy(rad: AviseringEntitet): Boolean {
        if (antalMedNyckelOchHash(rad.nyckel, rad.innehallHash) > 0) {
            return false
        }
        infogaAvisering(rad)
        return true
    }
}
