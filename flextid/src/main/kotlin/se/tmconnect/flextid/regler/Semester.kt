package se.tmconnect.flextid.regler

import java.time.LocalDate

data class SemesterAr(val fran: LocalDate, val till: LocalDate)

const val SEMESTERAR_STARTMANAD_STANDARD = 4

fun semesterArFor(datum: LocalDate, startManad: Int = SEMESTERAR_STARTMANAD_STANDARD): SemesterAr {
    if (startManad < 1 || startManad > 12) {
        throw IllegalArgumentException("startManad must be between 1 and 12")
    }
    
    val fran = if (datum.monthValue >= startManad) {
        LocalDate.of(datum.year, startManad, 1)
    } else {
        LocalDate.of(datum.year - 1, startManad, 1)
    }
    
    val till = fran.plusYears(1).minusDays(1)
    
    return SemesterAr(fran, till)
}
