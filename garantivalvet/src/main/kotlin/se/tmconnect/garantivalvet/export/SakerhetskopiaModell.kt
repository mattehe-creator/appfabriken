package se.tmconnect.garantivalvet.export

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate

const val SAKERHETSKOPIA_VERSION = 1
const val SAKERHETSKOPIA_KOP_JSON = "kop.json"
const val SAKERHETSKOPIA_KVITTON_MAPP = "kvitton/"
const val SAKERHETSKOPIA_MAX_KOP = 5_000
const val SAKERHETSKOPIA_MAX_FIL_BYTES = 20 * 1024 * 1024

data class SakerhetskopiaKop(
    val vad: String,
    val varKopt: String? = null,
    val kopdatum: LocalDate,
    val garantiManader: Int,
    val prisOre: Long? = null,
    val anteckning: String? = null,
    val kvittoFil: String? = null,
)

data class SakerhetskopiaInnehall(
    val version: Int,
    val skapad: String,
    val kop: List<SakerhetskopiaKop>,
)

data class SakerhetskopiaImportResultat(
    val tillagda: Int,
    val hoppadeOver: Int,
)

fun kopTillSakerhetskopia(kop: Kop): SakerhetskopiaKop =
    SakerhetskopiaKop(
        vad = kop.vad,
        varKopt = kop.varKopt,
        kopdatum = kop.kopdatum,
        garantiManader = kop.garantiManader,
        prisOre = kop.prisOre,
        anteckning = kop.anteckning,
        kvittoFil = kop.kvittoFil,
    )

fun sakerhetskopiaKopTillKop(export: SakerhetskopiaKop, kvittoFil: String? = export.kvittoFil): Kop =
    Kop(
        vad = export.vad.trim(),
        varKopt = export.varKopt?.trim()?.takeIf { it.isNotEmpty() },
        kopdatum = export.kopdatum,
        garantiManader = export.garantiManader,
        prisOre = export.prisOre,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
        kvittoFil = kvittoFil,
    )

/** Dubblett om de fem fälten matchar ett befintligt köp (enligt BESLUTSLOGG). */
fun arSakerhetskopiaDubblett(export: SakerhetskopiaKop, befintlig: Kop): Boolean =
    arSakerhetskopiaKopLik(export, kopTillSakerhetskopia(befintlig))

fun arSakerhetskopiaKopLik(a: SakerhetskopiaKop, b: SakerhetskopiaKop): Boolean =
    a.vad == b.vad &&
        a.varKopt == b.varKopt &&
        a.kopdatum == b.kopdatum &&
        a.garantiManader == b.garantiManader &&
        a.prisOre == b.prisOre
