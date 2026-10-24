package se.tmconnect.styrelserummet.server

import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.io.path.writeText
import org.junit.Assert.assertEquals
import org.junit.Test

class RensningTest {
    private val dag = 24L * 60 * 60 * 1000

    private fun meta(id: String, aktivMs: Long) =
        JobbMeta(
            id = id,
            forening = "Brf Test",
            arsslut = 2025,
            status = JobbStatus.KLAR.name,
            skapadMs = aktivMs,
            senastAktivMs = aktivMs,
            klientIpHash = "x",
        )

    @Test
    fun gamlaJobbRaderasNyaBehalls() {
        val lagring = JobbLagring(createTempDirectory("rensning"))
        val nu = System.currentTimeMillis()
        lagring.skapa(meta("gammal", nu - 31 * dag))
        lagring.skapa(meta("ny", nu - 29 * dag))
        rensaGamlaJobb(lagring, nu)
        assertEquals(listOf("ny"), lagring.allaJobb())
    }

    @Test
    fun trasigtJobbRaderasEfterEttDygn() {
        val lagring = JobbLagring(createTempDirectory("rensning"))
        lagring.skapa(meta("trasig", System.currentTimeMillis()))
        (lagring.jobbKatalog("trasig") / "meta.json").writeText("{")
        rensaGamlaJobb(lagring, System.currentTimeMillis())
        assertEquals(1, lagring.allaJobb().size)
        rensaGamlaJobb(lagring, System.currentTimeMillis() + 2 * dag)
        assertEquals(0, lagring.allaJobb().size)
    }
}
