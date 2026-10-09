package se.tmconnect.garantivalvet.regler

import org.junit.Assert.assertEquals
import org.junit.Test

class KvittoSamplingTest {

    @Test
    fun test8000x6000() {
        assertEquals(4, beraknaInSampleSize(8000, 6000))
    }

    @Test
    fun test4000x3000() {
        assertEquals(2, beraknaInSampleSize(4000, 3000))
    }

    @Test
    fun test3999x3000() {
        assertEquals(1, beraknaInSampleSize(3999, 3000))
    }

    @Test
    fun test1200x800() {
        assertEquals(1, beraknaInSampleSize(1200, 800))
    }

    @Test
    fun test16000x9000() {
        assertEquals(8, beraknaInSampleSize(16000, 9000))
    }
}
