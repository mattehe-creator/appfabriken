package se.tmconnect.garantivalvet.ui

sealed interface KopSkarm {
    data object Lista : KopSkarm

    data object Installningar : KopSkarm

    data object LaggTill : KopSkarm

    data class Detalj(val kopId: Long) : KopSkarm

    data class Andra(val kopId: Long) : KopSkarm

    data class Reklamera(val kopId: Long) : KopSkarm

    data class KvittoHelskarm(val kopId: Long) : KopSkarm
}
