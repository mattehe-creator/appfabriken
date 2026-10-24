package se.tmconnect.flextid.regler

import java.time.YearMonth

const val EXPORT_AR_MIN = 1900
const val EXPORT_AR_MAX = 9999

/** År och månad från exportfälten, eller null om något fält är tomt eller utanför gränserna. */
fun tolkaExportArManad(arText: String, manadText: String): YearMonth? {
    val ar = arText.trim().toIntOrNull()?.takeIf { it in EXPORT_AR_MIN..EXPORT_AR_MAX } ?: return null
    val manad = manadText.trim().toIntOrNull()?.takeIf { it in 1..12 } ?: return null
    return YearMonth.of(ar, manad)
}
