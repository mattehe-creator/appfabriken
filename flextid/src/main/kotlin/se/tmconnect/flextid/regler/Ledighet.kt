package se.tmconnect.flextid.regler

import java.time.LocalDate

/**
 * Arbetsdag enligt användarens veckodagar, minus svenska röda dagar och,
 * om [aftnarLediga], midsommar-, jul- och nyårsafton.
 * Används för påminnelser, visning och utfall (normaltid). Dagar som inte registreras räknas inte.
 */
fun arArbetsdagMedHelger(datum: LocalDate, arbetsdagarMask: Int, aftnarLediga: Boolean): Boolean =
    arArbetsdag(datum, arbetsdagarMask) && !arLedigDag(datum, aftnarLediga)
