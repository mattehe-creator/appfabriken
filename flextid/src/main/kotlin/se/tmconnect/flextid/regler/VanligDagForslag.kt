package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime

const val STANDARD_VANLIG_START_MIN = 8 * 60
const val STANDARD_VANLIG_SLUT_MIN = 16 * 60 + 30
const val STANDARD_VANLIG_RAST_MIN = 30

data class VanligDagForslag(
    val start: LocalTime,
    val slut: LocalTime,
    val rastMin: Int,
)

/** Förslag enligt vanliga tider på arbetsdagar; inget på helgdagar eller ogiltiga tider. */
fun dagsForslagVanligDag(
    datum: LocalDate,
    arbetsdagarMask: Int,
    vanligStart: LocalTime,
    vanligSlut: LocalTime,
    vanligRastMin: Int,
): VanligDagForslag? {
    if (!arArbetsdag(datum, arbetsdagarMask)) return null
    if (arRodDag(datum)) return null
    if (!vanligSlut.isAfter(vanligStart)) return null
    return VanligDagForslag(
        start = vanligStart,
        slut = vanligSlut,
        rastMin = vanligRastMin.coerceAtLeast(0),
    )
}

fun normaliseraVanligStartMin(sparad: Int?): Int =
    sparad?.coerceIn(0, 23 * 60 + 59) ?: STANDARD_VANLIG_START_MIN

fun normaliseraVanligSlutMin(sparad: Int?): Int =
    sparad?.coerceIn(0, 23 * 60 + 59) ?: STANDARD_VANLIG_SLUT_MIN

fun normaliseraVanligRastMin(sparad: Int?): Int =
    sparad?.coerceAtLeast(0) ?: STANDARD_VANLIG_RAST_MIN
