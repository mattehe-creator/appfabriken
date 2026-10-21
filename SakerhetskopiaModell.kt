package se.tmconnect.flextid.export

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.Installning
import se.tmconnect.flextid.regler.DagTyp
import se.tmconnect.flextid.regler.STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT

const val FLEXTID_SAKERHETSKOPIA_VERSION = 1
const val FLEXTID_SAKERHETSKOPIA_MIN_VERSION = 0
const val FLEXTID_SAKERHETSKOPIA_MAX_DAGAR = 10_000

data class SakerhetskopiaInstallning(
    val normaltidMinPerDag: Int,
    val standardRastMin: Int,
    val ingangssaldoMin: Int,
    val arbetsdagarMask: Int,
    val stamplaInPaminnelseMinEfterMidnatt: Int = STANDARD_STAMPLA_IN_PAMINNELSE_MIN_EFTER_MIDNATT,
    val aftnarLediga: Boolean = true,
    val semesterdagarPerAr: Int = 25,
    val semesterarStartmanad: Int = 4,
)

data class SakerhetskopiaDag(
    val datum: LocalDate,
    val start: LocalTime? = null,
    val slut: LocalTime? = null,
    val rastMin: Int = 0,
    val typ: String = DagTyp.ARBETE.name,
    val anteckning: String? = null,
)

data class SakerhetskopiaInnehall(
    val version: Int,
    val skapad: String,
    val installning: SakerhetskopiaInstallning,
    val dagar: List<SakerhetskopiaDag>,
)

fun dagTillSakerhetskopia(dag: Dag): SakerhetskopiaDag =
    SakerhetskopiaDag(
        datum = dag.datum,
        start = dag.start,
        slut = dag.slut,
        rastMin = dag.rastMin,
        typ = dag.typ.name,
        anteckning = dag.anteckning,
    )

fun sakerhetskopiaDagTillDag(export: SakerhetskopiaDag): Dag =
    Dag(
        datum = export.datum,
        start = export.start,
        slut = export.slut,
        rastMin = export.rastMin,
        typ = DagTyp.franNamn(export.typ) ?: DagTyp.ARBETE,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
    )

fun installningTillSakerhetskopia(
    installning: Installning,
    stamplaInPaminnelseMinEfterMidnatt: Int,
): SakerhetskopiaInstallning =
    SakerhetskopiaInstallning(
        normaltidMinPerDag = installning.normaltidMinPerDag,
        standardRastMin = installning.standardRastMin,
        ingangssaldoMin = installning.ingangssaldoMin,
        arbetsdagarMask = installning.arbetsdagarMask,
        stamplaInPaminnelseMinEfterMidnatt = stamplaInPaminnelseMinEfterMidnatt,
        aftnarLediga = installning.aftnarLediga,
        semesterdagarPerAr = installning.semesterdagarPerAr,
        semesterarStartmanad = installning.semesterarStartmanad,
    )

fun sakerhetskopiaInstallningTillInstallning(export: SakerhetskopiaInstallning): Installning =
    Installning(
        id = 1,
        normaltidMinPerDag = export.normaltidMinPerDag,
        standardRastMin = export.standardRastMin,
        ingangssaldoMin = export.ingangssaldoMin,
        arbetsdagarMask = export.arbetsdagarMask,
    )
