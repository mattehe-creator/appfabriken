package se.tmconnect.garantivalvet.ocr

import android.net.Uri

/** Utfallet av en avläsning: text, ingen text på bilden, eller ett fel (med orsaken, så att det går att åtgärda). */
sealed interface LasResultat {
    data class Text(val text: String) : LasResultat

    data object IngenText : LasResultat

    data class Fel(val orsak: String) : LasResultat
}

/** Läser texten på ett kvittofoto. Allt sker på enheten. */
interface KvittoLasare {
    /** Läser bilden. Kastar aldrig; fel kommer som [LasResultat.Fel]. */
    suspend fun las(bild: Uri): LasResultat

    /** Texten på bilden, eller `null` om bilden saknar text eller avläsningen misslyckades. */
    suspend fun lasText(bild: Uri): String? = (las(bild) as? LasResultat.Text)?.text
}

/** Kastas av formulärets avläsning när avläsningen misslyckades (inte när bilden bara saknar text). */
class KvittoAvlasningFel(val orsak: String) : Exception(orsak)
