package se.tmconnect.rostfangst.wear

object WearSokvagar {
    const val FANGST = "/rostfangst/fangst"
    const val FORSLAG = "/rostfangst/forslag"
    const val UTFOR = "/rostfangst/utfor"
    const val KLART = "/rostfangst/klart"

    fun arKandSokvag(s: String): Boolean {
        return s == FANGST || s == FORSLAG || s == UTFOR || s == KLART
    }
}
