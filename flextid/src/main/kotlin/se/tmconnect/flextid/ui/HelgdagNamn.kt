package se.tmconnect.flextid.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import se.tmconnect.flextid.R
import se.tmconnect.flextid.regler.Helgdag

/** Strängresurs för en helgdag. Namnen i `regler/Helgdagar.kt` är regeldata på svenska. */
@StringRes
fun helgdagNamnRes(namn: String): Int? =
    when (namn) {
        "Nyårsdagen" -> R.string.helg_nyarsdagen
        "Trettondedag jul" -> R.string.helg_trettondedag_jul
        "Långfredagen" -> R.string.helg_langfredagen
        "Påskdagen" -> R.string.helg_paskdagen
        "Annandag påsk" -> R.string.helg_annandag_pask
        "Första maj" -> R.string.helg_forsta_maj
        "Kristi himmelsfärdsdag" -> R.string.helg_kristi_himmelsfard
        "Pingstdagen" -> R.string.helg_pingstdagen
        "Sveriges nationaldag" -> R.string.helg_nationaldagen
        "Midsommardagen" -> R.string.helg_midsommardagen
        "Alla helgons dag" -> R.string.helg_alla_helgons_dag
        "Juldagen" -> R.string.helg_juldagen
        "Annandag jul" -> R.string.helg_annandag_jul
        "Midsommarafton" -> R.string.helg_midsommarafton
        "Julafton" -> R.string.helg_julafton
        "Nyårsafton" -> R.string.helg_nyarsafton
        else -> null
    }

@Composable
fun helgdagNamn(helg: Helgdag): String = helgdagNamnRes(helg.namn)?.let { stringResource(it) } ?: helg.namn
