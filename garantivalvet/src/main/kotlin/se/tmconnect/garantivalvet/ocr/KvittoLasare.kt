package se.tmconnect.garantivalvet.ocr

import android.net.Uri

/** Läser text från en kvittobild på enheten. Returnerar `null` vid fel eller tom text. */
interface KvittoLasare {
    suspend fun lasText(bild: Uri): String?
}
