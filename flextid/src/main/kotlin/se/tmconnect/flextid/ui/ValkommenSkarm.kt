package se.tmconnect.flextid.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import se.tmconnect.flextid.R
import se.tmconnect.karna.ui.ValkommenSkarm as KarnaValkommenSkarm
import se.tmconnect.karna.ui.ValkommenSteg

@Composable
fun ValkommenSkarm(
    onStamplaIn: () -> Unit,
    onTittaRunt: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KarnaValkommenSkarm(
        rubrik = stringResource(R.string.valkommen_rubrik),
        ingress = stringResource(R.string.valkommen_ingress),
        steg =
            listOf(
                ValkommenSteg(
                    stringResource(R.string.valkommen_steg1_rubrik),
                    stringResource(R.string.valkommen_steg1_text),
                ),
                ValkommenSteg(
                    stringResource(R.string.valkommen_steg2_rubrik),
                    stringResource(R.string.valkommen_steg2_text),
                ),
                ValkommenSteg(
                    stringResource(R.string.valkommen_steg3_rubrik),
                    stringResource(R.string.valkommen_steg3_text),
                ),
            ),
        integritet = stringResource(R.string.valkommen_integritet),
        primarKnappText = stringResource(R.string.valkommen_stampla_in),
        onPrimarKnapp = onStamplaIn,
        sekundarKnappText = stringResource(R.string.valkommen_titta),
        onSekundarKnapp = onTittaRunt,
        modifier = modifier,
    )
}
