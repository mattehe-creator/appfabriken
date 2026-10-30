package se.tmconnect.rostfangst.ha

import org.junit.Test
import se.tmconnect.rostfangst.regler.Forslag
import se.tmconnect.rostfangst.regler.ForslagTyp
import java.time.Instant

class HaKorningTest {
    
    @Test
    fun testTomStegslista() {
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
        val anropCount = mutableListOf<Int>()
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                anropCount.add(1)
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall {
                anropCount.add(2)
                return HaUtfall.Ok("ok")
            }
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "todo.x")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
        
        assert(anropCount.size == 2)
        assert(anropCount[0] == 1)
        assert(anropCount[1] == 2)
        assert(resultat.svar == null)
        assert(resultat.fel.isEmpty())
    }
    
    @Test
    fun testTodoTomEntity() {
        val anropCount = mutableListOf<Int>()
        val klient = object : HaKlient {
            override fun skickaHandelse(json: String): HaUtfall {
                anropCount.add(1)
                return HaUtfall.Ok("ok")
            }
            
            override fun laggTillTodo(json: String): HaUtfall = throw RuntimeException("Ska inte anropas")
            
            override fun assist(text: String): HaUtfall = throw RuntimeException("Ska inte anropas")
        }
        
        val konfig = HaKonfig("http://localhost:8123", "token", "")
        val forslag = Forslag(ForslagTyp.ATT_GORA, "Test")
        val resultat = korHaSteg(listOf(HaSteg.HANDELSE, HaSteg.TODO), klient, konfig, forslag, "original")
        
        assert(anropCount.size == 1)
        assert(anropCount[0] == 1)
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
        assert(resultat.fel.size == 1)
        assert(resultat.fel[0] == "Når inte Home Assistant just nu.")
    }
}
