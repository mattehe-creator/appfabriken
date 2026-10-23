package se.tmconnect.styrelserummet.underlag.exempel

import se.tmconnect.styrelserummet.modell.Forening

const val FORENING_NAMN = "Brf Exempelgården"
const val ORGANISATIONSNUMMER = "000000-0000"
const val ADRESS = "Exempelvägen 1, 100 00 Teststad"
const val BYGGNADSAR = 1974
const val RAKENSKAPSAR = 2025
const val BYGGNADENS_VARDE = 27_000_000L

data class ExempelAtgard(val namn: String, val ar: Int, val kostnadKronor: Long?)

val EXEMPEL_ATGARDER: List<ExempelAtgard> =
    listOf(
        ExempelAtgard("Yttertak", 2032, 5_000_000L),
        ExempelAtgard("Fasadputs", 2036, 4_000_000L),
        ExempelAtgard("Fönster", 2028, 3_500_000L),
        ExempelAtgard("Stammar vatten och avlopp", 2030, 3_000_000L),
        ExempelAtgard("Elstammar och elcentral", 2029, 2_500_000L),
        ExempelAtgard("Fjärrvärmecentral", 2027, 2_000_000L),
        ExempelAtgard("Ventilation FTX", 2034, 1_800_000L),
        ExempelAtgard("Hiss", 2031, 1_500_000L),
        ExempelAtgard("Tvättstuga", 2033, 1_200_000L),
        ExempelAtgard("Mark och gård", 2035, 1_000_000L),
        ExempelAtgard("Entréparti", 2037, 800_000L),
        ExempelAtgard("Garageport", 2039, 500_000L),
        ExempelAtgard("Balkonger", 2038, null),
        ExempelAtgard("Brandlarm", 2026, 200_000L),
    )

fun exempelForening(): Forening {
    return Forening(FORENING_NAMN, BYGGNADSAR)
}

fun summaAtgarderMedKostnad(): Long {
    return EXEMPEL_ATGARDER
        .filter { it.kostnadKronor != null }
        .sumOf { it.kostnadKronor!! }
}
