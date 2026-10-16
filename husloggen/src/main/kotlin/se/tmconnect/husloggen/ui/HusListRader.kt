package se.tmconnect.husloggen.ui

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.Uppgift
import se.tmconnect.husloggen.regler.NarmasteFristTyp
import se.tmconnect.husloggen.regler.appfabrikStatusFran
import se.tmconnect.husloggen.regler.beraknaInSampleSize
import se.tmconnect.husloggen.regler.formateraDatum
import se.tmconnect.husloggen.regler.formateraPrisKr
import se.tmconnect.husloggen.regler.narmasteFrist
import se.tmconnect.husloggen.regler.sorteraUppgifter
import se.tmconnect.husloggen.regler.statusForKop
import se.tmconnect.karna.tema.AppfabrikKortForm
import se.tmconnect.karna.tema.statusChipFarger

@Composable
fun UppgiftListRad(
    uppgift: Uppgift,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    val nasta = se.tmconnect.husloggen.regler.nastaDatum(uppgift.senastGjord, uppgift.startDatum, uppgift.intervallManader)
    val status = se.tmconnect.husloggen.regler.uppgiftStatus(nasta, idag)
    val chipFarger = statusChipFarger(appfabrikStatusFran(status))
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClick = onClick),
        shape = AppfabrikKortForm,
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
                    .padding(end = 12.dp),
            ) {
                Text(
                    text = uppgift.namn,
                    style = MaterialTheme.typography.titleMedium,
                )
                narmasteFrist(uppgift, idag)?.let { frist ->
                    val fristText = when (frist.typ) {
                        NarmasteFristTyp.GARANTI -> stringResource(
                            R.string.lista_garanti_till,
                            formateraDatum(frist.datum, locale),
                        )
                        NarmasteFristTyp.REKLAMATION -> stringResource(
                            R.string.lista_reklamation_till,
                            formateraDatum(frist.datum, locale),
                        )
                        NarmasteFristTyp.OPPET_KOP -> stringResource(
                            R.string.lista_oppet_kop_till,
                            formateraDatum(frist.datum, locale),
                        )
                    }
                    Text(
                        text = fristText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            val statusText = when (status) {
                se.tmconnect.husloggen.regler.UppgiftStatus.FORSENAD -> stringResource(R.string.status_forsenad)
                se.tmconnect.husloggen.regler.UppgiftStatus.SNART -> stringResource(R.string.status_snart)
                se.tmconnect.husloggen.regler.UppgiftStatus.SENARE -> stringResource(R.string.status_senare)
            }
            AssistChip(
                onClick = onClick,
                label = {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = chipFarger.container,
                    labelColor = chipFarger.content,
                ),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .tryckyta48(),
            )
        }
    }
}

@Composable
fun PostListRad(
    post: Post,
    locale: java.util.Locale,
    modifier: Modifier = Modifier,
) {
    val belopp = formateraPrisKr(post.beloppOre, locale)
    val radText = buildString {
        append(formateraDatum(post.datum, locale))
        append(" · ")
        append(post.vad)
        if (belopp.isNotEmpty()) {
            append(" · ")
            append(belopp)
        }
    }
    
    Text(
        text = radText,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable { }
            .padding(vertical = 8.dp),
    )
}
