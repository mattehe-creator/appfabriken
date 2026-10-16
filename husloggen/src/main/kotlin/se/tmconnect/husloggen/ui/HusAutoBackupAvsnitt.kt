package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import se.tmconnect.husloggen.R
import se.tmconnect.karna.sakerhetskopia.AUTO_INTERVALL_DAGLIGEN
import se.tmconnect.karna.sakerhetskopia.AUTO_INTERVALL_MANADSVIS
import se.tmconnect.karna.sakerhetskopia.AUTO_INTERVALL_VECKOVIS
import se.tmconnect.karna.sakerhetskopia.AutoBackupTillstand

@Composable
fun HusAutoBackupAvsnitt(
    tillstand: AutoBackupTillstand,
    onPa: (Boolean) -> Unit,
    onByttMapp: () -> Unit,
    onIntervall: (Int) -> Unit,
    onKorNu: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.auto_rubrik), style = MaterialTheme.typography.titleMedium)
        Text(
            text = stringResource(R.string.auto_beskrivning),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(R.string.auto_pa), style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = tillstand.pa,
                onCheckedChange = onPa,
                modifier = Modifier.tryckyta48(),
            )
        }
        if (tillstand.pa) {
            Text(
                text = stringResource(
                    R.string.auto_mapp,
                    tillstand.mappNamn ?: stringResource(R.string.auto_mapp_okand),
                ),
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onByttMapp, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.auto_byt_mapp))
                }
                Button(onClick = onKorNu, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(stringResource(R.string.auto_kor_nu))
                }
            }
            IntervallKnapp(
                text = stringResource(R.string.auto_dagligen),
                valt = tillstand.intervallDagar == AUTO_INTERVALL_DAGLIGEN,
                onClick = { onIntervall(AUTO_INTERVALL_DAGLIGEN) },
            )
            IntervallKnapp(
                text = stringResource(R.string.auto_veckovis),
                valt = tillstand.intervallDagar == AUTO_INTERVALL_VECKOVIS,
                onClick = { onIntervall(AUTO_INTERVALL_VECKOVIS) },
            )
            IntervallKnapp(
                text = stringResource(R.string.auto_manadsvis),
                valt = tillstand.intervallDagar == AUTO_INTERVALL_MANADSVIS,
                onClick = { onIntervall(AUTO_INTERVALL_MANADSVIS) },
            )
            val senasteText =
                tillstand.senaste?.let {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", locale)
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(formatter)
                }
            Text(
                text =
                    if (senasteText != null) {
                        stringResource(R.string.auto_senaste, senasteText)
                    } else {
                        stringResource(R.string.auto_ingen_an)
                    },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (tillstand.fel) {
                Text(
                    text = stringResource(R.string.auto_fel),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun IntervallKnapp(text: String, valt: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        enabled = !valt,
    ) {
        Text(text)
    }
}
