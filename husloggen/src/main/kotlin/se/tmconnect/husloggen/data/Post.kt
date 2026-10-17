package se.tmconnect.husloggen.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "post",
    foreignKeys = [
        ForeignKey(
            entity = Uppgift::class,
            parentColumns = ["id"],
            childColumns = ["uppgift_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("uppgift_id")],
)
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val datum: LocalDate,
    val vad: String,
    val typ: PostTyp,
    @ColumnInfo(name = "belopp_ore")
    val beloppOre: Long? = null,
    @ColumnInfo(name = "rot_rut_avdrag_ore")
    val rotRutAvdragOre: Long? = null,
    val utforare: String? = null,
    val anteckning: String? = null,
    @ColumnInfo(name = "kvitto_fil")
    val kvittoFil: String? = null,
    val bilder: String? = null,
    @ColumnInfo(name = "uppgift_id")
    val uppgiftId: Long? = null,
    val skapad: Long = System.currentTimeMillis(),
)
