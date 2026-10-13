package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import se.tmconnect.garantivalvet.data.Kop

/** Hur reklamationen slutade, enligt användaren själv. Sparas som namnet i `reklamation_utfall`. */
enum class ReklamationsUtfall {
    /** Butiken löste det (reparation, ny vara, prisavdrag eller pengar tillbaka). */
    LOST,

    /** Butiken sa nej eller svarade inte nöjaktigt. */
    AVSLAG,

    /** Användaren har gått vidare och anmält ärendet till Allmänna reklamationsnämnden (ARN). */
    ARN_ANMALD,
    ;

    companion object {
        fun franNamn(namn: String?): ReklamationsUtfall? = entries.firstOrNull { it.name == namn }
    }
}

/** Efter så här många dagar utan svar frågar appen om butiken svarat. Konsumentverket anger ingen fast tid; två veckor är en rimlig påminnelse. */
const val SVAR_FORVANTAD_DAGAR = 14

enum class Uppfoljning {
    /** Inte reklamerat. */
    INGEN,

    /** Reklamerat, inget utfall, och det har gått mindre än [SVAR_FORVANTAD_DAGAR] dagar. */
    VANTAR_SVAR,

    /** Reklamerat, inget utfall, och det har gått [SVAR_FORVANTAD_DAGAR] dagar eller mer. */
    BORDE_HA_SVARAT,
    LOST,
    AVSLAG,
    ARN_ANMALD,
}

fun uppfoljning(kop: Kop, idag: LocalDate): Uppfoljning {
    val reklamerad = kop.reklamerad ?: return Uppfoljning.INGEN
    return when (ReklamationsUtfall.franNamn(kop.reklamationUtfall)) {
        ReklamationsUtfall.LOST -> Uppfoljning.LOST
        ReklamationsUtfall.AVSLAG -> Uppfoljning.AVSLAG
        ReklamationsUtfall.ARN_ANMALD -> Uppfoljning.ARN_ANMALD
        null ->
            if (dagarSedanReklamation(reklamerad, idag) >= SVAR_FORVANTAD_DAGAR) {
                Uppfoljning.BORDE_HA_SVARAT
            } else {
                Uppfoljning.VANTAR_SVAR
            }
    }
}

fun dagarSedanReklamation(reklamerad: LocalDate, idag: LocalDate): Int =
    ChronoUnit.DAYS.between(reklamerad, idag).toInt().coerceAtLeast(0)

/** Dagen då appen börjar fråga om butiken svarat. */
fun svarForvantasSenast(reklamerad: LocalDate): LocalDate = reklamerad.plusDays(SVAR_FORVANTAD_DAGAR.toLong())

/** ARN-påminnelsen är meningslös när ärendet är löst eller redan anmält. */
fun arnPaminnelseRelevant(kop: Kop): Boolean =
    ReklamationsUtfall.franNamn(kop.reklamationUtfall).let { it != ReklamationsUtfall.LOST && it != ReklamationsUtfall.ARN_ANMALD }
