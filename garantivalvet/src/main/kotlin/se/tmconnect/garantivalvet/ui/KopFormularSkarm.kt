package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.regler.FormularFel
import se.tmconnect.garantivalvet.regler.GarantitidVal
import se.tmconnect.garantivalvet.regler.KvittoForslag
import se.tmconnect.garantivalvet.regler.fyllTommaFalt
import se.tmconnect.garantivalvet.regler.tolkaPrisTillOre
import se.tmconnect.garantivalvet.regler.valideraKopFormular
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.karna.tema.AppfabrikKortForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KopFormularSkarm(
    titel: String,
    initialVad: String,
    initialVar: String,
    initialGarantiManader: String,
    initialPris: String?,
    initialAnteckning: String?,
    initialKopdatum: LocalDate,
    kvittoUri: Uri?,
    onValjBild: () -> Unit,
    onTaFoto: () -> Unit,
    onTaBortKvitto: () -> Unit,
    onSpara: (vad: String, varKopt: String?, kopdatum: LocalDate, garantiManader: Int, prisOre: Long?, anteckning: String?) -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
    lasKvitto: suspend (Uri) -> KvittoForslag? = { null },
) {
    var vad by remember(initialVad) { mutableStateOf(initialVad) }
    var varKopt by remember(initialVar) { mutableStateOf(initialVar) }
    var prisText by remember(initialPris) { mutableStateOf(initialPris ?: "") }
    var anteckning by remember(initialAnteckning) { mutableStateOf(initialAnteckning ?: "") }
    var kopdatum by remember(initialKopdatum) { mutableStateOf(initialKopdatum) }

    var forslagButik by remember { mutableStateOf(false) }
    var forslagDatum by remember { mutableStateOf(false) }
    var forslagPris by remember { mutableStateOf(false) }
    var laserKvitto by remember { mutableStateOf(false) }
    var ingetHittades by remember { mutableStateOf(false) }

    // Läs av kvittot när användaren fotar eller byter det. Ett redan sparat kvitto läses inte om.
    val startUri = remember { kvittoUri }
    LaunchedEffect(kvittoUri) {
        if (kvittoUri == null || kvittoUri == startUri) return@LaunchedEffect
        laserKvitto = true
        ingetHittades = false
        try {
            val forslag = lasKvitto(kvittoUri)
            ingetHittades = forslag == null
            if (forslag != null) {
                val ny = fyllTommaFalt(forslag, varKopt, prisText, kopdatum, LocalDate.now())
                varKopt = ny.varKopt
                prisText = ny.prisText
                kopdatum = ny.kopdatum
                if (ny.butikFranKvitto) forslagButik = true
                if (ny.prisFranKvitto) forslagPris = true
                if (ny.datumFranKvitto) forslagDatum = true
            }
        } finally {
            laserKvitto = false
        }
    }

    var visarDatumvaljare by remember { mutableStateOf(false) }
    val datumValjare = rememberDatePickerState(
        initialSelectedDateMillis = kopdatum.toEpochDay() * 86_400_000,
    )
    LaunchedEffect(kopdatum) { datumValjare.selectedDateMillis = kopdatum.toEpochDay() * 86_400_000 }

    var fel by remember { mutableStateOf<List<FormularFel>>(emptyList()) }

    val chipManader = GarantitidVal.CHIP_MANADER
    var valdChipIndex by remember(initialGarantiManader) {
        mutableIntStateOf(
            when (val parsed = initialGarantiManader.trim().toIntOrNull()) {
                in chipManader -> chipManader.indexOf(parsed)
                null -> chipManader.indexOf(GarantitidVal.FORVALD_MANADER)
                else -> 3
            },
        )
    }
    var annatText by remember(initialGarantiManader) {
        mutableStateOf(
            when (val parsed = initialGarantiManader.trim().toIntOrNull()) {
                in chipManader -> ""
                else -> initialGarantiManader.trim()
            },
        )
    }

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            kopdatum = LocalDate.ofEpochDay(millis / 86_400_000)
                            forslagDatum = false
                        }
                        visarDatumvaljare = false
                    },
                ) {
                    Text(text = stringResource(R.string.spara))
                }
            },
            dismissButton = {
                TextButton(onClick = { visarDatumvaljare = false }) {
                    Text(text = stringResource(R.string.avbryt))
                }
            },
        ) {
            DatePicker(state = datumValjare)
        }
    }

    fun spara() {
        val idag = LocalDate.now()
        val garantiManader = GarantitidVal.manaderFranVal(valdChipIndex, annatText)
        fel = valideraKopFormular(
            vad,
            garantiManader ?: -1,
            kopdatum,
            idag,
            prisText,
        )
        if (fel.isEmpty() && garantiManader != null) {
            val prisOre = tolkaPrisTillOre(prisText)
            onSpara(
                vad,
                varKopt.takeIf { it.isNotBlank() },
                kopdatum,
                garantiManader,
                prisOre,
                anteckning.takeIf { it.isNotBlank() },
            )
        }
    }

    val locale = LocalConfiguration.current.locales[0]

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = titel) },
                navigationIcon = {
                    IconButton(onClick = onStang) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { spara() }) {
                        Text(text = stringResource(R.string.spara))
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KvittoKort(
                kvittoUri = kvittoUri,
                onValjBild = onValjBild,
                onTaFoto = onTaFoto,
                onTaBortKvitto = onTaBortKvitto,
            )
            if (laserKvitto) {
                Text(
                    text = stringResource(R.string.kvitto_laser),
                    style = MaterialTheme.typography.labelLarge,
                )
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else if (ingetHittades) {
                Text(
                    text = stringResource(R.string.kvitto_inget_hittades),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OutlinedTextField(
                value = vad,
                onValueChange = { vad = it },
                label = { Text(text = stringResource(R.string.falt_vad)) },
                isError = FormularFel.VAD_SAKNAS in fel,
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.VAD_SAKNAS in fel) {
                Text(
                    text = stringResource(R.string.fel_vad_saknas),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = varKopt,
                onValueChange = {
                    varKopt = it
                    forslagButik = false
                },
                label = { Text(text = stringResource(R.string.falt_var)) },
                supportingText = if (forslagButik) {
                    { Text(text = stringResource(R.string.kvitto_forslag)) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = formateraDatum(kopdatum, locale),
                onValueChange = { },
                label = { Text(text = stringResource(R.string.falt_datum)) },
                supportingText = if (forslagDatum) {
                    { Text(text = stringResource(R.string.kvitto_forslag)) }
                } else {
                    null
                },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { visarDatumvaljare = true }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = stringResource(R.string.falt_datum)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.KOPDATUM_FRAMTID in fel) {
                Text(
                    text = stringResource(R.string.fel_datum),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = stringResource(R.string.falt_garanti_manader),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                chipManader.forEachIndexed { index, manader ->
                    FilterChip(
                        selected = valdChipIndex == index,
                        onClick = {
                            valdChipIndex = index
                            annatText = ""
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    when (manader) {
                                        12 -> R.string.garanti_chip_12
                                        24 -> R.string.garanti_chip_24
                                        else -> R.string.garanti_chip_36
                                    },
                                ),
                            )
                        },
                    )
                }
                FilterChip(
                    selected = valdChipIndex == 3,
                    onClick = {
                        valdChipIndex = 3
                        annatText = ""
                    },
                    label = { Text(text = stringResource(R.string.garanti_chip_annat)) },
                )
            }
            if (valdChipIndex == 3) {
                OutlinedTextField(
                    value = annatText,
                    onValueChange = { annatText = it },
                    label = { Text(text = stringResource(R.string.falt_garanti_manader)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            if (FormularFel.GARANTI_OGILTIG in fel) {
                Text(
                    text = stringResource(R.string.fel_garanti),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = prisText,
                onValueChange = {
                    prisText = it
                    forslagPris = false
                },
                label = { Text(text = stringResource(R.string.falt_pris)) },
                supportingText = if (forslagPris) {
                    { Text(text = stringResource(R.string.kvitto_forslag)) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                suffix = { Text(text = stringResource(R.string.falt_pris_suffix)) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (FormularFel.PRIS_OGILTIGT in fel) {
                Text(
                    text = stringResource(R.string.fel_pris),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            OutlinedTextField(
                value = anteckning,
                onValueChange = { anteckning = it },
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun KvittoKort(
    kvittoUri: Uri?,
    onValjBild: () -> Unit,
    onTaFoto: () -> Unit,
    onTaBortKvitto: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (kvittoUri != null) {
                val bitmap = remember(kvittoUri) {
                    try {
                        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                        context.contentResolver.openInputStream(kvittoUri)?.use {
                            BitmapFactory.decodeStream(it, null, options)
                        }
                        val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 480)
                        context.contentResolver.openInputStream(kvittoUri)?.use { input ->
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
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.kvitto_fornhandsvisning),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onValjBild, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.kvitto_byt))
                    }
                    OutlinedButton(onClick = onTaBortKvitto, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(R.string.kvitto_ta_bort))
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTaFoto,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.kvitto_fotografera))
                    }
                    OutlinedButton(
                        onClick = onValjBild,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.kvitto_valj_bild))
                    }
                }
            }
        }
    }
}
