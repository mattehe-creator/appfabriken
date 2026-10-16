package se.tmconnect.husloggen.data

import android.content.Context
import se.tmconnect.karna.bild.BildLager

fun skapaHusBildLager(context: Context): BildLager =
    BildLager(
        context = context,
        katalogNamn = "bilder",
        fileProviderAuthority = "${context.packageName}.bildfil",
    )
