package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class HaTodoTest {
    @Test
    fun giltigInmatning() {
        val json = byggTodoJson("todo.att_gora", "Mjölk")
        assertEquals("{\"entity_id\":\"todo.att_gora\",\"item\":\"Mjölk\"}", json)
    }

    @Test
    fun tomEntityId() {
        val json = byggTodoJson("", "Mjölk")
        assertEquals(null, json)
    }

    @Test
    fun tomTitel() {
        val json = byggTodoJson("todo.x", "  ")
        assertEquals(null, json)
    }

    @Test
    fun mellanslagIInmatning() {
        val json = byggTodoJson(" todo.x ", " Köp \"mjölk\" ")
        assertEquals("{\"entity_id\":\"todo.x\",\"item\":\"Köp \\\"mjölk\\\"\"}", json)
    }
}
