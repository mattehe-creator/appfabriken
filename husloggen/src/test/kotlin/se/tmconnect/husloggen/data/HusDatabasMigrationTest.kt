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
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                skapaMigration5Till6(context),
                MIGRATION_6_7,
            )
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
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                skapaMigration5Till6(context),
                MIGRATION_6_7,
            )
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
            .addMigrations(
                MIGRATION_3_4,
                MIGRATION_4_5,
                skapaMigration5Till6(context),
                MIGRATION_6_7,
            )
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

    @Test
    fun migration5Till6BevararDataPaForvaldBostad() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(5) {
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
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        MIGRATION_2_3.migrate(db)
                        MIGRATION_3_4.migrate(db)
                        MIGRATION_4_5.migrate(db)
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
        val datum = LocalDate.of(2026, 5, 1).toEpochDay()
        openHelper.writableDatabase.apply {
            execSQL(
                """
                INSERT INTO post (id, datum, vad, typ, belopp_ore, skapad)
                VALUES (1, $datum, 'Fasad', 'UNDERHALL', 100000, 1700000000000)
                """.trimIndent(),
            )
            execSQL(
                """
                INSERT INTO matare (id, namn, enhet, skapad)
                VALUES (1, 'El', 'kWh', 1700000000000)
                """.trimIndent(),
            )
            execSQL(
                """
                INSERT INTO dokument (id, titel, filnamn, mime_typ, tillagd)
                VALUES (1, 'Manual', 'a.pdf', 'application/pdf', $datum)
                """.trimIndent(),
            )
            execSQL(
                """
                INSERT INTO planerad_kostnad (id, titel, belopp_ore, planerat_ar, anteckning)
                VALUES (1, 'Tak', 4000000, 2028, NULL)
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, HusDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_4_5, skapaMigration5Till6(context), MIGRATION_6_7)
            .allowMainThreadQueries()
            .build()

        val dao = databas.husDao()
        assertEquals(1, dao.antalBostader())
        val bostadId = dao.allaBostader().single().id
        assertEquals("Fasad", dao.hamtaPost(1)!!.vad)
        assertEquals(bostadId, dao.hamtaPost(1)!!.bostadId)
        assertEquals(bostadId, dao.hamtaMatare(1)!!.bostadId)
        assertEquals(bostadId, dao.hamtaDokument(1)!!.bostadId)
        assertEquals("Tak", dao.hamtaPlaneradKostnad(1)!!.titel)
        assertEquals(bostadId, dao.hamtaPlaneradKostnad(1)!!.bostadId)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration6Till7LaggerTillPrisPerEnhet() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(5) {
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
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS dokument (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                titel TEXT NOT NULL,
                                kategori TEXT,
                                anteckning TEXT,
                                filnamn TEXT NOT NULL,
                                mime_typ TEXT NOT NULL,
                                tillagd INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS planerad_kostnad (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                titel TEXT NOT NULL,
                                belopp_ore INTEGER NOT NULL,
                                planerat_ar INTEGER NOT NULL,
                                anteckning TEXT
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS matare (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                namn TEXT NOT NULL,
                                enhet TEXT NOT NULL,
                                anteckning TEXT,
                                skapad INTEGER NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS avlasning (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                matare_id INTEGER NOT NULL,
                                datum INTEGER NOT NULL,
                                varde INTEGER NOT NULL,
                                skapad INTEGER NOT NULL,
                                FOREIGN KEY(matare_id) REFERENCES matare(id) ON DELETE CASCADE
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "CREATE INDEX IF NOT EXISTS index_avlasning_matare_id ON avlasning (matare_id)",
                        )
                        db.execSQL(
                            """
                            CREATE UNIQUE INDEX IF NOT EXISTS index_avlasning_matare_id_datum
                            ON avlasning (matare_id, datum)
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            INSERT INTO matare (id, namn, enhet, skapad)
                            VALUES (1, 'El', 'kWh', 1700000000000)
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
        openHelper.writableDatabase.close()
        openHelper.close()

        val databas = Room.databaseBuilder(context, HusDatabas::class.java, TEST_DB)
            .addMigrations(skapaMigration5Till6(context), MIGRATION_6_7)
            .allowMainThreadQueries()
            .build()

        assertNull(databas.husDao().hamtaMatare(1)!!.prisOrePerEnhet)
        databas.husDao().uppdateraMatare(
            Matare(
                id = 1,
                bostadId = 1,
                namn = "El",
                enhet = "kWh",
                prisOrePerEnhet = 150L,
                skapad = 1700000000000,
            ),
        )
        assertEquals(150L, databas.husDao().hamtaMatare(1)!!.prisOrePerEnhet)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }
}
