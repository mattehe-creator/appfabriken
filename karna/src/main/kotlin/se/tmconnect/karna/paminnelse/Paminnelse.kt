package se.tmconnect.karna.paminnelse

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class PaminnelseFrist(
    val nyckel: String,
    val slutdatum: LocalDate,
)

/**
 * Returnerar frister som ska aviseras [idag]: [dagarInnan] dagar kvar eller tidigare,
 * och som inte redan finns i [redanAviserade].
 */
fun fristerSomSkaAviseras(
    frister: List<PaminnelseFrist>,
    idag: LocalDate,
    redanAviserade: Set<String>,
    dagarInnan: Int,
): List<PaminnelseFrist> =
    frister.filter { frist ->
        val dagarKvar = ChronoUnit.DAYS.between(idag, frist.slutdatum).toInt()
        dagarKvar <= dagarInnan && frist.nyckel !in redanAviserade
    }
