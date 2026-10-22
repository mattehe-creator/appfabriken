package se.tmconnect.flextid.regler

enum class SaldoStatus { INOM, OVER_TAKET, UNDER_GOLVET }

fun saldoStatus(saldoMin: Int, takMin: Int?, golvMin: Int?): SaldoStatus =
    when {
        takMin != null && saldoMin > takMin -> SaldoStatus.OVER_TAKET
        golvMin != null && saldoMin < golvMin -> SaldoStatus.UNDER_GOLVET
        else -> SaldoStatus.INOM
    }

fun minuterTillGrans(saldoMin: Int, takMin: Int?, golvMin: Int?): Int? =
    when {
        saldoMin >= 0 && takMin != null -> takMin - saldoMin
        saldoMin < 0 && golvMin != null -> saldoMin - golvMin
        else -> null
    }
