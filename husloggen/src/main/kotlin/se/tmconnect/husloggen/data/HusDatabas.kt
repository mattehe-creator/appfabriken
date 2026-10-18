package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Bostad::class, Uppgift::class, Post::class, Matare::class, Avlasning::class, Dokument::class],
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
                val appContext = context.applicationContext
                instans ?: Room.databaseBuilder(
                    appContext,
                    HusDatabas::class.java,
                    FILNAMN,
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        skapaMigration4Till5(appContext),
                    )
                    .addCallback(skapaBostadSeedCallback(appContext))
                    .build().also { instans = it }
            }
    }
}
