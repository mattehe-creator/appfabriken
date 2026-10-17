package se.tmconnect.husloggen.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matare")
data class Matare(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val namn: String,
    val enhet: String,
    val anteckning: String? = null,
    val skapad: Long = System.currentTimeMillis(),
)
