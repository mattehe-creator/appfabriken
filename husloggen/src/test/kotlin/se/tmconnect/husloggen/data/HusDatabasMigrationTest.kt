package se.tmconnect.husloggen.data

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

private const val TEST_DB = "hus-migration-test"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HusDatabasMigrationTest {
    @Test
    fun migration1Till2BevararRadMedNullRotRutAvdrag() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS uppgift (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                namn TEXT NOT NULL,
                                intervall_manader INTEGER NOT NULL,
                                start_datum INTEGER NOT NULL,
                                senast_gjord INTEGER,
                                paminnelse INTEGER NOT NULL,
                                anteckning TEXT,
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS post (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                datum INTEGER NOT NULL,
                                vad TEXT NOT NULL,
                                typ TEXT NOT NULL,
                                belopp_ore INTEGER,
                                utforare TEXT,
                                anteckning TEXT,
                                kvitto_fil TEXT,
                                bilder TEXT,
                                uppgift_id INTEGER,
                                skapad INTEGER NOT NULL,
                                FOREIGN KEY(uppgift_id) REFERENCES uppgift(id) ON UPDATE NO ACTION ON DELETE SET NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS index_post_uppgift_id ON post (uppgift_id)",
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
        val datum = LocalDate.of(2025, 3, 1).toEpochDay()
        openHelper.writableDatabase.apply {
            execSQL(
                """
                INSERT INTO post (id, datum, vad, typ, belopp_ore, utforare, anteckning, kvitto_fil, bilder, uppgift_id, skapad)
                VALUES (1, $datum, 'Målning', 'UNDERHALL', 500000, NULL, NULL, NULL, NULL, NULL, 1700000000000)
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, HusDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()

        val post = databas.husDao().hamtaPost(1)
        assertEquals("Målning", post!!.vad)
        assertEquals(500_000L, post.beloppOre)
        assertNull(post.rotRutAvdragOre)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration2Till3SkaparMatareTabeller() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS uppgift (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                namn TEXT NOT NULL,
                                intervall_manader INTEGER NOT NULL,
                                start_datum INTEGER NOT NULL,
                                senast_gjord INTEGER,
                                paminnelse INTEGER NOT NULL,
                                anteckning TEXT,
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS post (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                datum INTEGER NOT NULL,
                                vad TEXT NOT NULL,
                                typ TEXT NOT NULL,
                                belopp_ore INTEGER,
                                rot_rut_avdrag_ore INTEGER,
                                utforare TEXT,
                                anteckning TEXT,
                                kvitto_fil TEXT,
                                bilder TEXT,
                                uppgift_id INTEGER,
                                skapad INTEGER NOT NULL,
                                FOREIGN KEY(uppgift_id) REFERENCES uppgift(id) ON UPDATE NO ACTION ON DELETE SET NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS index_post_uppgift_id ON post (uppgift_id)",
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
        openHelper.writableDatabase.close()
        openHelper.close()

        val databas = Room.databaseBuilder(context, HusDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()

        val matareId =
            databas.husDao().infogaMatare(
                Matare(namn = "Vatten", enhet = "m³"),
            )
        databas.husDao().infogaAvlasning(
            Avlasning(
                matareId = matareId,
                datum = LocalDate.of(2026, 1, 1),
                varde = 1000,
            ),
        )
        assertEquals(1, databas.husDao().antalMatare())
        assertEquals(1, databas.husDao().avlasningarForMatare(matareId).size)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration3Till4SkaparDokumentTabell() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(3) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS uppgift (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                namn TEXT NOT NULL,
                                intervall_manader INTEGER NOT NULL,
                                start_datum INTEGER NOT NULL,
                                senast_gjord INTEGER,
                                paminnelse INTEGER NOT NULL,
                                anteckning TEXT,
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS post (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                datum INTEGER NOT NULL,
                                vad TEXT NOT NULL,
                                typ TEXT NOT NULL,
                                belopp_ore INTEGER,
                                rot_rut_avdrag_ore INTEGER,
                                utforare TEXT,
                                anteckning TEXT,
                                kvitto_fil TEXT,
                                bilder TEXT,
                                uppgift_id INTEGER,
                                skapad INTEGER NOT NULL,
                                FOREIGN KEY(uppgift_id) REFERENCES uppgift(id) ON UPDATE NO ACTION ON DELETE SET NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS index_post_uppgift_id ON post (uppgift_id)",
                        )
                        MIGRATION_2_3.migrate(db)
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
        openHelper.writableDatabase.close()
        openHelper.close()

        val databas = Room.databaseBuilder(context, HusDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()

        val id =
            databas.husDao().infogaDokument(
                Dokument(
                    titel = "Manual diskmaskin",
                    kategori = DokumentKategori.MANUAL,
                    filnamn = "test.pdf",
                    mimeTyp = "application/pdf",
                    tillagd = LocalDate.of(2026, 10, 1),
                ),
            )
        assertEquals(1, databas.husDao().allaDokument().size)
        assertEquals("Manual diskmaskin", databas.husDao().hamtaDokument(id)!!.titel)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }
}
