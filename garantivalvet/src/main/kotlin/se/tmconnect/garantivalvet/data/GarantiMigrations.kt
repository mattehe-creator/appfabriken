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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE kop ADD COLUMN butik_epost TEXT")
        db.execSQL("ALTER TABLE kop ADD COLUMN butik_telefon TEXT")
        db.execSQL("ALTER TABLE kop ADD COLUMN butik_orgnr TEXT")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE kop ADD COLUMN fel_bilder TEXT")
    }
}
