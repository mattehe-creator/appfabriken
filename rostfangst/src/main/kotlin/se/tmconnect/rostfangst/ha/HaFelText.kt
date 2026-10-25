package se.tmconnect.rostfangst.ha

fun haFelText(status: Int?, undantag: String?): String {
    return when {
        status == 401 -> "Token nekas, skapa en ny i Home Assistant."
        status == 404 -> "Hittar inte Home Assistant på adressen."
        status != null && status in 500..599 -> "Home Assistant svarade med fel."
        status == null && undantag != null -> "Når inte Home Assistant just nu."
        else -> "Något gick fel mot Home Assistant."
    }
}
