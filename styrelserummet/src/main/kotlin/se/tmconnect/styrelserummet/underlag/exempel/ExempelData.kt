package se.tmconnect.styrelserummet.underlag.exempel

import se.tmconnect.styrelserummet.modell.Forening

const val FORENING_NAMN = "Brf Exempelgården"
const val ORGANISATIONSNUMMER = "000000-0000"
const val ADRESS = "Exempelvägen 1, 100 00 Teststad"
const val BYGGNADSAR = 1974
const val RAKENSKAPSAR = 2025

data class ExempelAtgard(val namn: String, val ar: Int, val kostnadKronor: Long?)

var EXEMPEL_ATGARDER: List<ExempelAtgard> = emptyList()

fun exempelForening(): Forening {
    return Forening(FORENING_NAMN, BYGGNADSAR)
}
