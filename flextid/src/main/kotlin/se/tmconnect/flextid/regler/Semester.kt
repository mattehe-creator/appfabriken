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

fun raknaDagarAvTyp(
    dagar: List<se.tmconnect.flextid.data.Dag>,
    typ: DagTyp,
    fran: LocalDate,
    till: LocalDate,
    arbetsdagarMask: Int,
    aftnarLediga: Boolean = true
): Int {
    if (fran.isAfter(till)) return 0
    return dagar.count { d ->
        d.typ == typ && !d.datum.isBefore(fran) && !d.datum.isAfter(till) && arArbetsdagMedHelger(d.datum, arbetsdagarMask, aftnarLediga)
    }
}

data class SemesterSammanfattning(
    val ar: SemesterAr,
    val uttagna: Int,
    val planerade: Int,
    val kvar: Int,
    val sparade: Int = 0,
)

const val SEMESTERDAGAR_STANDARD = 25

fun semesterSammanfattning(
    dagar: List<se.tmconnect.flextid.data.Dag>,
    idag: LocalDate,
    arbetsdagarMask: Int,
    dagarPerAr: Int = SEMESTERDAGAR_STANDARD,
    startManad: Int = SEMESTERAR_STARTMANAD_STANDARD,
    aftnarLediga: Boolean = true,
    sparadeDagar: Int = 0,
): SemesterSammanfattning {
    val ar = semesterArFor(idag, startManad)
    val uttagna = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, ar.fran, idag, arbetsdagarMask, aftnarLediga)
    val planerade = raknaDagarAvTyp(dagar, DagTyp.SEMESTER, idag.plusDays(1), ar.till, arbetsdagarMask, aftnarLediga)
    val sparade = sparadeDagar.coerceAtLeast(0)
    return SemesterSammanfattning(ar, uttagna, planerade, dagarPerAr.coerceAtLeast(0) + sparade - uttagna - planerade, sparade)
}
