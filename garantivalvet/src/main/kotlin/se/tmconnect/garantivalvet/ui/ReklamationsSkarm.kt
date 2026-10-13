package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import se.tmconnect.garantivalvet.regler.MAX_FEL_BILDER
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.Krav
import se.tmconnect.garantivalvet.regler.ReklamationsTexter
import se.tmconnect.garantivalvet.regler.byggAiPrompt
import se.tmconnect.garantivalvet.regler.byggReklamationsbrev
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.formateraPrisKr
import se.tmconnect.karna.tema.AppfabrikKortForm

/** Hjälper användaren skriva ett reklamationsmeddelande till företaget i tre steg. Alltid gratis. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReklamationsSkarm(
    kop: Kop,
    kvittoFinns: Boolean,
    felBilder: List<FelBildPost> = emptyList(),
    onDelaTillAi: (text: String) -> Unit = {},
    onValjFelBild: () -> Unit = {},
    onTaFelFoto: () -> Unit = {},
    onTaBortFelBild: (filnamn: String) -> Unit = {},
    onSkickaMejl: (amne: String, text: String, bifogaUnderlag: Boolean) -> Unit,
    onSparaFel: (felUpptackt: LocalDate, beskrivning: String) -> Unit,
    onHarReklamerat: (felUpptackt: LocalDate, beskrivning: String) -> Unit,
    onTillbaka: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    var beskrivning by remember(kop.felBeskrivning) { mutableStateOf(kop.felBeskrivning.orEmpty()) }
    var upptackt by remember(kop.felUpptackt) { mutableStateOf(kop.felUpptackt ?: idag) }
    var krav by remember { mutableStateOf(Krav.AVHJALP) }
    var bifogaUnderlag by remember { mutableStateOf(false) }
    var visarDatumvaljare by remember { mutableStateOf(false) }
    var kopierad by remember { mutableStateOf(false) }
    val datumValjare = rememberDatePickerState(initialSelectedDateMillis = upptackt.toEpochDay() * 86_400_000)
    val clipboard = LocalClipboardManager.current

    val texter = ReklamationsTexter(
        amneMall = stringResource(R.string.brev_amne),
        hej = stringResource(R.string.brev_hej),
        intro = stringResource(R.string.brev_intro),
        vara = stringResource(R.string.brev_vara),
        butik = stringResource(R.string.brev_butik),
        kopdatum = stringResource(R.string.brev_kopdatum),
        pris = stringResource(R.string.brev_pris),
        kvittoBifogas = stringResource(R.string.brev_kvitto_bifogas),
        fel = stringResource(R.string.brev_fel),
        upptacktes = stringResource(R.string.brev_upptacktes),
        reklamationsdatum = stringResource(R.string.brev_reklamationsdatum),
        kravMall = stringResource(R.string.brev_krav_mall),
        kravAvhjalp = stringResource(R.string.brev_krav_avhjalp),
        kravPrisavdrag = stringResource(R.string.brev_krav_prisavdrag),
        kravHava = stringResource(R.string.brev_krav_hava),
        bekrafta = stringResource(R.string.brev_bekrafta),
        avslut = stringResource(R.string.brev_avslut),
        halsning = stringResource(R.string.brev_halsning),
    )
    val brev = byggReklamationsbrev(
        vad = kop.vad,
        butik = kop.varKopt,
        kopdatum = kop.kopdatum,
        prisOre = kop.prisOre,
        kvittoFinns = kvittoFinns,
        felBeskrivning = beskrivning.ifBlank { "…" },
        felUpptackt = upptackt,
        krav = krav,
        texter = texter,
        locale = locale,
        idag = idag,
    )
    var egenText by remember { mutableStateOf<String?>(null) }
    var redigerar by remember { mutableStateOf(false) }
    val brevText = egenText ?: brev.text
    val kanSkicka = beskrivning.isNotBlank()
    val aiMallSkriv = stringResource(R.string.ai_mall_skriv)
    val aiMallSvara = stringResource(R.string.ai_mall_svara)
    fun aiPrompt(mall: String) = byggAiPrompt(
        mall = mall,
        vad = kop.vad,
        butik = kop.varKopt,
        kopdatum = kop.kopdatum,
        prisOre = kop.prisOre,
        felBeskrivning = beskrivning,
        felUpptackt = upptackt,
        krav = krav,
        texter = texter,
        locale = locale,
        idag = idag,
    )

    if (visarDatumvaljare) {
        DatePickerDialog(
            onDismissRequest = { visarDatumvaljare = false },
            confirmButton = {
                Button(
                    onClick = {
                        datumValjare.selectedDateMillis?.let { millis ->
                            val valt = LocalDate.ofEpochDay(millis / 86_400_000)
                            upptackt = if (valt.isAfter(idag)) idag else valt
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.reklamera_titel)) },
                navigationIcon = {
                    IconButton(onClick = onTillbaka) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (!kanSkicka) {
                        Text(
                            text = stringResource(R.string.reklamera_beskriv_fel_forst),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Button(
                        onClick = {
                            onSparaFel(upptackt, beskrivning)
                            onSkickaMejl(brev.amne, brevText, bifogaUnderlag)
                        },
                        enabled = kanSkicka,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                    ) {
                        Text(text = stringResource(R.string.reklamera_skicka_mejl))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = {
                                onSparaFel(upptackt, beskrivning)
                                clipboard.setText(AnnotatedString("${brev.amne}\n\n$brevText"))
                                kopierad = true
                            },
                            enabled = kanSkicka,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                        ) {
                            Text(
                                text = stringResource(
                                    if (kopierad) R.string.reklamera_kopierad else R.string.reklamera_kopiera,
                                ),
                            )
                        }
                        TextButton(
                            onClick = { onHarReklamerat(upptackt, beskrivning) },
                            enabled = kanSkicka,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp),
                        ) {
                            Text(text = stringResource(R.string.reklamera_har_reklamerat))
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            KopSammanfattning(kop = kop, kvittoFinns = kvittoFinns, locale = locale)

            Steg(rubrik = stringResource(R.string.reklamera_steg_1)) {
                OutlinedTextField(
                    value = beskrivning,
                    onValueChange = {
                        beskrivning = it
                        kopierad = false
                    },
                    label = { Text(text = stringResource(R.string.reklamera_fel_etikett)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = formateraDatum(upptackt, locale),
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.reklamera_upptackt_etikett)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { visarDatumvaljare = true }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = stringResource(R.string.reklamera_valj_datum),
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                FelBilderRuta(
                    felBilder = felBilder,
                    onValjFelBild = onValjFelBild,
                    onTaFelFoto = onTaFelFoto,
                    onTaBortFelBild = onTaBortFelBild,
                )
            }

            Steg(rubrik = stringResource(R.string.reklamera_steg_2)) {
                KravVal(
                    vald = krav == Krav.AVHJALP,
                    titel = stringResource(R.string.reklamera_krav_avhjalp),
                    forklaring = stringResource(R.string.reklamera_krav_avhjalp_forklaring),
                    onVal = {
                        krav = Krav.AVHJALP
                        kopierad = false
                    },
                )
                KravVal(
                    vald = krav == Krav.PRISAVDRAG,
                    titel = stringResource(R.string.reklamera_krav_prisavdrag),
                    forklaring = stringResource(R.string.reklamera_krav_prisavdrag_forklaring),
                    onVal = {
                        krav = Krav.PRISAVDRAG
                        kopierad = false
                    },
                )
                KravVal(
                    vald = krav == Krav.HAVA,
                    titel = stringResource(R.string.reklamera_krav_hava),
                    forklaring = stringResource(R.string.reklamera_krav_hava_forklaring),
                    onVal = {
                        krav = Krav.HAVA
                        kopierad = false
                    },
                )
            }

            Steg(rubrik = stringResource(R.string.reklamera_steg_3)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppfabrikKortForm,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = brev.amne,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        HorizontalDivider()
                        if (redigerar) {
                            OutlinedTextField(
                                value = brevText,
                                onValueChange = {
                                    egenText = it
                                    kopierad = false
                                },
                                minLines = 8,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        } else {
                            Text(text = brevText, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { redigerar = !redigerar },
                        modifier = Modifier.heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(if (redigerar) R.string.ai_klar_redigera else R.string.ai_redigera))
                    }
                    TextButton(
                        onClick = {
                            clipboard.getText()?.text?.takeIf { it.isNotBlank() }?.let {
                                egenText = it
                                kopierad = false
                            }
                        },
                        modifier = Modifier.heightIn(min = 48.dp),
                    ) {
                        Text(text = stringResource(R.string.ai_klistra_in))
                    }
                    if (egenText != null) {
                        TextButton(
                            onClick = {
                                egenText = null
                                kopierad = false
                            },
                            modifier = Modifier.heightIn(min = 48.dp),
                        ) {
                            Text(text = stringResource(R.string.ai_aterstall))
                        }
                    }
                }
                AiHjalpKort(
                    kanDela = kanSkicka,
                    onSkriv = { onDelaTillAi(aiPrompt(aiMallSkriv)) },
                    onSvara = { onDelaTillAi(aiPrompt(aiMallSvara)) },
                    onKopieraSkriv = { clipboard.setText(AnnotatedString(aiPrompt(aiMallSkriv))) },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.reklamera_bifoga_underlag),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = stringResource(R.string.reklamera_bifoga_underlag_hjalp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(checked = bifogaUnderlag, onCheckedChange = { bifogaUnderlag = it })
                }
            }

            InfoRuta(text = stringResource(R.string.reklamera_info))
        }
    }
}

/** En sparad bild på felet: filnamnet i lagret och URI:n som visas. */
data class FelBildPost(val filnamn: String, val uri: Uri?)

