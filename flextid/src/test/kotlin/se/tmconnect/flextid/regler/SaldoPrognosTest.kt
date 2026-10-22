package se.tmconnect.flextid.regler

import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class SaldoPrognosTest {
    
    @Test
    fun `test saldo vid arssluten`() {
        // saldo 120, utfall 120, idag 2026-07-02 (dag 183, kvar 182) → 120 + 120×182/183 = 120 + 119,34 → 239
        assertEquals(
            239,
            saldoVidArssluten(120, 120, LocalDate.of(2026, 7, 2))
        )
        
        // saldo 0, utfall −90, idag 2026-03-31 (dag 90, kvar 275) → −275
        assertEquals(
            -275,
            saldoVidArssluten(0, -90, LocalDate.of(2026, 3, 31))
        )
        
        // saldo 50, utfall 0, idag 2026-12-31 → 50
        assertEquals(
            50,
            saldoVidArssluten(50, 0, LocalDate.of(2026, 12, 31))
        )
        
        // idag 2026-01-29 (dag 29) → null
        assertEquals(
            null,
            saldoVidArssluten(10, 3, LocalDate.of(2026, 1, 29))
        )
        
        // saldo 10, utfall 3, idag 2024-02-29 (dag 60, skottår, kvar 306) → 10 + 15,3 → 25
        assertEquals(
            25,
            saldoVidArssluten(10, 3, LocalDate.of(2024, 2, 29))
        )
    }
}
