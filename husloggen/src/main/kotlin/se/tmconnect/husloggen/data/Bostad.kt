package se.tmconnect.husloggen.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bostad")
data class Bostad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namn: String,
    val skapad: Long = System.currentTimeMillis(),
)
