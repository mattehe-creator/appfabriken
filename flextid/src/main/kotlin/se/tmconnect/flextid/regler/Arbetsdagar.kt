package se.tmconnect.flextid.regler

import java.time.DayOfWeek
import java.time.LocalDate

/** Bit 0 = måndag … bit 6 = söndag. Standard: måndag–fredag (31). */
const val ARBETSDAGAR_MANDAG_FREDAG = 0b011111

fun arArbetsdag(datum: LocalDate, arbetsdagarMask: Int): Boolean {
    val bit = datum.dayOfWeek.ordinal // MONDAY=0 … SUNDAY=6
    return arbetsdagarMask and (1 shl bit) != 0
}

fun veckodagBit(veckodag: DayOfWeek): Int = 1 shl veckodag.ordinal

/** Sant när minst en veckodag är vald. Annars finns ingen arbetsdag att hitta. */
fun harArbetsdagar(arbetsdagarMask: Int): Boolean = arbetsdagarMask and 0b111_1111 != 0

/** Högst så här många dagar framåt letas en arbetsdag; skyddar mot oändliga loopar. */
const val MAX_DAGAR_FRAMAT = 400
