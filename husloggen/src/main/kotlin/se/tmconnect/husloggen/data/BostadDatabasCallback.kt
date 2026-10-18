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
    db.execSQL(
        """
        INSERT INTO bostad (id, namn, adress, anteckning)
        VALUES (1, ?, NULL, NULL)
        """.trimIndent(),
        arrayOf(namn),
    )
}
