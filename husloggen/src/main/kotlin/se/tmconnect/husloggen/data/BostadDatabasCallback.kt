package se.tmconnect.husloggen.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import se.tmconnect.husloggen.R

internal fun skapaBostadSeedCallback(context: Context): RoomDatabase.Callback =
    object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            seedForvaldBostad(db, context.getString(R.string.bostad_forvald_namn))
        }
    }

internal fun seedForvaldBostad(db: SupportSQLiteDatabase, namn: String) {
    val skapad = System.currentTimeMillis()
    db.execSQL(
        """
        INSERT INTO bostad (id, namn, skapad)
        VALUES (1, ?, $skapad)
        """.trimIndent(),
        arrayOf(namn),
    )
}
