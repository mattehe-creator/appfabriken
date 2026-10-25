# Uppgift: Test av sortering och gruppering av dokument

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/DokumentSorteringTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/DokumentSortering.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Dokument.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/DokumentKategori.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `org.junit.Assert.assertEquals`) för
`sorteraDokument(dokument: List<Dokument>): List<Dokument>` och
`grupperaDokumentPerKategori(dokument: List<Dokument>): List<DokumentGrupp>`.

Sorteringen är: först kategori i ordningen MANUAL, RITNING, BESIKTNING, GARANTI, AVTAL, OVRIGT, och dokument utan kategori (`null`) sist. Inom samma kategori efter titel utan hänsyn till stora och små bokstäver, och sist efter id.

Skapa dokument med en hjälpfunktion i testklassen:

```kotlin
private fun dok(id: Long, titel: String, kategori: DokumentKategori?) =
    Dokument(
        id = id,
        titel = titel,
        kategori = kategori,
        filnamn = "a.pdf",
        mimeTyp = "application/pdf",
        tillagd = LocalDate.of(2026, 1, 1),
    )
```

Dessa sex används i flera exempel:

```kotlin
val d1 = dok(1, "Ritning plan 1", DokumentKategori.RITNING)
val d2 = dok(2, "manual diskmaskin", DokumentKategori.MANUAL)
val d3 = dok(3, "Avtal el", null)
val d4 = dok(4, "Manual ugn", DokumentKategori.MANUAL)
val d5 = dok(5, "Besiktning 2020", DokumentKategori.BESIKTNING)
val d6 = dok(6, "Garanti tak", DokumentKategori.OVRIGT)
val sex = listOf(d1, d2, d3, d4, d5, d6)
```

Jämför id-listor, till exempel `assertEquals(listOf(2L, 4L, 1L, 5L, 6L, 3L), sorteraDokument(sex).map { it.id })`.

## Exempel
- `sorteraDokument(emptyList())` → tom lista
- `sorteraDokument(sex)` → id `[2, 4, 1, 5, 6, 3]`
- `sorteraDokument(listOf(d3, d6))` → id `[6, 3]` (utan kategori hamnar sist)
- `sorteraDokument(listOf(dok(10, "b", DokumentKategori.MANUAL), dok(11, "A", DokumentKategori.MANUAL)))` → id `[11, 10]` ("A" före "b" trots stor bokstav)
- `sorteraDokument(listOf(dok(8, "kvitto", DokumentKategori.MANUAL), dok(7, "Kvitto", DokumentKategori.MANUAL)))` → id `[7, 8]` (samma titel, lägst id först)
- `grupperaDokumentPerKategori(emptyList())` → tom lista
- `grupperaDokumentPerKategori(sex)` → 5 grupper. Kategorierna i ordning: `[MANUAL, RITNING, BESIKTNING, OVRIGT, null]`. Id per grupp: `[[2, 4], [1], [5], [6], [3]]`.
- `grupperaDokumentPerKategori(listOf(d3))` → 1 grupp med `kategori = null` och id `[3]`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*DokumentSorteringTest"

## Gör inte
- Ändra andra filer.
- Ändra `DokumentSortering.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Använda å, ä eller ö i titlarna (sorteringen av dem är inte det som testas här).
- Lägga till beroenden.

utfall: underkänd 2026-10-24 av Cursor (PR #669). Ersatt av 2026-10-25-02-test-dokument-sortering-f2.md
