package se.tmconnect.karna.tema

import org.junit.Assert.assertNotEquals
import org.junit.Test

class FargschemaTest {
    /** Material 3 standard tertiary (lila) — ska inte förekomma i appfabrik-temat. */
    private val materialDefaultTertiary = 0xFF6650A4

    @Test
    fun ljusSchemaHarIngenStandardlila() {
        val schema = appfabrikLjusFargschema()
        assertNotEquals(
            materialDefaultTertiary,
            schema.tertiary.value.toLong() and 0xFFFFFFFF,
        )
        assertNotEquals(
            materialDefaultTertiary,
            schema.primary.value.toLong() and 0xFFFFFFFF,
        )
    }

    @Test
    fun morkSchemaHarIngenStandardlila() {
        val schema = appfabrikMorkFargschema()
        assertNotEquals(
            materialDefaultTertiary,
            schema.tertiary.value.toLong() and 0xFFFFFFFF,
        )
    }
}
