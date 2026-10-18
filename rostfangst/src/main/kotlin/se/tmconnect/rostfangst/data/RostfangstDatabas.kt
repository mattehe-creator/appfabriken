package se.tmconnect.rostfangst.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FangstEntitet::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Konverterare::class)
abstract class RostfangstDatabas : RoomDatabase() {
    abstract fun dao(): RostfangstDao

    companion object {
        private const val FILNAMN = "rostfangst.db"

        @Volatile
        private var instans: RostfangstDatabas? = null

        fun hamta(context: Context): RostfangstDatabas =
            instans ?: synchronized(this) {
                instans ?: Room.databaseBuilder(
                    context.applicationContext,
                    RostfangstDatabas::class.java,
                    FILNAMN,
                ).build().also { instans = it }
            }
    }
}
