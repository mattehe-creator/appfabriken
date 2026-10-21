package se.tmconnect.flextid.regler

import java.time.LocalDate

data class Helgdag(val datum: LocalDate, val namn: String, val arAfton: Boolean = false)

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

fun rorligaHelgdagar(ar: Int): List<Helgdag> {
    val p = paskdagen(ar)
    return listOf(
        Helgdag(p.minusDays(2), "Långfredagen"),
        Helgdag(p, "Påskdagen"),
        Helgdag(p.plusDays(1), "Annandag påsk"),
        Helgdag(p.plusDays(39), "Kristi himmelsfärdsdag"),
        Helgdag(p.plusDays(49), "Pingstdagen"),
    )
}

fun forstaLordagenFran(datum: LocalDate): LocalDate =
    datum.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY))

fun svenskaHelgdagar(ar: Int): List<Helgdag> {
    val fasta = listOf(
        Helgdag(LocalDate.of(ar, 1, 1), "Nyårsdagen"),
        Helgdag(LocalDate.of(ar, 1, 6), "Trettondedag jul"),
        Helgdag(LocalDate.of(ar, 5, 1), "Första maj"),
        Helgdag(LocalDate.of(ar, 6, 6), "Sveriges nationaldag"),
        Helgdag(LocalDate.of(ar, 12, 25), "Juldagen"),
        Helgdag(LocalDate.of(ar, 12, 26), "Annandag jul"),
    )
    val beraknade = listOf(
        Helgdag(forstaLordagenFran(LocalDate.of(ar, 6, 20)), "Midsommardagen"),
        Helgdag(forstaLordagenFran(LocalDate.of(ar, 10, 31)), "Alla helgons dag"),
    )
    return (fasta + rorligaHelgdagar(ar) + beraknade).sortedBy { it.datum }
}

fun svenskaAftnar(ar: Int): List<Helgdag> =
    listOf(
        Helgdag(forstaLordagenFran(LocalDate.of(ar, 6, 20)).minusDays(1), "Midsommarafton", arAfton = true),
        Helgdag(LocalDate.of(ar, 12, 24), "Julafton", arAfton = true),
        Helgdag(LocalDate.of(ar, 12, 31), "Nyårsafton", arAfton = true),
    )

fun helgdagPa(datum: LocalDate, medAftnar: Boolean): Helgdag? =
    svenskaHelgdagar(datum.year).firstOrNull { it.datum == datum }
        ?: if (medAftnar) svenskaAftnar(datum.year).firstOrNull { it.datum == datum } else null

fun arRodDag(datum: LocalDate): Boolean = svenskaHelgdagar(datum.year).any { it.datum == datum }

fun arLedigDag(datum: LocalDate, raknaAftnarSomLediga: Boolean): Boolean =
    arRodDag(datum) || (raknaAftnarSomLediga && svenskaAftnar(datum.year).any { it.datum == datum })
