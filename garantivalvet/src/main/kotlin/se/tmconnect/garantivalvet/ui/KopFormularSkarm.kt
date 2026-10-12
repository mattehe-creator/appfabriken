package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import se.tmconnect.garantivalvet.regler.Varutyp
import se.tmconnect.garantivalvet.regler.giltigaOppetKopDagar
import se.tmconnect.garantivalvet.regler.fyllTommaFalt
import se.tmconnect.garantivalvet.regler.tolkaPrisTillOre
import se.tmconnect.garantivalvet.regler.valideraKopFormular
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.karna.tema.AppfabrikKortForm

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun KopFormularSkarm(
    titel: String,
    initialVad: String,
    initialVar: String,
    initialGarantiManader: String,
    initialPris: String?,
    initialAnteckning: String?,
    initialKopdatum: LocalDate,
    initialOppetKop: Int? = null,
    initialVarutyp: String? = null,
    lasInitialtKvitto: Boolean = false,
    onRotera: (medurs: Boolean) -> Unit = {},
    kvittoUri: Uri?,
    onValjBild: () -> Unit,
    onTaFoto: () -> Unit,
    onTaBortKvitto: () -> Unit,
    onSpara: (
        vad: String,
        varKopt: String?,
        kopdatum: LocalDate,
        garantiManader: Int,
        prisOre: Long?,
        anteckning: String?,
        oppetKopDagar: Int?,
        varutyp: String?,
    ) -> Unit,
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
    var forslagOppetKop by remember { mutableStateOf(false) }

    // Öppet köp: chips Inget / 14 / 30 / Annat (index 3, med fritt antal dagar).
    fun oppetKopIndexFor(dagar: Int?): Int = when (dagar) {
        null -> 0
        14 -> 1
        30 -> 2
        else -> 3
    }
    var oppetKopIndex by remember(initialOppetKop) { mutableIntStateOf(oppetKopIndexFor(initialOppetKop)) }
    var oppetKopAnnat by remember(initialOppetKop) {
        mutableStateOf(if (oppetKopIndexFor(initialOppetKop) == 3) initialOppetKop.toString() else "")
    }
    var varutyp by remember(initialVarutyp) { mutableStateOf(Varutyp.franNamn(initialVarutyp)) }

    fun nuvarandeOppetKop(): Int? = when (oppetKopIndex) {
        0 -> null
        1 -> 14
        2 -> 30
        else -> giltigaOppetKopDagar(oppetKopAnnat.trim().toIntOrNull())
    }
    var laserKvitto by remember { mutableStateOf(false) }
    var ingetHittades by remember { mutableStateOf(false) }
    var lasIgen by remember { mutableIntStateOf(0) }
    var forslagVaror by remember { mutableStateOf<List<String>>(emptyList()) }

    // Läs av kvittot när användaren fotar eller byter det. Ett redan sparat kvitto läses inte om.
    val startUri = remember { if (lasInitialtKvitto) null else kvittoUri }
    LaunchedEffect(kvittoUri, lasIgen) {
        if (kvittoUri == null || (kvittoUri == startUri && lasIgen == 0)) return@LaunchedEffect
        laserKvitto = true
        ingetHittades = false
        try {
            val forslag = lasKvitto(kvittoUri)
            ingetHittades = forslag == null
            if (forslag != null) {
                // Förslag som kom från ett tidigare avläst kvitto och inte ändrats av användaren får bytas ut vid ny
                // avläsning (rotera, läs av igen). Det användaren själv skrivit ändras aldrig.
                val idag = LocalDate.now()
                val ny = fyllTommaFalt(
                    forslag,
                    varKopt = if (forslagButik) "" else varKopt,
                    prisText = if (forslagPris) "" else prisText,
                    kopdatum = if (forslagDatum) idag else kopdatum,
                    idag = idag,
                    oppetKopDagar = if (forslagOppetKop) null else nuvarandeOppetKop(),
                )
                forslagVaror = forslag.varor
                if (ny.butikFranKvitto) {
                    varKopt = ny.varKopt
                    forslagButik = true
                }
                if (ny.prisFranKvitto) {
                    prisText = ny.prisText
                    forslagPris = true
                }
                if (ny.datumFranKvitto) {
                    kopdatum = ny.kopdatum
                    forslagDatum = true
                }
                if (ny.oppetKopFranKvitto) {
                    oppetKopIndex = oppetKopIndexFor(ny.oppetKopDagar)
                    oppetKopAnnat = if (oppetKopIndex == 3) ny.oppetKopDagar.toString() else ""
                    forslagOppetKop = true
                }
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
                nuvarandeOppetKop(),
                varutyp?.name,
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
                onRotera = onRotera,
                onLasIgen = { lasIgen++ },
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
            if (vad.isBlank() && forslagVaror.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.kvitto_varu_forslag),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    forslagVaror.forEach { namn ->
                        FilterChip(
                            selected = false,
                            onClick = { vad = namn },
                            label = { Text(text = namn) },
                        )
                    }
                }
            }
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

            Text(
                text = stringResource(R.string.garanti_hjalp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.falt_oppet_kop),
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                listOf(
                    R.string.oppet_kop_chip_inget,
                    R.string.oppet_kop_chip_14,
                    R.string.oppet_kop_chip_30,
                    R.string.oppet_kop_chip_annat,
                ).forEachIndexed { index, textRes ->
                    FilterChip(
                        selected = oppetKopIndex == index,
                        onClick = {
                            oppetKopIndex = index
                            oppetKopAnnat = ""
                            forslagOppetKop = false
                        },
                        label = { Text(text = stringResource(textRes)) },
                    )
                }
            }
            if (oppetKopIndex == 3) {
                OutlinedTextField(
                    value = oppetKopAnnat,
                    onValueChange = {
                        oppetKopAnnat = it.filter { c -> c.isDigit() }.take(3)
                        forslagOppetKop = false
                    },
                    label = { Text(text = stringResource(R.string.falt_oppet_kop)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Text(
                text = stringResource(
                    if (forslagOppetKop) R.string.kvitto_forslag else R.string.oppet_kop_hjalp,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.falt_varutyp),
                style = MaterialTheme.typography.labelLarge,
            )
            if (varutyp == Varutyp.MAT_DRYCK || varutyp == Varutyp.VAXTER) {
                Text(
                    text = stringResource(
                        if (varutyp == Varutyp.MAT_DRYCK) R.string.varutyp_mat_hjalp else R.string.varutyp_vaxter_hjalp,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Varutyp.entries.forEach { typ ->
                    FilterChip(
                        selected = varutyp == typ,
                        onClick = { varutyp = if (varutyp == typ) null else typ },
                        label = { Text(text = stringResource(varutypText(typ))) },
                    )
                }
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
    onRotera: (medurs: Boolean) -> Unit,
    onLasIgen: () -> Unit,
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onRotera(false) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.kvitto_rotera_vanster))
                    }
                    OutlinedButton(
                        onClick = { onRotera(true) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.kvitto_rotera_hoger))
                    }
                }
                TextButton(
                    onClick = onLasIgen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                ) {
                    Text(text = stringResource(R.string.kvitto_las_igen))
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
