package se.tmconnect.garantivalvet.ocr

import android.net.Uri

/** Läser texten på ett kvittofoto. Allt sker på enheten. */
interface KvittoLasare {
    /** Texten på bilden, eller `null` om bilden saknar text eller avläsningen misslyckades. Kastar aldrig. */
    suspend fun lasText(bild: Uri): String?
}
