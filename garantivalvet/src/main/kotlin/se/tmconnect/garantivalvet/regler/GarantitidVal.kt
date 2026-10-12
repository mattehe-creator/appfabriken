package se.tmconnect.garantivalvet.regler

sealed class GarantitidVal {
    object ForvaldManader : GarantitidVal()
    data class ChipManader(val manader: Int) : GarantitidVal()
    data class Annat(val text: String) : GarantitidVal()

    companion object {
        const val FORVALD_MANADER = 24
        val CHIP_MANADER = listOf(12, 24, 36)

        fun manaderFranVal(chipIndex: Int, annatText: String): Int? {
            return when (chipIndex) {
                in 0..2 -> CHIP_MANADER[chipIndex]
                3 -> annatText.toIntOrNull()
                else -> null
            }
        }
    }
}
