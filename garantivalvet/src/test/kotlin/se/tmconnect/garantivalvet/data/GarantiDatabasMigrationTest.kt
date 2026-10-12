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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val kop = databas.kopDao().hamta(1)
        assertEquals("Kaffebryggare", kop!!.vad)
        assertEquals(LocalDate.of(2024, 6, 15), kop.kopdatum)
        assertNull(kop.kvittoFil)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration2Till3BevararRaderMedNullOppetKopOchVarutyp() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(2) {
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
                                kvitto_fil TEXT,
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
                INSERT INTO kop (id, vad, var_kopt, kopdatum, garanti_manader, pris_ore, anteckning, kvitto_fil, skapad)
                VALUES (1, 'Kaffebryggare', 'Elgiganten', $kopdatum, 24, 129950, NULL, 'kvitto.jpg', 1700000000000)
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, GarantiDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val kop = databas.kopDao().hamta(1)!!
        assertEquals("Kaffebryggare", kop.vad)
        assertEquals("kvitto.jpg", kop.kvittoFil)
        assertNull(kop.oppetKopDagar)
        assertNull(kop.varutyp)

        databas.kopDao().uppdatera(kop.copy(oppetKopDagar = 30, varutyp = "ELEKTRONIK"))
        val uppdaterad = databas.kopDao().hamta(1)!!
        assertEquals(30, uppdaterad.oppetKopDagar)
        assertEquals("ELEKTRONIK", uppdaterad.varutyp)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration3Till4BevararRaderMedNullLogg() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(3) {
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
                                kvitto_fil TEXT,
                                skapad INTEGER NOT NULL,
                                oppet_kop_dagar INTEGER,
                                varutyp TEXT
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
                INSERT INTO kop (id, vad, var_kopt, kopdatum, garanti_manader, pris_ore, anteckning, kvitto_fil, skapad, oppet_kop_dagar, varutyp)
                VALUES (1, 'Kaffebryggare', 'Elgiganten', $kopdatum, 24, 129950, NULL, NULL, 1700000000000, 30, 'ELEKTRONIK')
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, GarantiDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val kop = databas.kopDao().hamta(1)!!
        assertEquals(30, kop.oppetKopDagar)
        assertEquals("ELEKTRONIK", kop.varutyp)
        assertNull(kop.felUpptackt)
        assertNull(kop.felBeskrivning)
        assertNull(kop.reklamerad)

        val upptackt = LocalDate.of(2026, 10, 1)
        val reklamerad = LocalDate.of(2026, 10, 5)
        databas.kopDao().uppdatera(kop.copy(felUpptackt = upptackt, felBeskrivning = "Går sönder", reklamerad = reklamerad))
        val uppdaterad = databas.kopDao().hamta(1)!!
        assertEquals(upptackt, uppdaterad.felUpptackt)
        assertEquals("Går sönder", uppdaterad.felBeskrivning)
        assertEquals(reklamerad, uppdaterad.reklamerad)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }

    @Test
    fun migration4Till5BevararRaderMedNullKontakt() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(TEST_DB)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(TEST_DB)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(4) {
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
                                kvitto_fil TEXT,
                                skapad INTEGER NOT NULL,
                                oppet_kop_dagar INTEGER,
                                varutyp TEXT,
                                fel_upptackt INTEGER,
                                fel_beskrivning TEXT,
                                reklamerad INTEGER
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
                INSERT INTO kop (id, vad, var_kopt, kopdatum, garanti_manader, pris_ore, anteckning, kvitto_fil, skapad, oppet_kop_dagar, varutyp, fel_upptackt, fel_beskrivning, reklamerad)
                VALUES (1, 'Kaffebryggare', 'Elgiganten', $kopdatum, 24, 129950, NULL, NULL, 1700000000000, NULL, NULL, NULL, 'Läcker', NULL)
                """.trimIndent(),
            )
            close()
        }
        openHelper.close()

        val databas = Room.databaseBuilder(context, GarantiDatabas::class.java, TEST_DB)
            .addMigrations(MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        val kop = databas.kopDao().hamta(1)!!
        assertEquals("Läcker", kop.felBeskrivning)
        assertNull(kop.butikEpost)
        assertNull(kop.butikTelefon)
        assertNull(kop.butikOrgnr)

        databas.kopDao().uppdatera(kop.copy(butikEpost = "a@b.se", butikTelefon = "08-123 45", butikOrgnr = "556000-0000"))
        val uppdaterad = databas.kopDao().hamta(1)!!
        assertEquals("a@b.se", uppdaterad.butikEpost)
        assertEquals("08-123 45", uppdaterad.butikTelefon)
        assertEquals("556000-0000", uppdaterad.butikOrgnr)

        databas.close()
        context.deleteDatabase(TEST_DB)
    }
}
