package se.tmconnect.rostfangst.lokal

enum class TolkningsLage { ANTHROPIC, MIN_DATOR, MIN_DATOR_ANNARS_ANTHROPIC }

enum class LokalFel { NATFEL, TIMEOUT, OGILTIGT_SVAR }

sealed interface Val {
    object AnvandAnthropic : Val
    object AnvandDatorn : Val
    data class Fel(val fel: LokalFel) : Val
}

fun valjTolkning(lage: TolkningsLage, lokalFel: LokalFel?): Val {
    return when (lage) {
        TolkningsLage.ANTHROPIC -> Val.AnvandAnthropic
        TolkningsLage.MIN_DATOR -> {
            if (lokalFel == null) Val.AnvandDatorn else Val.Fel(lokalFel)
        }
        TolkningsLage.MIN_DATOR_ANNARS_ANTHROPIC -> {
            when (lokalFel) {
                null -> Val.AnvandDatorn
                LokalFel.NATFEL, LokalFel.TIMEOUT -> Val.AnvandAnthropic
                LokalFel.OGILTIGT_SVAR -> Val.Fel(LokalFel.OGILTIGT_SVAR)
            }
        }
    }
}
