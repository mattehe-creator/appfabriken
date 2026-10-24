package se.tmconnect.husloggen.export

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AvlasningCsvFilnamnTest {
    private val idag = LocalDate.of(2026, 10, 24)

    @Test
    fun varme() {
        assertEquals("husloggen-varme-2026-10-24.csv", avlasningCsvFilnamn("Värme", idag))
    }

    @Test
    fun fjarrvarmeOst() {
        assertEquals(
            "husloggen-fjarrvarme-ost-2026-10-24.csv",
            avlasningCsvFilnamn("Fjärrvärme Öst", idag),
        )
    }

    @Test
    fun angpanna() {
        assertEquals("husloggen-angpanna-2026-10-24.csv", avlasningCsvFilnamn("Ångpanna", idag))
    }

    @Test
    fun elOchVatten() {
        assertEquals("husloggen-el-vatten-2026-10-24.csv", avlasningCsvFilnamn("El & vatten", idag))
    }

    @Test
    fun el() {
        assertEquals("husloggen-el-2026-10-24.csv", avlasningCsvFilnamn("El", idag))
    }

    @Test
    fun baraSkiljeteckenBlirMatare() {
        assertEquals("husloggen-matare-2026-10-24.csv", avlasningCsvFilnamn("!!!", idag))
    }
}
