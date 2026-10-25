package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import se.tmconnect.husloggen.R

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE post ADD COLUMN rot_rut_avdrag_ore INTEGER")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
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
                FOREIGN KEY(matare_id) REFERENCES matare(id) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_avlasning_matare_id ON avlasning (matare_id)")
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_avlasning_matare_id_datum ON avlasning (matare_id, datum)",
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
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
        db.execSQL("CREATE INDEX IF NOT EXISTS index_dokument_kategori ON dokument (kategori)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_dokument_titel ON dokument (titel)")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
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
            "CREATE INDEX IF NOT EXISTS index_planerad_kostnad_planerat_ar ON planerad_kostnad (planerat_ar)",
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE matare ADD COLUMN pris_ore_per_enhet INTEGER")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val skapad = System.currentTimeMillis()
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS bostad_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                namn TEXT NOT NULL,
                skapad INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            """
            INSERT INTO bostad_new (id, namn, skapad)
            SELECT id, namn, $skapad FROM bostad
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE bostad")
        db.execSQL("ALTER TABLE bostad_new RENAME TO bostad")
    }
}

fun skapaMigration5Till6(context: Context): Migration =
    object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS bostad (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    namn TEXT NOT NULL,
                    skapad INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            val forvaldNamn = context.getString(R.string.bostad_forvald_namn)
            seedForvaldBostad(db, forvaldNamn)
            migreraUppgiftMedBostad(db)
            migreraPostMedBostad(db)
            migreraMatareMedBostad(db)
            migreraDokumentMedBostad(db)
            migreraPlaneradKostnadMedBostad(db)
        }
    }

private fun migreraUppgiftMedBostad(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS uppgift_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            bostad_id INTEGER NOT NULL,
            namn TEXT NOT NULL,
            intervall_manader INTEGER NOT NULL,
            start_datum INTEGER NOT NULL,
            senast_gjord INTEGER,
            paminnelse INTEGER NOT NULL,
            anteckning TEXT,
            skapad INTEGER NOT NULL,
            FOREIGN KEY(bostad_id) REFERENCES bostad(id) ON UPDATE NO ACTION ON DELETE CASCADE
        )
        """.trimIndent(),
    )
    db.execSQL(
        """
        INSERT INTO uppgift_new (
            id, bostad_id, namn, intervall_manader, start_datum, senast_gjord, paminnelse, anteckning, skapad
        )
        SELECT id, 1, namn, intervall_manader, start_datum, senast_gjord, paminnelse, anteckning, skapad
        FROM uppgift
        """.trimIndent(),
    )
    db.execSQL("DROP TABLE uppgift")
    db.execSQL("ALTER TABLE uppgift_new RENAME TO uppgift")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_uppgift_bostad_id ON uppgift (bostad_id)")
}

private fun migreraPostMedBostad(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS post_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            bostad_id INTEGER NOT NULL,
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
            FOREIGN KEY(bostad_id) REFERENCES bostad(id) ON UPDATE NO ACTION ON DELETE CASCADE,
            FOREIGN KEY(uppgift_id) REFERENCES uppgift(id) ON UPDATE NO ACTION ON DELETE SET NULL
        )
        """.trimIndent(),
    )
    db.execSQL(
        """
        INSERT INTO post_new (
            id, bostad_id, datum, vad, typ, belopp_ore, rot_rut_avdrag_ore, utforare, anteckning,
            kvitto_fil, bilder, uppgift_id, skapad
        )
        SELECT id, 1, datum, vad, typ, belopp_ore, rot_rut_avdrag_ore, utforare, anteckning,
            kvitto_fil, bilder, uppgift_id, skapad
        FROM post
        """.trimIndent(),
    )
    db.execSQL("DROP TABLE post")
    db.execSQL("ALTER TABLE post_new RENAME TO post")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_post_bostad_id ON post (bostad_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_post_uppgift_id ON post (uppgift_id)")
}

private fun migreraMatareMedBostad(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS matare_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            bostad_id INTEGER NOT NULL,
            namn TEXT NOT NULL,
            enhet TEXT NOT NULL,
            anteckning TEXT,
            skapad INTEGER NOT NULL,
            FOREIGN KEY(bostad_id) REFERENCES bostad(id) ON UPDATE NO ACTION ON DELETE CASCADE
        )
        """.trimIndent(),
    )
    db.execSQL(
        """
        INSERT INTO matare_new (id, bostad_id, namn, enhet, anteckning, skapad)
        SELECT id, 1, namn, enhet, anteckning, skapad FROM matare
        """.trimIndent(),
    )
    db.execSQL("DROP TABLE matare")
    db.execSQL("ALTER TABLE matare_new RENAME TO matare")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_matare_bostad_id ON matare (bostad_id)")
}

private fun migreraDokumentMedBostad(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS dokument_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            bostad_id INTEGER NOT NULL,
            titel TEXT NOT NULL,
            kategori TEXT,
            anteckning TEXT,
            filnamn TEXT NOT NULL,
            mime_typ TEXT NOT NULL,
            tillagd INTEGER NOT NULL,
            FOREIGN KEY(bostad_id) REFERENCES bostad(id) ON UPDATE NO ACTION ON DELETE CASCADE
        )
        """.trimIndent(),
    )
    db.execSQL(
        """
        INSERT INTO dokument_new (
            id, bostad_id, titel, kategori, anteckning, filnamn, mime_typ, tillagd
        )
        SELECT id, 1, titel, kategori, anteckning, filnamn, mime_typ, tillagd FROM dokument
        """.trimIndent(),
    )
    db.execSQL("DROP TABLE dokument")
    db.execSQL("ALTER TABLE dokument_new RENAME TO dokument")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_dokument_bostad_id ON dokument (bostad_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_dokument_kategori ON dokument (kategori)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_dokument_titel ON dokument (titel)")
}

private fun migreraPlaneradKostnadMedBostad(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS planerad_kostnad_new (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            bostad_id INTEGER NOT NULL,
            titel TEXT NOT NULL,
            belopp_ore INTEGER NOT NULL,
            planerat_ar INTEGER NOT NULL,
            anteckning TEXT,
            FOREIGN KEY(bostad_id) REFERENCES bostad(id) ON UPDATE NO ACTION ON DELETE CASCADE
        )
        """.trimIndent(),
    )
    db.execSQL(
        """
        INSERT INTO planerad_kostnad_new (
            id, bostad_id, titel, belopp_ore, planerat_ar, anteckning
        )
        SELECT id, 1, titel, belopp_ore, planerat_ar, anteckning FROM planerad_kostnad
        """.trimIndent(),
    )
    db.execSQL("DROP TABLE planerad_kostnad")
    db.execSQL("ALTER TABLE planerad_kostnad_new RENAME TO planerad_kostnad")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_planerad_kostnad_bostad_id ON planerad_kostnad (bostad_id)")
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_planerad_kostnad_planerat_ar ON planerad_kostnad (planerat_ar)",
    )
}
