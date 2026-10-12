package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import se.tmconnect.garantivalvet.data.Kop

/**
 * Konsumentverket ("Reklamationsrätt", https://www.konsumentverket.se/konsumentratt-process/reklamera-vara/):
 * reklamera inom skälig tid, och två månader från att felet upptäcktes är alltid i tid
 * (se [REKLAMATION_INOM_MANADER]). Kommer konsumenten och företaget inte överens kan ärendet anmälas
 * till Allmänna reklamationsnämnden (ARN) inom ett år från den första reklamationen.
 */
const val ARN_ANMALAN_AR: Long = 1

/** Sista dag som alltid räknas som i tid för att reklamera ett fel som upptäcktes [felUpptackt]. */
fun reklamerasSenast(felUpptackt: LocalDate): LocalDate = felUpptackt.plusMonths(REKLAMATION_INOM_MANADER)

/** Sista dag att anmäla till ARN när första reklamationen gjordes [reklamerad]. */
fun arnAnmalanSenast(reklamerad: LocalDate): LocalDate = reklamerad.plusYears(ARN_ANMALAN_AR)

enum class ReklamationsLogg {
    /** Inget fel angivet. */
    INGEN,

    /** Fel upptäckt men ännu inte reklamerat. */
    FEL_UPPTACKT,

    /** Reklamerat hos företaget. */
    REKLAMERAD,
}

fun reklamationsLogg(kop: Kop): ReklamationsLogg =
    when {
        kop.reklamerad != null -> ReklamationsLogg.REKLAMERAD
        kop.felUpptackt != null -> ReklamationsLogg.FEL_UPPTACKT
        else -> ReklamationsLogg.INGEN
    }
