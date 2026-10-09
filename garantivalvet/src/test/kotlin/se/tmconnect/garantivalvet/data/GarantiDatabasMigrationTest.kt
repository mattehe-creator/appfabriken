package se.tmconnect.garantivalvet.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val TEST_DB = "migration-test"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class GarantiDatabasMigrationTest {
    @Test
    fun migration1Till2BevararRadMedNullKvittoFil() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS kop (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                vad TEXT NOT NULL,
                                var_kopt TEXT,
                                kopdatum INTEGER NOT NULL,
                                garanti_manader INTEGER NOT NULL,
                                pris_ore INTEGER,
                                anteckning TEXT,
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int,
                    ) = Unit
                },
            )
            .build()
        val openHelper = FrameworkSQLiteOpenHelperFactory().create(config)
        val kopdatum = LocalDate.of(2024, 6, 15).toEpochDay()
        openHelper.writableDatabase.apply {
            execSQL(
                """
                INSERT INTO kop (id, vad, var_kopt, kopdatum, garanti_manader, pris_ore, anteckning, skapad)
                VALUES (1, 'Kaffebryggare', 'Elgiganten', $kopdatum, 24, 129950, NULL, 1700000000000)
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, GarantiDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        val kop = databas.kopDao().hamta(1)
        assertEquals("Kaffebryggare", kop!!.vad)
        assertEquals(LocalDate.of(2024, 6, 15), kop.kopdatum)
        assertNull(kop.kvittoFil)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }
}