@Composable
private fun FelBilderRuta(
    felBilder: List<FelBildPost>,
    onValjFelBild: () -> Unit,
    onTaFelFoto: () -> Unit,
    onTaBortFelBild: (String) -> Unit,
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.reklamera_bilder_rubrik), style = MaterialTheme.typography.titleSmall)
        Text(
            text = stringResource(R.string.reklamera_bilder_hjalp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (felBilder.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                felBilder.forEach { post ->
                    val bitmap = remember(post.uri) {
                        val uri = post.uri ?: return@remember null
                        try {
                            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
                            val sample = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 320)
                            context.contentResolver.openInputStream(uri)?.use {
                                BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }
                    Box(modifier = Modifier.size(104.dp)) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = stringResource(R.string.reklamera_bild_beskrivning),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 4.dp, end = 4.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                            )
                        }
                        IconButton(
                            onClick = { onTaBortFelBild(post.filnamn) },
                            modifier = Modifier.align(Alignment.TopEnd).size(48.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.reklamera_bild_ta_bort),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f), CircleShape)
                                    .padding(4.dp),
                            )
                        }
                    }
                }
            }
        }
        if (felBilder.size < MAX_FEL_BILDER) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onTaFelFoto,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                ) { Text(text = stringResource(R.string.reklamera_bild_foto)) }
                OutlinedButton(
                    onClick = onValjFelBild,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                ) { Text(text = stringResource(R.string.reklamera_bild_galleri)) }
            }
        }
    }
}

