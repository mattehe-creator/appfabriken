package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.regler.uppfoljning
import se.tmconnect.garantivalvet.regler.dagarSedanReklamation
import se.tmconnect.garantivalvet.regler.Uppfoljning
import se.tmconnect.garantivalvet.regler.ReklamationsUtfall
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.KONSUMENTKOP_IKRAFT
import se.tmconnect.garantivalvet.regler.KopStatus
import se.tmconnect.garantivalvet.regler.ReklamationsLogg
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.garantivalvet.regler.arnAnmalanSenast
import se.tmconnect.garantivalvet.regler.reklamationsLogg
import se.tmconnect.garantivalvet.regler.reklamerasSenast
import se.tmconnect.garantivalvet.regler.garantiSlut
import se.tmconnect.garantivalvet.regler.giltigaOppetKopDagar
import se.tmconnect.garantivalvet.regler.oppetKopSlut
import se.tmconnect.garantivalvet.regler.TidslinjeMarkorer
import se.tmconnect.garantivalvet.regler.appfabrikStatusFran
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.garantivalvet.regler.arFarskvara
import se.tmconnect.garantivalvet.regler.statusForKop
import se.tmconnect.garantivalvet.regler.NarmasteFristTyp
import se.tmconnect.garantivalvet.regler.narmasteFrist
import se.tmconnect.garantivalvet.regler.reklamationSlut
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.beraknaTidslinje
import se.tmconnect.garantivalvet.regler.visaMedVersal
import se.tmconnect.karna.tema.AppfabrikKortForm
import se.tmconnect.karna.tema.StatusChipFarger
import se.tmconnect.karna.tema.statusChipFarger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopDetaljSkarm(
    kop: Kop,
    kvittoUri: Uri?,
    onOppnaKvitto: () -> Unit,
    onAndra: () -> Unit,
    onTaBort: () -> Unit,
    onTillbaka: () -> Unit,
    arPro: Boolean,
    onExportPdf: () -> Unit,
    onVisaProDialog: () -> Unit,
    modifier: Modifier = Modifier,
    onReklamera: () -> Unit = {},
    onMarkeraReklamerad: (LocalDate?) -> Unit = {},
    onReklamationsUtfall: (ReklamationsUtfall?) -> Unit = {},
    onMejlaButik: (String) -> Unit = {},
    onRingButik: (String) -> Unit = {},
) {
    var visaBekraftaTaBort by remember { mutableStateOf(false) }
    var visaMeny by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    val status = statusForKop(kop, idag)
    val chipFarger = statusChipFarger(appfabrikStatusFran(status))
    val tidslinjeMarkorer = if (arFarskvara(kop)) null else beraknaTidslinje(kop.kopdatum, kop.garantiManader, idag)

    if (visaBekraftaTaBort) {
        AlertDialog(
            onDismissRequest = { visaBekraftaTaBort = false },
            title = { Text(text = stringResource(R.string.bekrafta_ta_bort_titel)) },
            text = { Text(text = stringResource(R.string.bekrafta_ta_bort_text)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTaBort()
                        visaBekraftaTaBort = false
                    },
                ) {
                    Text(text = stringResource(R.string.ja_ta_bort))
                }
            },
            dismissButton = {
                TextButton(onClick = { visaBekraftaTaBort = false }) {
                    Text(text = stringResource(R.string.nej))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = visaMedVersal(kop.vad)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { visaMeny = true }, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.mer_meny),
                        )
                    }
                    DropdownMenu(expanded = visaMeny, onDismissRequest = { visaMeny = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.andra)) },
                            onClick = {
                                visaMeny = false
                                onAndra()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.ta_bort)) },
                            onClick = {
                                visaMeny = false
                                visaBekraftaTaBort = true
                            },
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Button(
                    onClick = onReklamera,
                    modifier = Modifier
                        .fillMaxWidth()
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.reklamera_knapp))
                }
                OutlinedButton(
                    onClick = onExportPdf,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.export_pdf))
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatusKort(
                kop = kop,
                status = status,
                chipFarger = chipFarger,
                locale = locale,
                idag = idag,
                tidslinjeMarkorer = tidslinjeMarkorer,
            )

            ReklamationsLoggKort(
                kop = kop,
                locale = locale,
                onMarkeraReklamerad = onMarkeraReklamerad,
                onUtfall = onReklamationsUtfall,
                onSvara = onReklamera,
            )
            ButikKontaktKort(kop = kop, onMejla = onMejlaButik, onRing = onRingButik)

            kvittoUri?.let { uri ->
                KvittoBild(uri = uri, onOppnaKvitto = onOppnaKvitto)
            }

            DetaljRad(
                etikett = stringResource(R.string.falt_var),
                varde = kop.varKopt ?: "—",
            )
            DetaljRad(
                etikett = stringResource(R.string.falt_datum),
                varde = formateraDatum(kop.kopdatum, locale),
            )
            kop.prisOre?.let {
                DetaljRad(
                    etikett = stringResource(R.string.falt_pris),
                    varde = formateraPrisKr(it, locale),
                )
            }
            if (!arFarskvara(kop)) {
                DetaljRad(
                    etikett = stringResource(R.string.falt_garanti_manader),
                    varde = kop.garantiManader.toString(),
                )
            }
            Varutyp.franNamn(kop.varutyp)?.let { typ ->
                DetaljRad(
                    etikett = stringResource(R.string.underlag_varutyp),
                    varde = stringResource(varutypText(typ)),
                )
            }
            kop.anteckning?.let {
                DetaljRad(
                    etikett = stringResource(R.string.falt_anteckning),
                    varde = it,
                )
            }

            // Tre skilda delar: öppet köp (butikens erbjudande), garanti (frivillig) och reklamationsrätt (lag).
            giltigaOppetKopDagar(kop.oppetKopDagar)?.let { dagar ->
                val slut = oppetKopSlut(kop.kopdatum, dagar)
                FristKort(
                    rubrik = stringResource(R.string.detalj_oppet_kop_rubrik),
                    datumText = stringResource(
                        if (idag.isAfter(slut)) R.string.detalj_oppet_kop_gick_ut else R.string.detalj_oppet_kop_till,
                        formateraDatum(slut, locale),
                    ),
                    forklaring = stringResource(R.string.detalj_oppet_kop_forklaring) + " " + stringResource(
                        if (arFarskvara(kop)) R.string.detalj_oppet_kop_mat else R.string.detalj_oppet_kop_oanvand,
                    ),
                )
            }
            if (arFarskvara(kop)) {
                // Mat och dryck är färskvaror: ingen garanti eller treårig reklamationsfrist (se arFarskvara).
                FristKort(
                    rubrik = stringResource(R.string.farskvara_rubrik),
                    datumText = stringResource(R.string.farskvara_text),
                    forklaring = stringResource(R.string.farskvara_forklaring),
                )
            } else {
                val garantiSlutDatum = garantiSlut(kop.kopdatum, kop.garantiManader)
                FristKort(
                    rubrik = stringResource(R.string.detalj_garanti_rubrik),
                    datumText = stringResource(
                        if (idag.isAfter(garantiSlutDatum)) R.string.detalj_garanti_gick_ut else R.string.detalj_garanti_till,
                        formateraDatum(garantiSlutDatum, locale),
                    ),
                    forklaring = stringResource(R.string.detalj_garanti_forklaring),
                )
                ReklamationsKort(kop = kop, locale = locale)
            }
        }
    }
}

