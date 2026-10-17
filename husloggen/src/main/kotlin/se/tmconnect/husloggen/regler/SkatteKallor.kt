package se.tmconnect.husloggen.regler

/** Minsta summa förbättringsutgifter per kalenderår (5 000 kr). */
const val FORBATTRING_GRANS_ORE = 500_000L

const val SKATTE_KALLOR_KONTROLLDATUM = "2026-10-17"

const val SKATTE_KALLOR_SKATTEVERKET_SMÅHUS =
    "Skatteverket, Försäljning av småhus eller ägarlägenhet"

const val SKATTE_KALLOR_SKATTEVERKET_REPARATION =
    "Skatteverket, Reparationer och underhåll"

const val SKATTE_KALLOR_SKATTEVERKET_FORBATRING =
    "Skatteverket, Avdrag för renoveringar och nybyggnad (ej rotavdrag igen)"

const val SKATTE_KALLOR_BLANKETT = "Hjälpblankett SKV 2197"

/** Inkomstskattelagen (SFS 1999:1229), verifierat mot riksdagen.se 2026-10-17. */
const val SKATTE_LAGRUM_FASTIGHET = "45 kap. (avyttring av fastigheter)"

const val SKATTE_LAGRUM_BOSTADSRATT = "46 kap. (avyttring av bostadsrätter)"
