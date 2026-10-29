package se.tmconnect.rostfangst.ha

import org.junit.Test
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant

class HaKorningTest {
    
    @Test
    fun testTomLista() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(emptyList(), klient, HaKonfig("http://localhost", "token", "todo.entity"), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testHandelseOchTodoOk() {
        var handelseAnropad = false
        var todoAnropad = false
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                handelseAnropad = true
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                todoAnropad = true
                return HaUtfall.Ok("ok")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, 
            HaKonfig("http://localhost", "token", "todo.entity"), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(handelseAnropad)
        assert(todoAnropad)
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testTodoTomt() {
        var handelseAnropad = false
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                handelseAnropad = true
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, 
            HaKonfig("http://localhost", "token", ""), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(handelseAnropad)
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
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
            HaKonfig("http://localhost", "token", "todo.entity"), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(resultat.svar == "Lampan är tänd")
        assert(resultat.fel.isEmpty())
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
            HaKonfig("http://localhost", "token", "todo.entity"), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(resultat.svar == "Klart.")
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testBadaFel() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, 
            HaKonfig("http://localhost", "token", "todo.entity"), 
            Forslag(ForslagTyp.ATT_GORA, "Test"), "original")
        
        assert(resultat.svar == null)
        assert(resultat.fel.size == 1)
        assert(resultat.fel[0] == "Når inte Home Assistant just nu.")
    }
}
