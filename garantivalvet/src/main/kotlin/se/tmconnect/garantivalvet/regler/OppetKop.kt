package se.tmconnect.garantivalvet.regler

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Öppet köp är butikens frivilliga erbjudande, inte lag. Appen räknar bara när kvittot säger det
 * eller användaren själv har angett antalet dagar, och påstår inget om en butiks villkor.
 */
enum class OppetKopStatus {
    GALLER,
    SLUTAR_SNART,
    UTGANGEN,
}

const val OPPET_KOP_SNART_DAGAR = 3
const val OPPET_KOP_MAX_DAGAR = 365

/** Köpdagen är dag 0 och slutdagen är sista dagen. */
fun oppetKopSlut(kopdatum: LocalDate, dagar: Int): LocalDate = kopdatum.plusDays(dagar.toLong())

fun oppetKopStatus(kopdatum: LocalDate, dagar: Int, idag: LocalDate): OppetKopStatus {
    val slut = oppetKopSlut(kopdatum, dagar)
    if (idag.isAfter(slut)) {
        return OppetKopStatus.UTGANGEN
    }
    val dagarKvar = ChronoUnit.DAYS.between(idag, slut).toInt()
    return if (dagarKvar <= OPPET_KOP_SNART_DAGAR) OppetKopStatus.SLUTAR_SNART else OppetKopStatus.GALLER
}

/** Antal dagar som kan sparas: 1 till [OPPET_KOP_MAX_DAGAR]. Allt annat betyder inget öppet köp. */
fun giltigaOppetKopDagar(dagar: Int?): Int? = dagar?.takeIf { it in 1..OPPET_KOP_MAX_DAGAR }

enum class Varutyp {
    ELEKTRONIK,
    KLADER_SKOR,
    HEM_BYGG,
    VAXTER,
    MAT_DRYCK,
    OVRIGT,
    ;

    companion object {
        fun franNamn(namn: String?): Varutyp? = entries.firstOrNull { it.name == namn }
    }
}
