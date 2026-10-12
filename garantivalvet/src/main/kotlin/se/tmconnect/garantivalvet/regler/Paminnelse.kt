package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class FristTyp {
    GARANTI,
    REKLAMATION,
    OPPET_KOP,
}

data class Frist(
    val kopId: Long,
    val typ: FristTyp,
    val slutdatum: LocalDate,
)

fun fristNyckel(frist: Frist): String =
    "${frist.kopId}:${frist.typ.name}:${frist.slutdatum}"

/**
 * Returnerar frister som ska aviseras [idag]: 0–[dagarInnan] dagar kvar och inte redan aviserade.
 */
fun fristerSomSkaAviseras(
    frister: List<Frist>,
    idag: LocalDate,
    redanAviserade: Set<String>,
    dagarInnan: Int = SNART_DAGAR,
): List<Frist> =
    frister.filter { frist ->
        val dagarKvar = ChronoUnit.DAYS.between(idag, frist.slutdatum).toInt()
        val grans = if (frist.typ == FristTyp.OPPET_KOP) OPPET_KOP_SNART_DAGAR else dagarInnan
        dagarKvar in 0..grans && fristNyckel(frist) !in redanAviserade
    }
