package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Room

fun skapaTestHusDatabas(context: Context): HusDatabas =
    Room.inMemoryDatabaseBuilder(context, HusDatabas::class.java)
        .allowMainThreadQueries()
        .addCallback(skapaBostadSeedCallback(context))
        .build()
