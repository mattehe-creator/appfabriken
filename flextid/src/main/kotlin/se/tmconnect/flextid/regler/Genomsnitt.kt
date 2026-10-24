package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag
import se.tmconnect.flextid.data.DagTyp

fun genomsnittArbetadMin(dagar: List<Dag>, fran: LocalDate, till: LocalDate): Int? {
    val filtreradeDagar = dagar.filter { 
        it.datum >= fran && it.datum <= till && it.typ == DagTyp.ARBETE 
            && it.start != null && it.slut != null
    }
    
    if (filtreradeDagar.isEmpty()) return null
    
    val totalMinuter = filtreradeDagar.sumOf { 
        arbetadeMinuter(it.start, it.slut, it.rastMin) 
    }
    
    val n = filtreradeDagar.size
    // Avrunda halvt uppåt: (summa * 2 + n) / (2 * n)
    return (totalMinuter * 2 + n) / (2 * n)
}

fun genomsnittStart(dagar: List<Dag>, fran: LocalDate, till: LocalDate): LocalTime? {
    val filtreradeDagar = dagar.filter { 
        it.datum >= fran && it.datum <= till && it.typ == DagTyp.ARBETE && it.start != null
    }
    
    if (filtreradeDagar.isEmpty()) return null
    
    val totalMinuterEfterMidnatt = filtreradeDagar.sumOf { 
        it.start!!.hour * 60 + it.start!!.minute 
    }
    
    val n = filtreradeDagar.size
    // Avrunda halvt uppåt: (summa * 2 + n) / (2 * n)
    val genomsnittMinuter = (totalMinuterEfterMidnatt * 2 + n) / (2 * n)
    
    val timmar = genomsnittMinuter / 60
    val minuter = genomsnittMinuter % 60
    
    return LocalTime.of(timmar, minuter)
}
