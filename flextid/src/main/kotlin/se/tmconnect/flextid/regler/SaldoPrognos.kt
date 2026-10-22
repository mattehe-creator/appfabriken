package se.tmconnect.flextid.regler

import java.time.LocalDate

fun saldoVidArssluten(saldoNuMin: Int, utfallIArMin: Int, idag: LocalDate): Int? {
    val dagarHittills = idag.dayOfYear
    val dagarKvar = 365 - dagarHittills
    
    // Färre än 30 dagar hittills → null (för lite underlag)
    if (dagarHittills < 30) {
        return null
    }
    
    // Beräkna prognos: saldoNuMin + utfallIArMin * dagarKvar / dagarHittills
    val täljare = (saldoNuMin.toLong() * 1000) + (utfallIArMin.toLong() * dagarKvar * 1000)
    val nämnare = dagarHittills * 1000
    
    // Avrundning: för täljare t och nämnare n > 0:
    // if (t >= 0) (2*t + n) / (2*n) else -((-2*t + n) / (2*n))
    val avrundat = if (täljare >= 0) {
        (2 * täljare + nämnare) / (2 * nämnare)
    } else {
        -( (-2 * täljare + nämnare) / (2 * nämnare) )
    }
    
    return avrundat.toInt()
}
