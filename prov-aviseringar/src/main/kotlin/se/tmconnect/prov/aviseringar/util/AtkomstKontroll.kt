package se.tmconnect.prov.aviseringar.util

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import se.tmconnect.prov.aviseringar.lyssnare.AviseringsLyssnare

fun arAviseringsAtkomstAktiverad(context: Context): Boolean {
    val flat =
        Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners",
        ) ?: return false
    val names = flat.split(":")
    val eget =
        ComponentName(context, AviseringsLyssnare::class.java).flattenToString()
    return names.any { it.equals(eget, ignoreCase = true) }
}