@Composable
private fun StatusKort(
    kop: Kop,
    status: KopStatus,
    chipFarger: StatusChipFarger,
    locale: java.util.Locale,
    idag: LocalDate,
    tidslinjeMarkorer: TidslinjeMarkorer?,
) {
    val narmaste = narmasteFrist(kop, idag)
    val huvudText = when {
        narmaste != null -> when (narmaste.typ) {
            NarmasteFristTyp.GARANTI -> stringResource(
                R.string.lista_garanti_till,
                formateraDatum(narmaste.datum, locale),
            )
            NarmasteFristTyp.REKLAMATION -> stringResource(
                R.string.lista_reklamation_till,
                formateraDatum(narmaste.datum, locale),
            )
            NarmasteFristTyp.OPPET_KOP -> stringResource(
                R.string.lista_oppet_kop_till,
                formateraDatum(narmaste.datum, locale),
            )
        }
        arFarskvara(kop) -> stringResource(R.string.farskvara_huvud)
        reklamationSlut(kop.kopdatum) != null -> stringResource(
            R.string.detalj_reklamation_till,
            formateraDatum(reklamationSlut(kop.kopdatum)!!, locale),
        )
        else -> stringResource(R.string.status_utgangen)
    }
    val statusText = when (status) {
        KopStatus.GARANTI_GALLER -> stringResource(R.string.status_galler)
        KopStatus.GARANTI_SNART -> stringResource(R.string.status_gar_ut_snart)
        KopStatus.REKLAMATION_GALLER -> stringResource(R.string.status_reklamation_galler)
        KopStatus.UTGANGEN -> stringResource(R.string.status_utgangen)
        KopStatus.FARSKVARA -> stringResource(R.string.status_farskvara)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = huvudText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text(statusText) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = chipFarger.container,
                        labelColor = chipFarger.content,
                    ),
                    modifier = Modifier.tryckyta48(),
                )
            }

            tidslinjeMarkorer?.let {
                TidslinjeStapel(markorer = it, contentDescription = huvudText)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        }
    }
}

