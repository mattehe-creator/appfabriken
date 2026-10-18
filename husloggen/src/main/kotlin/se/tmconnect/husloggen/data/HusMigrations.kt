package se.tmconnect.husloggen.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
