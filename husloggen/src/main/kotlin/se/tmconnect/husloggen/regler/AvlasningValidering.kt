package se.tmconnect.husloggen.regler

import java.time.LocalDate
import se.tmconnect.husloggen.data.Avlasning

enum class AvlasningFel {
    VardeOgiltigt,
    DatumRedanAnvant,
}

fun valideraAvlasning(
    vardeText: String,
    datum: LocalDate,
    befintliga: List<Avlasning>,
    redigerarId: Long = 0L,
): AvlasningFel? {
    if (tolkaMatareVardeTillTusendel(vardeText) == null) return AvlasningFel.VardeOgiltigt
    val datumUpptaget =
        befintliga.any { it.datum == datum && it.id != redigerarId }
    if (datumUpptaget) return AvlasningFel.DatumRedanAnvant
    return null
}

fun behoverBekraftelseLagreAnForegaende(
    nyVarde: Long,
    foregaendeVarde: Long?,
): Boolean = foregaendeVarde != null && nyVarde < foregaendeVarde

fun foregaendeAvlasning(
    datum: LocalDate,
    sorterade: List<Avlasning>,
    redigerarId: Long = 0L,
): Avlasning? =
    sorterade
        .filter { it.id != redigerarId && !it.datum.isAfter(datum) }
        .maxByOrNull { it.datum }
