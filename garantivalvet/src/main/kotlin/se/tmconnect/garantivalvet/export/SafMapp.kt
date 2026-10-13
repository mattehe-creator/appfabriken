package se.tmconnect.garantivalvet.export

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.io.OutputStream

/** En mapp vald med `ACTION_OPEN_DOCUMENT_TREE`, via systemets dokumentleverantör. Ingen lagringsbehörighet behövs. */
class SafMapp(
    context: Context,
    private val tradUri: Uri,
) : MappLagring {
    private val resolver = context.contentResolver

    private val rotId: String get() = DocumentsContract.getTreeDocumentId(tradUri)

    override fun lista(): List<MappPost> {
        val barn = DocumentsContract.buildChildDocumentsUriUsingTree(tradUri, rotId)
        val kolumner = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )
        val resultat = mutableListOf<MappPost>()
        resolver.query(barn, kolumner, null, null, null)?.use { markor ->
            while (markor.moveToNext()) {
                // Vissa leverantörer saknar id eller namn: hoppa över posten i stället för att fela hela körningen.
                val id = markor.getString(0) ?: continue
                val namn = markor.getString(1) ?: continue
                resultat += MappPost(id, namn, markor.getLong(2))
            }
        }
        return resultat
    }

    override fun skapa(namn: String): OutputStream? {
        val rot = DocumentsContract.buildDocumentUriUsingTree(tradUri, rotId)
        val fil = DocumentsContract.createDocument(resolver, rot, "application/zip", namn) ?: return null
        return resolver.openOutputStream(fil, "w")
    }

    override fun radera(post: MappPost): Boolean =
        try {
            DocumentsContract.deleteDocument(resolver, DocumentsContract.buildDocumentUriUsingTree(tradUri, post.id))
        } catch (_: Exception) {
            false
        }
}