/** Frivillig hjälp av användarens egen AI-app: frågan delas via telefonens delningsmeny, appen skickar inget själv. */
@Composable
private fun AiHjalpKort(
    kanDela: Boolean,
    onSkriv: () -> Unit,
    onSvara: () -> Unit,
    onKopieraSkriv: () -> Unit,
) {
    var visaTips by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.ai_rubrik),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.ai_beskrivning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onSkriv,
                    enabled = kanDela,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                ) { Text(text = stringResource(R.string.ai_skriv_knapp)) }
                OutlinedButton(
                    onClick = onSvara,
                    enabled = kanDela,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                ) { Text(text = stringResource(R.string.ai_svara_knapp)) }
            }
            TextButton(onClick = onKopieraSkriv, enabled = kanDela, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(text = stringResource(R.string.ai_kopiera_fraga))
            }
            TextButton(onClick = { visaTips = !visaTips }, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(text = stringResource(if (visaTips) R.string.ai_dolj_tips else R.string.ai_visa_tips))
            }
            if (visaTips) {
                Text(text = stringResource(R.string.ai_tips), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Steg(rubrik: String, innehall: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = rubrik,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        innehall()
    }
}

@Composable
private fun KopSammanfattning(kop: Kop, kvittoFinns: Boolean, locale: java.util.Locale) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = kop.vad, style = MaterialTheme.typography.titleMedium)
            val rad = listOfNotNull(
                kop.varKopt?.takeIf { it.isNotBlank() },
                formateraDatum(kop.kopdatum, locale),
                kop.prisOre?.let { formateraPrisKr(it, locale) },
            ).joinToString(" · ")
            Text(
                text = rad,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    if (kvittoFinns) R.string.reklamera_kvitto_bifogas else R.string.reklamera_inget_kvitto,
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun KravVal(vald: Boolean, titel: String, forklaring: String, onVal: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = vald, onClick = onVal, role = Role.RadioButton),
        shape = AppfabrikKortForm,
        colors = CardDefaults.cardColors(
            containerColor = if (vald) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            },
        ),
        border = BorderStroke(
            width = if (vald) 2.dp else 1.dp,
            color = if (vald) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = titel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                text = forklaring,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoRuta(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
