package se.tmconnect.styrelserummet.regler

import java.time.LocalDate

fun paskdagen(ar: Int): LocalDate {
    if (ar < 1900 || ar > 2200) {
        throw IllegalArgumentException("År måste vara mellan 1900 och 2200")
    }

    val a = ar % 19
    val b = ar / 100
    val c = ar % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val manad = (h + l - 7 * m + 114) / 31
    val dag = ((h + l - 7 * m + 114) % 31) + 1

    return LocalDate.of(ar, manad, dag)
}

fun forstaLordagenFran(datum: LocalDate): LocalDate =
    datum.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY))

fun arSvenskHelgdag(d: LocalDate): Boolean {
    val ar = d.year
    val fasta = listOf(
        LocalDate.of(ar, 1, 1), // Nyårsdagen
        LocalDate.of(ar, 1, 6), // Trettondedag jul
        LocalDate.of(ar, 5, 1), // Första maj
        LocalDate.of(ar, 6, 6), // Sveriges nationaldag
        LocalDate.of(ar, 12, 25), // Juldagen
        LocalDate.of(ar, 12, 26), // Annandag jul
    )
    
    val rorliga = listOf(
        paskdagen(ar).minusDays(2), // Långfredag
        paskdagen(ar), // Påskdagen
        paskdagen(ar).plusDays(1), // Annandag påsk
        paskdagen(ar).plusDays(39), // Kristi himmelsfärdsdag
        forstaLordagenFran(LocalDate.of(ar, 6, 20)).minusDays(1), // Midsommarafton
        forstaLordagenFran(LocalDate.of(ar, 6, 20)), // Midsommardagen
        forstaLordagenFran(LocalDate.of(ar, 10, 31)), // Alla helgons dag
    )
    
    return (fasta + rorliga).contains(d)
}

fun narmasteArbetsdag(d: LocalDate, riktning: Int): LocalDate {
    if (riktning != 1 && riktning != -1) {
        throw IllegalArgumentException("Riktning måste vara 1 (framåt) eller -1 (bakåt)")
    }
    
    var datum = d
    while (arSvenskHelgdag(datum) || datum.dayOfWeek.value > 5) { // > 5 means weekend (saturday=6, sunday=7)
        datum = datum.plusDays(riktning.toLong())
    }
    
    return datum
}
