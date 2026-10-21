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
