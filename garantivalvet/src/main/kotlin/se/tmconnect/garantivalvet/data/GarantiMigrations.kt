package se.tmconnect.garantivalvet.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE kop ADD COLUMN kvitto_fil TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE kop ADD COLUMN oppet_kop_dagar INTEGER")
        db.execSQL("ALTER TABLE kop ADD COLUMN varutyp TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE kop ADD COLUMN fel_upptackt INTEGER")
        db.execSQL("ALTER TABLE kop ADD COLUMN fel_beskrivning TEXT")
        db.execSQL("ALTER TABLE kop ADD COLUMN reklamerad INTEGER")
    }
}
