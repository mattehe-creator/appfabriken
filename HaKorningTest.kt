package se.tmconnect.rostfangst.ha

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
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(emptyList(), klient, konfig, forslag, "original")
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testHandelseOchTodo() {
        var handelseAnropad = false
        var todoAnropad = false
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                handelseAnropad = true
                return HaUtfall.Ok("OK")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                todoAnropad = true
                return HaUtfall.Ok("OK")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
        assert(handelseAnropad)
        assert(todoAnropad)
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testTodoTomEntity() {
        var handelseAnropad = false
        
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                handelseAnropad = true
                return HaUtfall.Ok("OK")
            }
            
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
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
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.ASSIST), klient, konfig, forslag, "original")
        assert(resultat.svar == "Lampan är tänd")
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testAssistOkTomtSvar() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            
            override fun assist(text: String): HaUtfall {
                return HaUtfall.Ok("{}")
            }
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.ASSIST), klient, konfig, forslag, "original")
        assert(resultat.svar == "Klart.")
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testFel() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
        assert(resultat.svar == null)
        assert(resultat.fel.size == 1)
        assert(resultat.fel[0] == "Når inte Home Assistant just nu.")
    }
    
    @Test
    fun testDubbletter() {
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                return HaUtfall.Fel("Når inte Home Assistant just nu.")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
        assert(resultat.svar == null)
        assert(resultat.fel.size == 1) // Dubbletter ska tas bort
        assert(resultat.fel[0] == "Når inte Home Assistant just nu.")
    }
}
