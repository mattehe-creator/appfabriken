package se.tmconnect.husloggen.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import se.tmconnect.husloggen.R

/** Flikar som nås via Mer. Bakåt från dem går till Mer. */
val MER_FLIKAR = setOf(HusFlik.PLAN, HusFlik.MATARE, HusFlik.DOKUMENT)

/** Vilken knapp i navigeringsfältet som är markerad för en flik. */
fun navigeringsknappFor(flik: HusFlik): HusFlik =
    if (flik in MER_FLIKAR) HusFlik.MER else flik

private data class Knapp(val flik: HusFlik, val ikon: ImageVector, val etikett: Int)

private val KNAPPAR = listOf(
    Knapp(HusFlik.ATT_GORA, Icons.Filled.Home, R.string.flik_att_gora),
    Knapp(HusFlik.LOGG, Icons.AutoMirrored.Filled.List, R.string.flik_logg),
    Knapp(HusFlik.SAMMANSTALLNING, Icons.Filled.DateRange, R.string.flik_oversikt),
    Knapp(HusFlik.MER, Icons.Filled.Menu, R.string.flik_mer),
)

/**
 * Fyra knappar enligt Material (3–5 mål): Att göra, Logg, Översikt och Mer.
 * Planerade kostnader, mätare och dokument nås via Mer.
 */
@Composable
fun HusNavigering(
    valdFlik: HusFlik,
    onValjFlik: (HusFlik) -> Unit,
) {
    val markerad = navigeringsknappFor(valdFlik)
    NavigationBar {
        KNAPPAR.forEach { k ->
            NavigationBarItem(
                selected = markerad == k.flik,
                onClick = { onValjFlik(k.flik) },
                icon = { Icon(k.ikon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(k.etikett),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

private data class MerRad(
    val flik: HusFlik,
    val ikon: ImageVector,
    val titel: Int,
    val text: Int,
    val antal: Int,
)

@Composable
fun MerFlik(
    antalPlanerade: Int,
    antalMatare: Int,
    antalDokument: Int,
    contentPadding: PaddingValues,
    onValjFlik: (HusFlik) -> Unit,
) {
    val rader = listOf(
        MerRad(HusFlik.PLAN, Icons.Filled.ShoppingCart, R.string.mer_plan_titel, R.string.mer_plan_text, antalPlanerade),
        MerRad(HusFlik.MATARE, Icons.Filled.Build, R.string.flik_matare, R.string.mer_matare_text, antalMatare),
        MerRad(HusFlik.DOKUMENT, Icons.Filled.Info, R.string.flik_dokument, R.string.mer_dokument_text, antalDokument),
    )
    LazyColumn(contentPadding = contentPadding) {
        items(rader, key = { it.flik.name }) { r ->
            ListItem(
                headlineContent = { Text(stringResource(r.titel)) },
                supportingContent = { Text(stringResource(r.text)) },
                leadingContent = { Icon(r.ikon, contentDescription = null) },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (r.antal > 0) Text(text = r.antal.toString())
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .tryckyta48()
                    .clickable(role = Role.Button) { onValjFlik(r.flik) },
            )
            HorizontalDivider()
        }
    }
}
