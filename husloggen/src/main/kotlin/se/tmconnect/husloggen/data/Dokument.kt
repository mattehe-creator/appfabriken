package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "dokument",
    indices = [
        Index("kategori"),
        Index("titel"),
    ],
)
data class Dokument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titel: String,
    val kategori: DokumentKategori? = null,
    val anteckning: String? = null,
    val filnamn: String,
    @ColumnInfo(name = "mime_typ")
    val mimeTyp: String,
    val tillagd: LocalDate,
)
