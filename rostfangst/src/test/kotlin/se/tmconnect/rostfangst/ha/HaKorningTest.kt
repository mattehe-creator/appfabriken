package se.tmconnect.rostfangst.ha

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp

class HaKorningTest {
    
    @Test
    fun testTomLista() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(emptyList(), klient, HaKonfig("http://localhost", "token", "todo.x"), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertNull(resultat.svar)
        assertTrue(resultat.fel.isEmpty())
    }
    
    @Test
    fun testHandelseOchTodo() {
        var antalHandelse = 0
        var antalTodo = 0
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                antalHandelse++
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                antalTodo++
                return HaUtfall.Ok("ok")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, 
            HaKonfig("http://localhost", "token", "todo.x"), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertEquals(1, antalHandelse)
        assertEquals(1, antalTodo)
        assertNull(resultat.svar)
        assertTrue(resultat.fel.isEmpty())
    }
    
    @Test
    fun testAssistOk() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            
            override fun assist(text: String): HaUtfall {
                return HaUtfall.Ok("{\"response\":{\"speech\":{\"plain\":{\"speech\":\"Lampan är tänd\"}}}}")
            }
        }
        
        val resultat = korHaSteg(listOf(HaSteg.ASSIST), klient, 
            HaKonfig("http://localhost", "token", "todo.x"), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertEquals("Lampan är tänd", resultat.svar)
        assertTrue(resultat.fel.isEmpty())
    }
    
    @Test
    fun testAssistTomtSvar() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            
            override fun assist(text: String): HaUtfall {
                return HaUtfall.Ok("{}")
            }
        }
        
        val resultat = korHaSteg(listOf(HaSteg.ASSIST), klient, 
            HaKonfig("http://localhost", "token", "todo.x"), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertEquals("Klart.", resultat.svar)
        assertTrue(resultat.fel.isEmpty())
    }
    
    @Test
    fun testFel() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE), klient, 
            HaKonfig("http://localhost", "token", "todo.x"), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertNull(resultat.svar)
        assertEquals(1, resultat.fel.size)
        assertEquals("Når inte Home Assistant just nu.", resultat.fel[0])
    }
    
    @Test
    fun testTodoTomEntity() {
        var antalHandelse = 0
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                antalHandelse++
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, 
            HaKonfig("http://localhost", "token", ""), 
            Forslag(ForslagTyp.ATT_GORA, "test"), "original")
        
        assertEquals(1, antalHandelse)
        assertNull(resultat.svar)
        assertTrue(resultat.fel.isEmpty())
    }
}