@Composable
private fun DetaljRad(etikett: String, varde: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = etikett,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f),
        )
        Text(
            text = varde,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f),
        )
    }
}

@Composable
private fun KvittoBild(uri: Uri, onOppnaKvitto: () -> Unit) {
    val context = LocalContext.current
    val bitmap = remember(uri) {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 480)
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(
                    input,
                    null,
                    BitmapFactory.Options().apply { inSampleSize = sampleSize },
                )
            }
        } catch (_: Exception) {
            null
        }
    }
    bitmap?.let { decoded ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOppnaKvitto),
            shape = AppfabrikKortForm,
        ) {
            Image(
                bitmap = decoded.asImageBitmap(),
                contentDescription = stringResource(R.string.kvitto_oppna),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
        }
    }
}

/** Loggen över användarens reklamation: fel upptäckt, reklamerat och sista dag för ARN. Visas bara när det finns något. */
@Composable
private fun ReklamationsLoggKort(
    kop: Kop,
    locale: java.util.Locale,
    onMarkeraReklamerad: (LocalDate?) -> Unit,
    onUtfall: (ReklamationsUtfall?) -> Unit,
    onSvara: () -> Unit,
) {
    val logg = reklamationsLogg(kop)
    if (logg == ReklamationsLogg.INGEN) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = stringResource(R.string.logg_rubrik), style = MaterialTheme.typography.titleMedium)
            kop.felBeskrivning?.let {
                Text(text = stringResource(R.string.logg_fel_prefix, it), style = MaterialTheme.typography.bodyMedium)
            }
            when (logg) {
                ReklamationsLogg.FEL_UPPTACKT -> {
                    val upptackt = kop.felUpptackt!!
                    Text(
                        text = stringResource(
                            R.string.logg_fel_upptackt,
                            formateraDatum(upptackt, locale),
                            formateraDatum(reklamerasSenast(upptackt), locale),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = stringResource(R.string.logg_fel_upptackt_hjalp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        onClick = { onMarkeraReklamerad(LocalDate.now()) },
                        modifier = Modifier.tryckyta48(),
                    ) {
                        Text(text = stringResource(R.string.logg_markera_reklamerad))
                    }
                }
                ReklamationsLogg.REKLAMERAD -> {
                    val reklamerad = kop.reklamerad!!
                    Text(
                        text = stringResource(R.string.logg_reklamerad, formateraDatum(reklamerad, locale)),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    val idag = LocalDate.now()
                    val upp = uppfoljning(kop, idag)
                    val dagar = dagarSedanReklamation(reklamerad, idag)
                    val utfallDatum = kop.reklamationUtfallDatum?.let { formateraDatum(it, locale) }.orEmpty()
                    when (upp) {
                        Uppfoljning.VANTAR_SVAR ->
                            Text(
                                text = stringResource(R.string.upp_vantar, dagar),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        Uppfoljning.BORDE_HA_SVARAT ->
                            Text(
                                text = stringResource(R.string.upp_borde_ha_svarat, dagar),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        Uppfoljning.LOST ->
                            Text(text = stringResource(R.string.upp_lost, utfallDatum), style = MaterialTheme.typography.bodyMedium)
                        Uppfoljning.AVSLAG ->
                            Text(text = stringResource(R.string.upp_avslag, utfallDatum), style = MaterialTheme.typography.bodyMedium)
                        Uppfoljning.ARN_ANMALD ->
                            Text(text = stringResource(R.string.upp_arn_anmald, utfallDatum), style = MaterialTheme.typography.bodyMedium)
                        Uppfoljning.INGEN -> Unit
                    }
                    if (upp != Uppfoljning.LOST && upp != Uppfoljning.ARN_ANMALD) {
                        Text(
                            text = stringResource(R.string.logg_arn, formateraDatum(arnAnmalanSenast(reklamerad), locale)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (upp == Uppfoljning.AVSLAG || upp == Uppfoljning.BORDE_HA_SVARAT) {
                        Text(
                            text = stringResource(
                                if (upp == Uppfoljning.AVSLAG) R.string.upp_avslag_hjalp else R.string.upp_borde_hjalp,
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(onClick = onSvara, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(text = stringResource(R.string.upp_svara_knapp))
                        }
                    }
                    if (kop.reklamationUtfall == null) {
                        Text(text = stringResource(R.string.upp_hur_gick_det), style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = { onUtfall(ReklamationsUtfall.LOST) }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(text = stringResource(R.string.upp_knapp_lost))
                        }
                        TextButton(onClick = { onUtfall(ReklamationsUtfall.AVSLAG) }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(text = stringResource(R.string.upp_knapp_avslag))
                        }
                        TextButton(onClick = { onUtfall(ReklamationsUtfall.ARN_ANMALD) }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(text = stringResource(R.string.upp_knapp_arn))
                        }
                    } else {
                        TextButton(onClick = { onUtfall(null) }, modifier = Modifier.heightIn(min = 48.dp)) {
                            Text(text = stringResource(R.string.upp_oppna_igen))
                        }
                    }
                    TextButton(onClick = { onMarkeraReklamerad(null) }, modifier = Modifier.heightIn(min = 48.dp)) {
                        Text(text = stringResource(R.string.logg_angra))
                    }
                }
                ReklamationsLogg.INGEN -> Unit
            }
        }
    }
}

/** Butikens kontaktuppgifter från kvittot, med knappar för att mejla eller ringa. Visas bara när något finns. */
@Composable
private fun ButikKontaktKort(kop: Kop, onMejla: (String) -> Unit, onRing: (String) -> Unit) {
    val epost = kop.butikEpost?.takeIf { it.isNotBlank() }
    val telefon = kop.butikTelefon?.takeIf { it.isNotBlank() }
    val orgnr = kop.butikOrgnr?.takeIf { it.isNotBlank() }
    if (epost == null && telefon == null && orgnr == null) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.detalj_butik_kontakt), style = MaterialTheme.typography.titleMedium)
            epost?.let {
                DetaljRad(etikett = stringResource(R.string.falt_butik_epost), varde = it)
                TextButton(onClick = { onMejla(it) }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.mejla_butiken))
                }
            }
            telefon?.let {
                DetaljRad(etikett = stringResource(R.string.falt_butik_telefon), varde = it)
                TextButton(onClick = { onRing(it) }, modifier = Modifier.tryckyta48()) {
                    Text(text = stringResource(R.string.ring_butiken))
                }
            }
            orgnr?.let { DetaljRad(etikett = stringResource(R.string.falt_butik_orgnr), varde = it) }
        }
    }
}

@Composable
private fun FristKort(rubrik: String, datumText: String, forklaring: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = rubrik, style = MaterialTheme.typography.titleMedium)
            Text(text = datumText, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = forklaring,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReklamationsKort(kop: Kop, locale: java.util.Locale) {
    val reklamationSlutDatum = reklamationSlut(kop.kopdatum)
    var expanderad by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.detalj_reklamation_kort_rubrik),
                style = MaterialTheme.typography.titleMedium,
            )

            if (reklamationSlutDatum != null) {
                Text(
                    text = stringResource(
                        R.string.detalj_reklamation_till,
                        formateraDatum(reklamationSlutDatum, locale),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(R.string.detalj_reklamation_oavsett_garanti),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                TextButton(
                    onClick = { expanderad = !expanderad },
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.detalj_reklamation_las_mer))
                }
                
                AnimatedVisibility(visible = expanderad) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.detalj_reklamation_forklaring),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.detalj_reklamation_bevis),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = stringResource(R.string.detalj_reklamation_slitage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (Varutyp.franNamn(kop.varutyp) == Varutyp.VAXTER) {
                            Text(
                                text = stringResource(R.string.detalj_reklamation_vaxter),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = stringResource(R.string.detalj_reklamation_vagledning),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else if (kop.kopdatum.isBefore(KONSUMENTKOP_IKRAFT)) {
                Text(
                    text = stringResource(R.string.detalj_reklamation_aldre_lag),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
