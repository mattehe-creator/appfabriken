package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "dokument",
    foreignKeys = [
        ForeignKey(
            entity = Bostad::class,
            parentColumns = ["id"],
            childColumns = ["bostad_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("kategori"),
        Index("titel"),
        Index("bostad_id"),
    ],
)
data class Dokument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "bostad_id")
    val bostadId: Long = 1L,
    val titel: String,
    val kategori: DokumentKategori? = null,
    val anteckning: String? = null,
    val filnamn: String,
    @ColumnInfo(name = "mime_typ")
    val mimeTyp: String,
    val tillagd: LocalDate,
)
