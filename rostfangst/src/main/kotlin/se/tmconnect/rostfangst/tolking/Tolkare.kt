package se.tmconnect.rostfangst.tolking

import se.tmconnect.rostfangst.regler.Forslag

interface Tolkare {
    fun tolka(text: String, systemprompt: String): Result<Forslag>
}

class TolkningMisslyckades(val orsak: String) : Exception(orsak)
