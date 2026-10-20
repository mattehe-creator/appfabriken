package se.tmconnect.styrelserummet.underlag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.styrelserummet.modell.Forening
import se.tmconnect.styrelserummet.regler.Avstamning

class AnmarkningarTest {
    @Test
    fun anmarkningSummaProcent() {
        val over150 = anmarkningarFor(
            utkast(byggnadensVarde = 10_000_000, summa = 16_000_000),
            emptyList(),
        )
        assertTrue(
            over150.any {
                it.contains("överstiger 150 procent") &&
                    it.contains("16000000") &&
                    it.contains("10000000")
            },
        )
        assertTrue(over150.any { it.contains("differens") })

        val under50 = anmarkningarFor(
            utkast(byggnadensVarde = 10_000_000, summa = 4_000_000),
            emptyList(),
        )
        assertTrue(
            under50.any {
                it.contains("understiger 50 procent") &&
                    it.contains("4000000") &&
                    it.contains("10000000")
            },
        )

        val normal = anmarkningarFor(
            utkast(byggnadensVarde = 10_000_000, summa = 10_000_000, stammer = true),
            emptyList(),
        )
        assertFalse(normal.any { it.contains("överstiger 150 procent") })
        assertFalse(normal.any { it.contains("understiger 50 procent") })

        val exakt150 = anmarkningarFor(
            utkast(byggnadensVarde = 10_000_000, summa = 15_000_000),
            emptyList(),
        )
        assertFalse(exakt150.any { it.contains("överstiger 150 procent") })

        val exakt50 = anmarkningarFor(
            utkast(byggnadensVarde = 10_000_000, summa = 5_000_000),
            emptyList(),
        )
        assertFalse(exakt50.any { it.contains("understiger 50 procent") })

        val utanByggnad = anmarkningarFor(
            utkast(byggnadensVarde = null, summa = 16_000_000),
            emptyList(),
        )
        assertFalse(utanByggnad.any { it.contains("överstiger 150 procent") })
        assertFalse(utanByggnad.any { it.contains("understiger 50 procent") })
    }

    private fun utkast(
        byggnadensVarde: Long?,
        summa: Long,
        stammer: Boolean = false,
    ): Utkast {
        val differens = (byggnadensVarde ?: 0L) - summa
        return Utkast(
            forening = Forening("Testföreningen", 1975),
            komponenter = emptyList(),
            byggnadensVarde = byggnadensVarde,
            arsredovisning = ArsredovisningUtdrag(null, null, null, null),
            avstamning = Avstamning(summa = summa, differens = differens, stammer = stammer),
            anmarkningar = emptyList(),
        )
    }
}
