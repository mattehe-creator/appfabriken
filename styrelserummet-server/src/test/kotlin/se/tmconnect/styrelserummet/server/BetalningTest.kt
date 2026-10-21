package se.tmconnect.styrelserummet.server

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BetalningTest {
    private val aktiv = BetalKonfig(stripeNyckel = "sk_test_x", basUrl = "https://exempel.se")

    private class FalskHttp(
        val postSvar: HttpSvarEnkelt = HttpSvarEnkelt(200, """{"id":"cs_test_abcdefgh123","url":"https://checkout.stripe.com/c/pay/cs_test_abcdefgh123"}"""),
        val getSvar: HttpSvarEnkelt = HttpSvarEnkelt(200, """{"id":"cs_test_abcdefgh123","client_reference_id":"JOBB","payment_status":"paid"}"""),
    ) : StripeHttp {
        val anrop = mutableListOf<String>()
        var senasteFormular = ""
        override fun post(url: String, nyckel: String, formular: String): HttpSvarEnkelt {
            anrop += "POST $url"
            senasteFormular = formular
            return postSvar
        }
        override fun get(url: String, nyckel: String): HttpSvarEnkelt {
            anrop += "GET $url"
            return getSvar
        }
    }

    @Test
    fun konfigFranMiljo() {
        assertFalse(betalKonfigFranMiljo(emptyMap()).aktiv)
        assertFalse(betalKonfigFranMiljo(mapOf("STRIPE_SECRET_KEY" to "sk")).aktiv)
        assertFalse(betalKonfigFranMiljo(mapOf("STRIPE_SECRET_KEY" to "sk", "STYRELSERUMMET_BAS_URL" to "http://osaker")).aktiv)
        val k = betalKonfigFranMiljo(mapOf("STRIPE_SECRET_KEY" to "sk", "STYRELSERUMMET_BAS_URL" to "https://a.se/"))
        assertTrue(k.aktiv)
        assertEquals("https://a.se", k.basUrl)
        assertEquals(99_000L, k.prisOre)
        assertEquals(49_000L, betalKonfigFranMiljo(mapOf("STYRELSERUMMET_PRIS_ORE" to "49000")).prisOre)
        assertEquals(99_000L, betalKonfigFranMiljo(mapOf("STYRELSERUMMET_PRIS_ORE" to "-5")).prisOre)
    }

    @Test
    fun lamnasUtBaraNarBetaltEllerAvstangt() {
        val meta = JobbMeta("id", "brf", 2025, "KLAR", 0, 0, "h")
        assertTrue(farLamnaUt(BetalKonfig(null, null), meta))
        assertFalse(farLamnaUt(aktiv, meta))
        assertTrue(farLamnaUt(aktiv, meta.copy(betald = true)))
        assertEquals(BetalStatus(aktiv = true, betald = false, prisKr = 990), betalStatus(aktiv, meta))
    }

    @Test
    fun kassaFormularetInnehallerPrisOchAterhopp() {
        val f = StripeBetalare(aktiv, FalskHttp()).kassaFormular("JOBB")
        assertTrue(f.contains("mode=payment"))
        assertTrue(f.contains("line_items%5B0%5D%5Bprice_data%5D%5Bunit_amount%5D=99000"))
        assertTrue(f.contains("line_items%5B0%5D%5Bprice_data%5D%5Bcurrency%5D=sek"))
        assertTrue(f.contains("client_reference_id=JOBB"))
        assertTrue(f.contains("success_url=https%3A%2F%2Fexempel.se%2Fj%2FJOBB%3Fkassa%3D{CHECKOUT_SESSION_ID}"))
        assertTrue(f.contains("invoice_creation%5Benabled%5D=true"))
    }

    @Test
    fun skapaKassa() {
        val http = FalskHttp()
        val kassa = StripeBetalare(aktiv, http).skapaKassa("JOBB")
        assertEquals("cs_test_abcdefgh123", kassa?.id)
        assertEquals(listOf("POST https://api.stripe.com/v1/checkout/sessions"), http.anrop)
        assertNull(StripeBetalare(aktiv, FalskHttp(postSvar = HttpSvarEnkelt(400, "{}"))).skapaKassa("JOBB"))
        assertNull(StripeBetalare(BetalKonfig(null, null), FalskHttp()).skapaKassa("JOBB"))
    }

    @Test
    fun kontrolleraKraverRattJobbOchBetalt() {
        assertEquals(KassaStatus.Betald, StripeBetalare(aktiv, FalskHttp()).kontrollera("cs_test_abcdefgh123", "JOBB"))
        assertEquals(KassaStatus.Fel, StripeBetalare(aktiv, FalskHttp()).kontrollera("cs_test_abcdefgh123", "ANNAT"))
        val obetald = FalskHttp(getSvar = HttpSvarEnkelt(200, """{"client_reference_id":"JOBB","payment_status":"unpaid"}"""))
        assertEquals(KassaStatus.EjBetald, StripeBetalare(aktiv, obetald).kontrollera("cs_test_abcdefgh123", "JOBB"))
        assertEquals(KassaStatus.Fel, StripeBetalare(aktiv, FalskHttp()).kontrollera("ogiltigt/../id", "JOBB"))
    }
}
