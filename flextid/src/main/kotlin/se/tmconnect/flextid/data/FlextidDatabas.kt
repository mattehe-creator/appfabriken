package se.tmconnect.flextid.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import se.tmconnect.flextid.regler.ARBETSDAGAR_MANDAG_FREDAG

@Database(
    entities = [Dag::class, Installning::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Konverterare::class)
abstract class FlextidDatabas : RoomDatabase() {
    abstract fun flextidDao(): FlextidDao

    companion object {
        private const val FILNAMN = "flextid.db"

        @Volatile
        private var instans: FlextidDatabas? = null

        fun hamta(context: Context): FlextidDatabas =
            instans ?: synchronized(this) {
                instans ?: Room.databaseBuilder(
                    context.applicationContext,
                    FlextidDatabas::class.java,
                    FILNAMN,
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                """
                                INSERT INTO installning (id, normaltidMinPerDag, standardRastMin, ingangssaldoMin, arbetsdagarMask)
                                VALUES (1, 480, 30, 0, $ARBETSDAGAR_MANDAG_FREDAG)
                                """.trimIndent(),
                            )
                        }
                    })
                    .build().also { instans = it }
            }
    }
}
