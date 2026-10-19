package se.tmconnect.styrelserummet.regler

import se.tmconnect.styrelserummet.modell.Komponent

data class Avstamning(
    val summa: Long,
    val differens: Long,
    val stammer: Boolean,
)

fun stamAv(komponenter: List<Komponent>, byggnadensVarde: Long): Avstamning {
    val summa = komponenter.sumOf { it.anskaffningsvarde }
    val differens = byggnadensVarde - summa
    return Avstamning(summa = summa, differens = differens, stammer = differens == 0L)
}
