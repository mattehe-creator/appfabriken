package se.tmconnect.garantivalvet.export

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.garantivalvet.regler.giltigaOppetKopDagar
import se.tmconnect.garantivalvet.regler.lasFelBilder
import se.tmconnect.garantivalvet.regler.skrivFelBilder

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
    val oppetKopDagar: Int? = null,
    val varutyp: String? = null,
    val felUpptackt: LocalDate? = null,
    val felBeskrivning: String? = null,
    val reklamerad: LocalDate? = null,
    val butikEpost: String? = null,
    val butikTelefon: String? = null,
    val butikOrgnr: String? = null,
    val felBilder: List<String> = emptyList(),
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
        oppetKopDagar = kop.oppetKopDagar,
        varutyp = kop.varutyp,
        felUpptackt = kop.felUpptackt,
        felBeskrivning = kop.felBeskrivning,
        reklamerad = kop.reklamerad,
        butikEpost = kop.butikEpost,
        butikTelefon = kop.butikTelefon,
        butikOrgnr = kop.butikOrgnr,
        felBilder = lasFelBilder(kop.felBilder),
    )

fun sakerhetskopiaKopTillKop(
    export: SakerhetskopiaKop,
    kvittoFil: String? = export.kvittoFil,
    felBilder: List<String> = export.felBilder,
): Kop =
    Kop(
        vad = export.vad.trim(),
        varKopt = export.varKopt?.trim()?.takeIf { it.isNotEmpty() },
        kopdatum = export.kopdatum,
        garantiManader = export.garantiManader,
        prisOre = export.prisOre,
        anteckning = export.anteckning?.trim()?.takeIf { it.isNotEmpty() },
        kvittoFil = kvittoFil,
        oppetKopDagar = giltigaOppetKopDagar(export.oppetKopDagar),
        varutyp = Varutyp.franNamn(export.varutyp)?.name,
        felUpptackt = export.felUpptackt,
        felBeskrivning = export.felBeskrivning?.trim()?.takeIf { it.isNotEmpty() },
        reklamerad = export.reklamerad,
        butikEpost = export.butikEpost?.trim()?.takeIf { it.isNotEmpty() },
        butikTelefon = export.butikTelefon?.trim()?.takeIf { it.isNotEmpty() },
        butikOrgnr = export.butikOrgnr?.trim()?.takeIf { it.isNotEmpty() },
        felBilder = skrivFelBilder(felBilder),
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
