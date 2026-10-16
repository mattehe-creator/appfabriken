package se.tmconnect.husloggen.regler

import org.junit.Test
import se.tmconnect.husloggen.data.Post
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class LoggSummaTest {
    
    @Test
    fun `summaPerAr med poster med och utan belopp`() {
        val post1 = Post(
            id = 1,
            datum = LocalDate.of(2025, 1, 1),
            vad = "Test 1",
            typ = PostTyp.UNDERHALL,
            beloppOre = 10000
        )
        
        val post2 = Post(
            id = 2,
            datum = LocalDate.of(2025, 1, 2),
            vad = "Test 2",
            typ = PostTyp.UNDERHALL,
            beloppOre = null
        )
        
        val post3 = Post(
            id = 3,
            datum = LocalDate.of(2024, 1, 1),
            vad = "Test 3",
            typ = PostTyp.UNDERHALL,
            beloppOre = 5000
        )
        
        val result = summaPerAr(listOf(post1, post2, post3))
        
        assert(result[2025] == 10000L)
        assert(result[2024] == 5000L)
        assert(result.size == 2)
    }
    
    @Test
    fun `summaPerAr med bara poster utan belopp`() {
        val post1 = Post(
            id = 1,
            datum = LocalDate.of(2025, 1, 1),
            vad = "Test 1",
            typ = PostTyp.UNDERHALL,
            beloppOre = null
        )
        
        val post2 = Post(
            id = 2,
            datum = LocalDate.of(2024, 1, 1),
            vad = "Test 2",
            typ = PostTyp.UNDERHALL,
            beloppOre = null
        )
        
        val result = summaPerAr(listOf(post1, post2))
        
        assert(result.isEmpty())
    }
    
    @Test
    fun `summaPerAr med tom lista`() {
        val result = summaPerAr(emptyList())
        
        assert(result.isEmpty())
    }
}
