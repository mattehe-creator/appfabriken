package se.tmconnect.flextid.regler

enum class DagTyp {
    ARBETE,

    /** Flexledig: hela dagens normaltid tas från saldot. */
    FLEX,
    LEDIG,
    SEMESTER,
    SJUK,
    HELGDAG,
    ;

    companion object {
        fun franNamn(namn: String?): DagTyp? =
            namn?.let { runCatching { valueOf(it.trim().uppercase()) }.getOrNull() }
    }
}
