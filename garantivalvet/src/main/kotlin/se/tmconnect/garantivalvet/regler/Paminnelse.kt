package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import se.tmconnect.garantivalvet.data.Kop

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

/** Påminnelser som hör till ett köp (samma logik som [se.tmconnect.garantivalvet.paminnelse.PaminnelseWorker]). */
fun fristerFranKop(kop: Kop): List<Frist> {
    val garanti = Frist(
        kopId = kop.id,
        typ = FristTyp.GARANTI,
        slutdatum = garantiSlut(kop.kopdatum, kop.garantiManader),
    )
    val reklamation = reklamationSlut(kop.kopdatum)?.let { slut ->
        Frist(kopId = kop.id, typ = FristTyp.REKLAMATION, slutdatum = slut)
    }
    val oppetKop = giltigaOppetKopDagar(kop.oppetKopDagar)?.let { dagar ->
        Frist(kopId = kop.id, typ = FristTyp.OPPET_KOP, slutdatum = oppetKopSlut(kop.kopdatum, dagar))
    }
    val svarFrist = if (kop.reklamerad != null && kop.reklamationUtfall == null) {
        Frist(kopId = kop.id, typ = FristTyp.SVAR, slutdatum = svarForvantasSenast(kop.reklamerad))
    } else {
        null
    }
    val reklamationsLogg = when (reklamationsLogg(kop)) {
        ReklamationsLogg.REKLAMERAD ->
            if (arnPaminnelseRelevant(kop)) {
                Frist(kopId = kop.id, typ = FristTyp.ARN, slutdatum = arnAnmalanSenast(kop.reklamerad!!))
            } else {
                null
            }
        ReklamationsLogg.FEL_UPPTACKT ->
            Frist(kopId = kop.id, typ = FristTyp.REKLAMERA_SENAST, slutdatum = reklamerasSenast(kop.felUpptackt!!))
        ReklamationsLogg.INGEN -> null
    }
    if (arFarskvara(kop)) return listOfNotNull(oppetKop, reklamationsLogg, svarFrist)
    return listOfNotNull(oppetKop, garanti, reklamation, reklamationsLogg, svarFrist)
}
