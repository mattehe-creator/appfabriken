package se.tmconnect.garantivalvet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Kop::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Konverterare::class)
abstract class GarantiDatabas : RoomDatabase() {
    abstract fun kopDao(): KopDao

    companion object {
        private const val FILNAMN = "garantivalvet.db"

        @Volatile
        private var instans: GarantiDatabas? = null

        fun hamta(context: Context): GarantiDatabas =
            instans ?: synchronized(this) {
                instans ?: Room.databaseBuilder(
                    context.applicationContext,
                    GarantiDatabas::class.java,
                    FILNAMN,
                ).build().also { instans = it }
            }
    }
}
