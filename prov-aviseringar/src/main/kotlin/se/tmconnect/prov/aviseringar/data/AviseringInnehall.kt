package se.tmconnect.prov.aviseringar.data

import android.app.Notification
import android.os.Bundle
import java.security.MessageDigest

data class AviseringInnehall(
    val postTime: Long,
    val nyckel: String,
    val paketNamn: String,
    val appNamn: String,
    val kanalId: String?,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val subText: String?,
    val textLines: String?,
    val conversationTitle: String?,
    val messages: String?,
) {
    val innehallHash: String = beraknaInnehallHash(
        title = title,
        text = text,
        bigText = bigText,
        subText = subText,
        textLines = textLines,
        conversationTitle = conversationTitle,
        messages = messages,
    )

    fun tillEntitet(loggadTid: Long): AviseringEntitet =
        AviseringEntitet(
            loggadTid = loggadTid,
            postTime = postTime,
            nyckel = nyckel,
            paketNamn = paketNamn,
            appNamn = appNamn,
            kanalId = kanalId,
            title = title,
            text = text,
            bigText = bigText,
            subText = subText,
            textLines = textLines,
            conversationTitle = conversationTitle,
            messages = messages,
            innehallHash = innehallHash,
        )
}

fun beraknaInnehallHash(
    title: String?,
    text: String?,
    bigText: String?,
    subText: String?,
    textLines: String?,
    conversationTitle: String?,
    messages: String?,
): String {
    val radata = listOf(
        title.orEmpty(),
        text.orEmpty(),
        bigText.orEmpty(),
        subText.orEmpty(),
        textLines.orEmpty(),
        conversationTitle.orEmpty(),
        messages.orEmpty(),
    ).joinToString("\u001e")
    val digest = MessageDigest.getInstance("SHA-256")
    return digest.digest(radata.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
}

fun extraheraAviseringInnehall(
    paketNamn: String,
    appNamn: String,
    nyckel: String,
    postTime: Long,
    kanalId: String?,
    extras: Bundle,
): AviseringInnehall =
    AviseringInnehall(
        postTime = postTime,
        nyckel = nyckel,
        paketNamn = paketNamn,
        appNamn = appNamn,
        kanalId = kanalId,
        title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
        text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
        bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
        subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString(),
        textLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            ?.mapNotNull { it?.toString() }
            ?.joinToString("\n")
            ?.takeIf { it.isNotEmpty() },
        conversationTitle = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString(),
        messages = extraheraMeddelanden(extras),
    )

@Suppress("DEPRECATION")
private fun extraheraMeddelanden(extras: Bundle): String? {
    val raw = extras.get(Notification.EXTRA_MESSAGES) ?: return null
    val rader =
        when (raw) {
            is Array<*> ->
                raw.mapNotNull { element ->
                    when (element) {
                        is Bundle -> formateraMeddelandeBundle(element)
                        else -> element?.toString()
                    }
                }
            else -> listOfNotNull(raw.toString())
        }
    return rader.joinToString("\n").takeIf { it.isNotEmpty() }
}

private fun formateraMeddelandeBundle(bundle: Bundle): String? {
    val text = bundle.getCharSequence("text")?.toString()
    val sender = bundle.getCharSequence("sender")?.toString()
    return when {
        text == null -> null
        sender.isNullOrBlank() -> text
        else -> "$sender: $text"
    }
}
