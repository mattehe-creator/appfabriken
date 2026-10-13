package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class FristTyp {
    GARANTI,
    REKLAMATION,
    OPPET_KOP,

    /** Fel upptäckt men inte reklamerat: påminner innan två månader gått. */
    REKLAMERA_SENAST,

    /** Reklamerat: påminner innan året för anmälan till ARN gått. */
    ARN,

    /** Reklamerat utan besked: dagen då det gått två veckor (se [SVAR_FORVANTAD_DAGAR]). Påminner den dagen och en vecka framåt. */
    SVAR,
}

data class Frist(
    val kopId: Long,
    val typ: FristTyp,
    val slutdatum: LocalDate,
)

const val REKLAMERA_SENAST_SNART_DAGAR = 14

/** Hur många dagar efter svarsdagen påminnelsen fortfarande kan komma (om telefonen varit avstängd eller jobbet missat). */
const val SVAR_PAMINNELSE_EFTER_DAGAR = 7

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
        val grans = when (frist.typ) {
            FristTyp.OPPET_KOP -> OPPET_KOP_SNART_DAGAR
            FristTyp.REKLAMERA_SENAST -> REKLAMERA_SENAST_SNART_DAGAR
            FristTyp.SVAR -> 0
            else -> dagarInnan
        }
        // "Har butiken svarat?" ska komma när dagen gått, inte i förväg: fönstret är dagen och en vecka efter.
        val nedre = if (frist.typ == FristTyp.SVAR) -SVAR_PAMINNELSE_EFTER_DAGAR else 0
        dagarKvar in nedre..grans && fristNyckel(frist) !in redanAviserade
    }
