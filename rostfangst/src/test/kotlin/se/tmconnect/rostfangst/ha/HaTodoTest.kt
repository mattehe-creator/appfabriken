package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HaTodoTest {
    @Test
    fun bygger_json_med_entity_och_item() {
        assertEquals(
            """{"entity_id":"todo.att_gora","item":"Mjölk"}""",
            byggTodoJson("todo.att_gora", "Mjölk")
        )
    }

    @Test
    fun tom_entity_eller_titel_ger_null() {
        assertNull(byggTodoJson("", "Mjölk"))
        assertNull(byggTodoJson("todo.x", "  "))
    }

    @Test
    fun trimmar_och_kodar_citattecken() {
        assertEquals(
            """{"entity_id":"todo.x","item":"Köp \"mjölk\""}""",
            byggTodoJson(" todo.x ", " Köp \"mjölk\" ")
        )
    }
}
