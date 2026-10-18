package se.tmconnect.flextid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.regler.DagTyp

@Entity(tableName = "dag")
data class Dag(
    @PrimaryKey val datum: LocalDate,
    val start: LocalTime? = null,
    val slut: LocalTime? = null,
    val rastMin: Int = 0,
    val typ: DagTyp = DagTyp.ARBETE,
    val anteckning: String? = null,
)
