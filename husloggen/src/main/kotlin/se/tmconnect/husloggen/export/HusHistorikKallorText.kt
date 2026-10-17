package se.tmconnect.husloggen.export

import se.tmconnect.husloggen.regler.SKATTE_KALLOR_BLANKETT
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_KONTROLLDATUM
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_FORBATRING
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_REPARATION
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_SMÅHUS
import se.tmconnect.husloggen.regler.SKATTE_LAGRUM_BOSTADSRATT
import se.tmconnect.husloggen.regler.SKATTE_LAGRUM_FASTIGHET

fun skatteKallorText(): String =
    listOf(
        SKATTE_KALLOR_SKATTEVERKET_SMÅHUS,
        SKATTE_KALLOR_SKATTEVERKET_REPARATION,
        SKATTE_KALLOR_SKATTEVERKET_FORBATRING,
        SKATTE_KALLOR_BLANKETT,
        "Inkomstskattelagen: $SKATTE_LAGRUM_FASTIGHET, $SKATTE_LAGRUM_BOSTADSRATT.",
        "Kontrollerat $SKATTE_KALLOR_KONTROLLDATUM.",
    ).joinToString(" ")
