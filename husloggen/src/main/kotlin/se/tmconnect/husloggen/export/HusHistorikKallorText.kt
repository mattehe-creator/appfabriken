package se.tmconnect.husloggen.export

import se.tmconnect.husloggen.regler.SKATTE_KALLOR_BLANKETT
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_KONTROLLDATUM
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_FORBATRING
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_REPARATION
import se.tmconnect.husloggen.regler.SKATTE_KALLOR_SKATTEVERKET_SMÅHUS

/**
 * Källtexten i husets historik. Rubrikerna från Skatteverket är namn på svenska sidor och står
 * kvar på svenska; raden om lagen och kontrolldatumet kommer från strängresurserna.
 *
 * @param lagRad till exempel "Inkomstskattelagen: 45 kap. (...), 46 kap. (...)."
 * @param kontrolleradRad får kontrolldatumet, till exempel "Kontrollerat 2026-10-17."
 */
fun skatteKallorText(lagRad: String, kontrolleradRad: (String) -> String): String =
    listOf(
        SKATTE_KALLOR_SKATTEVERKET_SMÅHUS,
        SKATTE_KALLOR_SKATTEVERKET_REPARATION,
        SKATTE_KALLOR_SKATTEVERKET_FORBATRING,
        SKATTE_KALLOR_BLANKETT,
        lagRad,
        kontrolleradRad(SKATTE_KALLOR_KONTROLLDATUM),
    ).joinToString(" ")
