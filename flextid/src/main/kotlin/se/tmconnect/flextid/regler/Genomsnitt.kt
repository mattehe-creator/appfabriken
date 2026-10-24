package se.tmconnect.flextid.regler

import java.time.LocalDate
import java.time.LocalTime
import se.tmconnect.flextid.data.Dag

fun genomsnittArbetadMin(dagar: List<Dag>, fran: LocalDate, till: LocalDate): Int? {
    val filtreradeDagar = dagar.filter { dag ->
        dag.typ == DagTyp.ARBETE && 
        dag.datum >= fran && 
        dag.datum <= till &&
        dag.start != null && 
        dag.slut != null
    }
    
    if (filtreradeDagar.isEmpty()) {
        return null
    }
    
    val totalMinuter = filtreradeDagar.sumOf { dag ->
        arbetadeMinuter(dag.start, dag.slut, dag.rastMin)
    }
    
    val n = filtreradeDagar.size
    // Avrunda halvt uppåt: (summa * 2 + n) / (2 * n)
    return (totalMinuter * 2 + n) / (2 * n)
}

fun genomsnittStart(dagar: List<Dag>, fran: LocalDate, till: LocalDate): LocalTime? {
    val filtreradeDagar = dagar.filter { dag ->
        dag.typ == DagTyp.ARBETE && 
        dag.datum >= fran && 
        dag.datum <= till &&
        dag.start != null
    }
    
    if (filtreradeDagar.isEmpty()) {
        return null
    }
    
    val totalMinuterEfterMidnatt = filtreradeDagar.sumOf { dag ->
        val start = dag.start!!
        start.hour * 60 + start.minute
    }
    
    val n = filtreradeDagar.size
    // Avrunda halvt uppåt: (summa * 2 + n) / (2 * n)
    val genomsnittsMinuter = (totalMinuterEfterMidnatt * 2 + n) / (2 * n)
    
    val timmar = genomsnittsMinuter / 60
    val minuter = genomsnittsMinuter % 60
    
    return LocalTime.of(timmar, minuter)
}
