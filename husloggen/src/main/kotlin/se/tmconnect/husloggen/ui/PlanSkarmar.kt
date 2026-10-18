package se.tmconnect.husloggen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import java.util.Locale
import se.tmconnect.husloggen.R
import se.tmconnect.husloggen.data.PlaneradKostnad
import se.tmconnect.husloggen.regler.PlaneradKostnadFel
import se.tmconnect.husloggen.regler.grupperaPlaneradeKostnaderPerAr
import se.tmconnect.karna.format.formateraPrisKr

@Composable
fun PlanFlik(
    kostnader: List<PlaneradKostnad>,
    locale: Locale,
    contentPadding: PaddingValues,
    onNyPlaneradKostnad: () -> Unit,
    onAndraPlaneradKostnad: (Long) -> Unit,
    onGenomford: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (kostnader.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.tom_plan),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            OutlinedButton(
                onClick = onNyPlaneradKostnad,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.fab_ny_planerad_kostnad))
            }
        }
    } else {
        val grupper = remember(kostnader) { grupperaPlaneradeKostnaderPerAr(kostnader) }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            grupper.forEach { grupp ->
                item(key = "ar-${grupp.ar}") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = grupp.ar.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(
                                R.string.plan_ar_summa,
                                formateraPrisKr(grupp.summaOre, locale),
                            ),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
                items(grupp.kostnader, key = { it.id }) { rad ->
                    PlaneradKostnadRad(
                        rad = rad,
                        locale = locale,
                        onAndra = { onAndraPlaneradKostnad(rad.id) },
                        onGenomford = { onGenomford(rad.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaneradKostnadRad(
    rad: PlaneradKostnad,
    locale: Locale,
    onAndra: () -> Unit,
    onGenomford: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAndra)
            .tryckyta48()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = rad.titel,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formateraPrisKr(rad.beloppOre, locale),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        rad.anteckning?.let { anteckning ->
            Text(
                text = anteckning,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        OutlinedButton(
            onClick = onGenomford,
            modifier = Modifier
                .padding(top = 8.dp)
                .tryckyta48(),
        ) {
            Text(text = stringResource(R.string.plan_genomford))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanFormularSkarm(
    tillstand: PlanFormularTillstand,
    innevarandeAr: Int,
    onTitelAndras: (String) -> Unit,
    onBeloppAndras: (String) -> Unit,
    onPlaneratArAndras: (String) -> Unit,
    onAnteckningAndras: (String) -> Unit,
    onSpara: () -> Unit,
    onTaBort: () -> Unit,
    onStang: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rubrik =
        if (tillstand.redigerarId == 0L) {
            stringResource(R.string.ny_planerad_kostnad_rubrik)
        } else {
            stringResource(R.string.andra_planerad_kostnad_rubrik)
        }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = rubrik) },
                navigationIcon = {
                    IconButton(onClick = onStang, modifier = Modifier.tryckyta48()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.stang),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = tillstand.titel,
                onValueChange = onTitelAndras,
                label = { Text(text = stringResource(R.string.falt_plan_titel)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == PlaneradKostnadFel.TITEL_SAKNAS,
                supportingText = {
                    if (tillstand.valideringsFel == PlaneradKostnadFel.TITEL_SAKNAS) {
                        Text(text = stringResource(R.string.fel_plan_titel_saknas))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.beloppText,
                onValueChange = onBeloppAndras,
                label = { Text(text = stringResource(R.string.falt_belopp)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == PlaneradKostnadFel.BELOPP_SAKNAS_ELLER_NOLL,
                supportingText = {
                    if (tillstand.valideringsFel == PlaneradKostnadFel.BELOPP_SAKNAS_ELLER_NOLL) {
                        Text(text = stringResource(R.string.fel_plan_belopp))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.planeratArText,
                onValueChange = onPlaneratArAndras,
                label = { Text(text = stringResource(R.string.falt_planerat_ar)) },
                modifier = Modifier.fillMaxWidth(),
                isError = tillstand.valideringsFel == PlaneradKostnadFel.AR_FORE_INNEVARANDE,
                supportingText = {
                    when (tillstand.valideringsFel) {
                        PlaneradKostnadFel.AR_FORE_INNEVARANDE ->
                            Text(text = stringResource(R.string.fel_plan_ar, innevarandeAr))
                        else ->
                            Text(text = stringResource(R.string.falt_planerat_ar_hjalp, innevarandeAr))
                    }
                },
            )
            OutlinedTextField(
                value = tillstand.anteckning,
                onValueChange = onAnteckningAndras,
                label = { Text(text = stringResource(R.string.falt_anteckning)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSpara,
                modifier = Modifier
                    .fillMaxWidth()
                    .tryckyta48(),
            ) {
                Text(text = stringResource(R.string.spara))
            }
            if (tillstand.redigerarId != 0L) {
                TextButton(
                    onClick = onTaBort,
                    modifier = Modifier
                        .fillMaxWidth()
                        .tryckyta48(),
                ) {
                    Text(text = stringResource(R.string.ta_bort))
                }
            }
        }
    }
}
