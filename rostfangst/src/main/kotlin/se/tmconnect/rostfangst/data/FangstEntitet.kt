package se.tmconnect.rostfangst.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import se.tmconnect.rostfangst.regler.ForslagTyp

@Entity(tableName = "fangst")
data class FangstEntitet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalText: String,
    val typ: ForslagTyp,
    val titel: String,
    val startEpochMillis: Long? = null,
    val slutEpochMillis: Long? = null,
    val plats: String? = null,
    val svar: String? = null,
    val skapadEpochMillis: Long,
    val klar: Boolean = false,
    /** Sant om texten väntar på (om)tolkning, t.ex. utan nät. */
    val vantarTolkning: Boolean = false,
)
