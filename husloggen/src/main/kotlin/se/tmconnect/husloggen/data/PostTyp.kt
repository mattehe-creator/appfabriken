package se.tmconnect.husloggen.data

enum class PostTyp {
    UNDERHALL,
    REPARATION,
    OMBYGGNAD,
    ;

    companion object {
        fun franNamn(namn: String?): PostTyp? =
            namn?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}
