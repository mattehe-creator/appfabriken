package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.IntervallEnhet
import se.tmconnect.husloggen.regler.intervallEtikett
import se.tmconnect.husloggen.regler.nastaDatum
import se.tmconnect.husloggen.regler.UppgiftStatus
import se.tmconnect.husloggen.regler.uppgiftStatus
import se.tmconnect.husloggen.regler.tolkaBilder
import se.tmconnect.karna.format.formateraDatum
import se.tmconnect.karna.format.formateraPrisKr
import se.tmconnect.karna.tema.AppfabrikStatus
import se.tmconnect.karna.tema.statusChipFarger

private fun appfabrikStatusFran(status: UppgiftStatus): AppfabrikStatus =
    when (status) {
        UppgiftStatus.FORSENAD -> AppfabrikStatus.UTGANGEN
        UppgiftStatus.SNART -> AppfabrikStatus.SNART
        UppgiftStatus.SENARE -> AppfabrikStatus.GALLER
    }

@Composable
fun UppgiftListRad(
    uppgift: Uppgift,
    onClick: () -> Unit,
    onMarkeraGjord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    val nasta = nastaDatum(uppgift.senastGjord, uppgift.startDatum, uppgift.intervallManader)
    val status = uppgiftStatus(nasta, idag)
    val chipFarger = statusChipFarger(appfabrikStatusFran(status))
    val (enhet, antal) = intervallEtikett(uppgift.intervallManader)
    val intervallText = when (enhet) {
        IntervallEnhet.AR -> pluralStringResource(R.plurals.intervall_ar, antal, antal)
        IntervallEnhet.MANAD -> pluralStringResource(R.plurals.intervall_manader, antal, antal)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
            ) {
                Text(
                    text = uppgift.namn,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = formateraDatum(nasta, locale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Text(
                    text = intervallText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            IconButton(
                onClick = onMarkeraGjord,
                modifier = Modifier.tryckyta48(),
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = stringResource(R.string.markera_gjord),
                )
            }

            AssistChip(
                onClick = onClick,
                label = {
                    Text(
                        text = when (status) {
                            UppgiftStatus.FORSENAD -> stringResource(R.string.status_forsenad)
                            UppgiftStatus.SNART -> stringResource(R.string.status_snart)
                            UppgiftStatus.SENARE -> stringResource(R.string.status_senare)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = chipFarger.container,
                    labelColor = chipFarger.content,
                ),
                modifier = Modifier.tryckyta48(),
            )
        }
    }
}

@Composable
fun PostListRad(
    post: Post,
    locale: java.util.Locale,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val belopp = formateraPrisKr(post.beloppOre, locale)
    val typText = postTypEtikett(post.typ)
    val radText = buildString {
        append(post.vad)
        append(" · ")
        append(formateraDatum(post.datum, locale))
        append(" · ")
        append(typText)
        if (belopp.isNotEmpty()) {
            append(" · ")
            append(belopp)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        ) {
            Text(
                text = radText,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        if (tolkaBilder(post.bilder).isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.bild_har_bilder),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
