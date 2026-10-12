package se.tmconnect.garantivalvet.regler

object GarantitidVal {
    const val FORVALD_MANADER = 24
    val CHIP_MANADER = listOf(12, 24, 36)
    
    fun manaderFranVal(chipIndex: Int, annatText: String): Int? {
        return when (chipIndex) {
            in CHIP_MANADER.indices -> CHIP_MANADER[chipIndex]
            3 -> {
                // För "Annat" - tolka texten som ett positivt heltal
                try {
                    val antal = annatText.toInt()
                    if (antal > 0) antal else null
                } catch (e: NumberFormatException) {
                    null
                }
            }
            else -> null
        }
    }
}
