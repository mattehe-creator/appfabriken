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
