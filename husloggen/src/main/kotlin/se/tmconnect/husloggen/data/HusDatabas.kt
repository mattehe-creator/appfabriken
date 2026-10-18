package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Uppgift::class,
        Post::class,
        Matare::class,
        Avlasning::class,
        Dokument::class,
        PlaneradKostnad::class,
    ],
    version = 5,
    exportSchema = true,
)
@TypeConverters(Konverterare::class)
abstract class HusDatabas : RoomDatabase() {
    abstract fun husDao(): HusDao

    companion object {
        private const val FILNAMN = "husloggen.db"

        @Volatile
        private var instans: HusDatabas? = null

        fun hamta(context: Context): HusDatabas =
            instans ?: synchronized(this) {
                instans ?: Room.databaseBuilder(
                    context.applicationContext,
                    HusDatabas::class.java,
                    FILNAMN,
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build().also { instans = it }
            }
    }
}
