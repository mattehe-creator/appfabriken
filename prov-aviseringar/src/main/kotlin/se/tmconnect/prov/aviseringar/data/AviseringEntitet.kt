package se.tmconnect.prov.aviseringar.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "avisering_logg",
    indices = [
        Index(value = ["nyckel", "innehallHash"], unique = true),
        Index(value = ["paketNamn"]),
        Index(value = ["loggadTid"]),
    ],
)
data class AviseringEntitet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val loggadTid: Long,
    val postTime: Long,
    val nyckel: String,
    val paketNamn: String,
    val appNamn: String,
    val kanalId: String?,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val subText: String?,
    val textLines: String?,
    val conversationTitle: String?,
    val messages: String?,
    val innehallHash: String,
)

@Entity(tableName = "paket_statistik")
data class PaketStatistik(
    @PrimaryKey val paketNamn: String,
    val appNamn: String,
    val antal: Int,
)

data class AppAntalRad(
    val paketNamn: String,
    val appNamn: String,
    val antal: Int,
)
