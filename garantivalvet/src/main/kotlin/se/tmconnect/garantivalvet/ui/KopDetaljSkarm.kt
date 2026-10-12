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
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.KONSUMENTKOP_IKRAFT
import se.tmconnect.garantivalvet.regler.KopStatus
import se.tmconnect.garantivalvet.regler.appfabrikStatusFran
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.garantivalvet.regler.kopStatus
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
) {
    var visaBekraftaTaBort by remember { mutableStateOf(false) }
    var visaMeny by remember { mutableStateOf(false) }
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    val status = kopStatus(kop.kopdatum, kop.garantiManader, idag)
    val chipFarger = statusChipFarger(appfabrikStatusFran(status))
    // Ta bort referens till TidslinjeMarkerer
    // val tidslinjeMarkorer = beraknaTidslinje(kop.kopdatum, kop.garantiManader, idag)

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
                    IconButton(onClick = onTillbaka) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tillbaka),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { visaMeny = true }) {
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
                    onClick = {
                        if (arPro) {
                            onExportPdf()
                        } else {
                            onVisaProDialog()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.export_pdf))
                        if (!arPro) {
                            Text(
                                text = stringResource(R.string.pro_mark),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
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
                // Ta bort tidslinjeMarkorer parameter
                // tidslinjeMarkorer = tidslinjeMarkorer
            )

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
            DetaljRad(
                etikett = stringResource(R.string.falt_garanti_manader),
                varde = kop.garantiManader.toString(),
            )
            kop.anteckning?.let {
                DetaljRad(
                    etikett = stringResource(R.string.falt_anteckning),
                    varde = it,
                )
            }

            ReklamationsKort(kop = kop, locale = locale)
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
    // Ta bort tidslinjeMarkorer parameter
    // tidslinjeMarkorer: TidslinjeMarkerer?
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
        }
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
                    label = { Text(statusText) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = chipFarger.container,
                        labelColor = chipFarger.content,
                    ),
                )
            }

            // Ta bort användning av tidslinjeMarkorer
            /*
            tidslinjeMarkorer?.let {
                TidslinjeStapel(markorer = it, contentDescription = huvudText)
            }
            */

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
    // Korrigera let-anropet för att ta bort typinferensproblem
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
                
                // Läs mer knapp
                TextButton(
                    onClick = { expanderad = !expanderad },
                    modifier = Modifier.padding(top = 4.dp)
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
