package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertEquals
import org.junit.Test

class HittaByggnadensVardeTest {
    
    @Test
    fun parseBeloppAvgransare() {
        // Testa olika typer av tusentalsavgränsare
        assertEquals(12345L, parseBelopp("12\u00A0345", iTusentals = false))
        assertEquals(12345678L, parseBelopp("12.345.678", iTusentals = false))
        assertEquals(1234500L, parseBelopp("1.234,5", iTusentals = true))
        assertEquals(45000000L, parseBelopp("45 000", iTusentals = true))
        
        // Befintliga testfall ska fortfarande fungera
        assertEquals(12345L, parseBelopp("12 345", iTusentals = false))
        assertEquals(12345000L, parseBelopp("12 345", iTusentals = true))
        assertEquals(12L, parseBelopp("12,3", iTusentals = false))
        assertEquals(12L, parseBelopp("12,345", iTusentals = false))
        assertEquals(12345L, parseBelopp("12,345", iTusentals = true))
    }
    
    @Test
    fun hittaByggnadensVardeTvaKolumner() {
        // Testa två kolumner med årtal
        val text1 = "Byggnader och mark\n2024 2023\n12 500 000 11 800 000"
        assertEquals(Belopp(12500000, false), hittaByggnadensVarde(text1))
        
        // Testa att årtalsrad inte tolkas som belopp
        val text2 = "Byggnader och mark 2024 2023"
        assertEquals(null, hittaByggnadensVarde(text2))
        
        // Testa en kolumn (oförändrat beteende)
        val text3 = "Byggnader 10 000 000"
        assertEquals(Belopp(10000000, false), hittaByggnadensVarde(text3))
        
        // Testa med tkr enhet
        val text4 = "Byggnader och mark\n2024 2023\n12 500 000 tkr"
        assertEquals(Belopp(12500000000L, true), hittaByggnadensVarde(text4))
    }
}
