package se.tmconnect.husloggen.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import se.tmconnect.husloggen.data.PostTyp
import java.time.LocalDate

class SakerhetskopiaJsonTest {
    @Test
    fun rundturSkrivLas() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16T10:00:00Z",
                uppgifter =
                    listOf(
                        SakerhetskopiaUppgift(
                            namn = "Rensa hängrännor",
                            intervallManader = 12,
                            startDatum = LocalDate.of(2025, 4, 1),
                            senastGjord = LocalDate.of(2026, 4, 1),
                        ),
                    ),
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2026, 4, 1),
                            vad = "Rensat",
                            typ = PostTyp.UNDERHALL.name,
                            uppgiftIndex = 0,
                            bilder = listOf("foto.jpg"),
                        ),
                    ),
            )
        val json = skrivSakerhetskopiaJson(innehall)
        val las = lasSakerhetskopiaJson(json)
        assertTrue(las is SakerhetskopiaJsonLasResult.Ok)
        assertEquals(innehall, (las as SakerhetskopiaJsonLasResult.Ok).innehall)
    }

    @Test
    fun postUtanValfriaFalt() {
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16",
                uppgifter = emptyList(),
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2026, 1, 1),
                            vad = "Minimalt",
                            typ = PostTyp.UNDERHALL.name,
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall)) as SakerhetskopiaJsonLasResult.Ok
        val post = las.innehall.poster.single()
        assertEquals(null, post.beloppOre)
        assertEquals(null, post.utforare)
        assertEquals(null, post.uppgiftIndex)
        assertEquals(emptyList<String>(), post.bilder)
        assertEquals(null, post.rotRutAvdragOre)
    }

    @Test
    fun importUtanRotRutAvdragFalt() {
        val json =
            """
            {"version":1,"skapad":"2026-10-16","uppgifter":[],"poster":[{"datum":"2026-01-01","vad":"Gammal","typ":"UNDERHALL","beloppOre":100000}]}
            """.trimIndent()
        val las = lasSakerhetskopiaJson(json) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(null, las.innehall.poster.single().rotRutAvdragOre)
    }

    @Test
    fun specialteckenOchEmoji() {
        val text = "Citat \" och \\ backslash\nrad\nåäö 🏠"
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16",
                uppgifter = emptyList(),
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2026, 6, 1),
                            vad = text,
                            typ = PostTyp.REPARATION.name,
                            anteckning = text,
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall)) as SakerhetskopiaJsonLasResult.Ok
        val post = las.innehall.poster.single()
        assertEquals(text, post.vad)
        assertEquals(text, post.anteckning)
    }

    @Test
    fun version2Avvisas() {
        val json = """{"version":2,"skapad":"2026-10-16","uppgifter":[],"poster":[]}"""
        assertTrue(lasSakerhetskopiaJson(json) is SakerhetskopiaJsonLasResult.Fel.OkandVersion)
    }

    @Test
    fun trasigJsonAvvisas() {
        assertTrue(lasSakerhetskopiaJson("{") is SakerhetskopiaJsonLasResult.Fel.TrasigJson)
    }

    @Test
    fun dubblettfunktion() {
        val export =
            SakerhetskopiaPost(
                datum = LocalDate.of(2024, 1, 1),
                vad = "Samma",
                typ = PostTyp.UNDERHALL.name,
                beloppOre = 100L,
            )
        val lik =
            SakerhetskopiaPost(
                datum = LocalDate.of(2024, 1, 1),
                vad = "Samma",
                typ = PostTyp.UNDERHALL.name,
                beloppOre = 100L,
            )
        val olik = lik.copy(vad = "Annan")
        assertTrue(arSakerhetskopiaPostNyckelLik(sakerhetskopiaPostNyckel(export, emptyList()), sakerhetskopiaPostNyckel(lik, emptyList())))
        assertFalse(arSakerhetskopiaPostNyckelLik(sakerhetskopiaPostNyckel(export, emptyList()), sakerhetskopiaPostNyckel(olik, emptyList())))
    }

    @Test
    fun uppgiftKopplingBevaras() {
        val uppgifter =
            listOf(
                SakerhetskopiaUppgift(
                    namn = "Service",
                    intervallManader = 24,
                    startDatum = LocalDate.of(2024, 1, 1),
                ),
            )
        val innehall =
            SakerhetskopiaInnehall(
                version = 1,
                skapad = "2026-10-16",
                uppgifter = uppgifter,
                poster =
                    listOf(
                        SakerhetskopiaPost(
                            datum = LocalDate.of(2026, 1, 1),
                            vad = "Gjort",
                            typ = PostTyp.UNDERHALL.name,
                            uppgiftIndex = 0,
                        ),
                    ),
            )
        val las = lasSakerhetskopiaJson(skrivSakerhetskopiaJson(innehall)) as SakerhetskopiaJsonLasResult.Ok
        assertEquals(0, las.innehall.poster.single().uppgiftIndex)
    }
}
