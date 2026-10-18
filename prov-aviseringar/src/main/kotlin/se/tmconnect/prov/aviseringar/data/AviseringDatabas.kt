package se.tmconnect.prov.aviseringar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AviseringEntitet::class, PaketStatistik::class],
    version = 1,
    exportSchema = true,
)
abstract class AviseringDatabas : RoomDatabase() {
    abstract fun aviseringDao(): AviseringDao

    companion object {
        private const val FILNAMN = "prov_aviseringar.db"

        @Volatile
        private var instans: AviseringDatabas? = null

        fun hamta(context: Context): AviseringDatabas =
            instans ?: synchronized(this) {
                instans ?: Room.databaseBuilder(
                    context.applicationContext,
                    AviseringDatabas::class.java,
                    FILNAMN,
                ).build().also { instans = it }
            }
    }
}
