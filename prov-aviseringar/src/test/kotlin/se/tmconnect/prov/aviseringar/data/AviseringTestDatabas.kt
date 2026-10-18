package se.tmconnect.prov.aviseringar.data

import android.content.Context
import androidx.room.Room

fun skapaTestAviseringDatabas(context: Context): AviseringDatabas =
    Room.inMemoryDatabaseBuilder(context, AviseringDatabas::class.java)
        .allowMainThreadQueries()
        .build()
