package se.tmconnect.garantivalvet.regler

/** Användarens val av utseende: följ telefonens inställning (standard), alltid ljust eller alltid mörkt. */
enum class TemaLage {
    SYSTEM,
    LJUST,
    MORKT,
    ;

    /** Om appen ska ritas mörk, givet om telefonen står på mörkt läge. */
    fun arMorkt(telefonenArMork: Boolean): Boolean =
        when (this) {
            SYSTEM -> telefonenArMork
            LJUST -> false
            MORKT -> true
        }

    companion object {
        /** Okänt eller saknat värde ger [SYSTEM]. */
        fun franNamn(namn: String?): TemaLage = entries.firstOrNull { it.name == namn } ?: SYSTEM
    }
}
