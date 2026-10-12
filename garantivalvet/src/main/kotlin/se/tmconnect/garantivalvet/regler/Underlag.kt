package se.tmconnect.garantivalvet.regler

import se.tmconnect.garantivalvet.data.Kop
import java.time.LocalDate
import java.util.Locale

data class UnderlagRad(
    val etikett: String,
    val varde: String,
)

data class Underlag(
    val dokumentRubrik: String,
    val rader: List<UnderlagRad>,
    val skapad: LocalDate,
    val kvittoFil: String? = null,
)

data class UnderlagEtiketter(
    val vad: String,
    val varKopt: String,
    val kopdatum: String,
    val pris: String,
    val garantiManader: String,
    val garantiSlut: String,
    val reklamationSlut: String,
    val presumtionSlut: String,
    val reklamationStatus: String,
    val statusFelAntasFunnits: String,
    val statusGaller: String,
    val statusUtgangen: String,
    val anteckning: String,
    val skapad: String,
    val oppetKop: String = "",
    val oppetKopSlut: String = "",
    val varutyp: String = "",
    /** Visningsnamn per [Varutyp.name]. */
    val varutyper: Map<String, String> = emptyMap(),
)

fun byggUnderlag(
    kop: Kop,
    skapad: LocalDate,
    etiketter: UnderlagEtiketter,
    dokumentRubrik: String,
    locale: Locale = Locale.forLanguageTag("sv-SE"),
): Underlag {
    val rader = mutableListOf<UnderlagRad>()

    rader.add(UnderlagRad(etiketter.vad, kop.vad))

    kop.varKopt?.takeIf { it.isNotBlank() }?.let { varKopt ->
        rader.add(UnderlagRad(etiketter.varKopt, varKopt))
    }

    rader.add(UnderlagRad(etiketter.kopdatum, formateraDatum(kop.kopdatum, locale)))

    kop.prisOre?.let { prisOre ->
        rader.add(UnderlagRad(etiketter.pris, formateraPrisKr(prisOre, locale)))
    }

    Varutyp.franNamn(kop.varutyp)?.let { typ ->
        val namn = etiketter.varutyper[typ.name]
        if (namn != null && etiketter.varutyp.isNotEmpty()) {
            rader.add(UnderlagRad(etiketter.varutyp, namn))
        }
    }

    // Tre skilda delar: öppet köp (butikens erbjudande), garanti (frivillig) och reklamationsrätt (lag).
    giltigaOppetKopDagar(kop.oppetKopDagar)?.let { dagar ->
        if (etiketter.oppetKop.isNotEmpty()) {
            rader.add(UnderlagRad(etiketter.oppetKop, dagar.toString()))
            rader.add(
                UnderlagRad(etiketter.oppetKopSlut, formateraDatum(oppetKopSlut(kop.kopdatum, dagar), locale)),
            )
        }
    }

    rader.add(UnderlagRad(etiketter.garantiManader, kop.garantiManader.toString()))

    val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
    rader.add(
        UnderlagRad(etiketter.garantiSlut, formateraDatum(garantiSlutDatum, locale)),
    )

    val reklamationSlutDatum = reklamationSlut(kop.kopdatum)
    if (reklamationSlutDatum != null) {
        rader.add(
            UnderlagRad(etiketter.reklamationSlut, formateraDatum(reklamationSlutDatum, locale)),
        )
        presumtionSlut(kop.kopdatum)?.let { presumtionDatum ->
            rader.add(
                UnderlagRad(etiketter.presumtionSlut, formateraDatum(presumtionDatum, locale)),
            )
        }
        val status = reklamationStatus(kop.kopdatum, skapad)
        rader.add(
            UnderlagRad(
                etiketter.reklamationStatus,
                reklamationStatusText(status, etiketter),
            ),
        )
    }

    kop.anteckning?.takeIf { it.isNotBlank() }?.let { anteckning ->
        rader.add(UnderlagRad(etiketter.anteckning, anteckning))
    }

    rader.add(UnderlagRad(etiketter.skapad, formateraDatum(skapad, locale)))

    return Underlag(
        dokumentRubrik = dokumentRubrik,
        rader = rader,
        skapad = skapad,
        kvittoFil = kop.kvittoFil,
    )
}

private fun reklamationStatusText(status: ReklamationStatus, etiketter: UnderlagEtiketter): String =
    when (status) {
        ReklamationStatus.FEL_ANTAS_FUNNITS -> etiketter.statusFelAntasFunnits
        ReklamationStatus.GALLER -> etiketter.statusGaller
        ReklamationStatus.UTGANGEN -> etiketter.statusUtgangen
        ReklamationStatus.EJ_TILLAMPLIG -> ""
    }
