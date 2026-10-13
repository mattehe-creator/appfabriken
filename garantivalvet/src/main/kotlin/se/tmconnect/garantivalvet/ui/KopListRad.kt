package se.tmconnect.garantivalvet.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import java.time.LocalDate
import se.tmconnect.garantivalvet.R
import se.tmconnect.garantivalvet.data.Kop
import se.tmconnect.garantivalvet.regler.KopStatus
import se.tmconnect.garantivalvet.regler.appfabrikStatusFran
import se.tmconnect.garantivalvet.regler.beraknaInSampleSize
import se.tmconnect.garantivalvet.regler.formateraDatum
import se.tmconnect.garantivalvet.regler.statusForKop
import se.tmconnect.garantivalvet.regler.narmasteFrist
import se.tmconnect.garantivalvet.regler.NarmasteFristTyp
import se.tmconnect.garantivalvet.regler.visaMedVersal
import se.tmconnect.karna.tema.AppfabrikKortForm
import se.tmconnect.karna.tema.statusChipFarger

@Composable
fun KopListRad(
    kop: Kop,
    kvittoUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]
    val idag = LocalDate.now()
    val status = statusForKop(kop, idag)
    val chipFarger = statusChipFarger(appfabrikStatusFran(status))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
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
            KvittoMiniatyr(
                kvittoUri = kvittoUri,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = visaMedVersal(kop.vad),
                    style = MaterialTheme.typography.titleMedium,
                )
                kop.varKopt?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                narmasteFrist(kop, idag)?.let { frist ->
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
                KopStatus.GARANTI_GALLER -> stringResource(R.string.status_galler)
                KopStatus.GARANTI_SNART -> stringResource(R.string.status_gar_ut_snart)
                KopStatus.REKLAMATION_GALLER -> stringResource(R.string.status_reklamation_galler)
                KopStatus.UTGANGEN -> stringResource(R.string.status_utgangen)
                KopStatus.FARSKVARA -> stringResource(R.string.status_farskvara)
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
}

@Composable
private fun KvittoMiniatyr(
    kvittoUri: Uri?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (kvittoUri == null) {
        Icon(
            painter = painterResource(R.drawable.ic_kvitto),
            contentDescription = stringResource(R.string.kvitto_saknas),
            modifier = modifier.padding(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    val bitmap = remember(kvittoUri) {
        try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(kvittoUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
            val sampleSize = beraknaInSampleSize(options.outWidth, options.outHeight, maxSida = 128)
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
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        Icon(
            painter = painterResource(R.drawable.ic_kvitto),
            contentDescription = stringResource(R.string.kvitto_saknas),
            modifier = modifier.padding(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
