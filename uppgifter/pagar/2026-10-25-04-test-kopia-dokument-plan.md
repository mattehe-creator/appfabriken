# Uppgift: Test av säkerhetskopians omvandling för dokument och planerade kostnader

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/test/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaDokumentPlanTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/export/SakerhetskopiaModell.kt (raderna med `dokumentTillSakerhetskopia` och nedåt)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/Dokument.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/PlaneradKostnad.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertNull`) i paketet `se.tmconnect.husloggen.export` för:

- `dokumentTillSakerhetskopia(dokument: Dokument, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaDokument`
- `sakerhetskopiaDokumentTillDokument(export: SakerhetskopiaDokument, filnamn: String, bostadId: Long): Dokument`
- `planeradKostnadTillSakerhetskopia(planeradKostnad: PlaneradKostnad, bostadIdTillIndex: Map<Long, Int>): SakerhetskopiaPlaneradKostnad`
- `sakerhetskopiaPlaneradKostnadTillPlaneradKostnad(export: SakerhetskopiaPlaneradKostnad, bostadId: Long): PlaneradKostnad`

Importera `se.tmconnect.husloggen.data.Dokument`, `se.tmconnect.husloggen.data.DokumentKategori`, `se.tmconnect.husloggen.data.PlaneradKostnad` och `java.time.LocalDate`. Alla fyra typerna är dataklasser utan tidsstämpel, så hela objekt kan jämföras med `assertEquals`.

## Exempel
- `dokumentTillSakerhetskopia(Dokument(id = 4, bostadId = 2, titel = "Ritning", kategori = DokumentKategori.RITNING, anteckning = "Plan 1", filnamn = "abc.pdf", mimeTyp = "application/pdf", tillagd = LocalDate.of(2026, 5, 1)), mapOf(2L to 1))` → `SakerhetskopiaDokument(titel = "Ritning", kategori = "RITNING", anteckning = "Plan 1", filnamn = "abc.pdf", mimeTyp = "application/pdf", tillagd = LocalDate.of(2026, 5, 1), bostadIndex = 1)`
- Samma dokument men `kategori = null` → resultatets `kategori` är `null`
- `sakerhetskopiaDokumentTillDokument(SakerhetskopiaDokument(titel = " Avtal el ", kategori = "AVTAL", anteckning = " ", filnamn = "gammal.pdf", mimeTyp = "application/pdf", tillagd = LocalDate.of(2025, 12, 31)), filnamn = "ny.pdf", bostadId = 3)` → `Dokument(id = 0, bostadId = 3, titel = "Avtal el", kategori = DokumentKategori.AVTAL, anteckning = null, filnamn = "ny.pdf", mimeTyp = "application/pdf", tillagd = LocalDate.of(2025, 12, 31))` (filnamnet från argumentet används, inte det i kopian)
- Samma som ovan men `kategori = "OKAND"` → resultatets `kategori` är `null`
- Samma som ovan men `kategori = "avtal"` (små bokstäver) → resultatets `kategori` är `null`
- `planeradKostnadTillSakerhetskopia(PlaneradKostnad(id = 9, bostadId = 1, titel = "Nytt tak", beloppOre = 25_000_000, planeratAr = 2030), mapOf(1L to 0))` → `SakerhetskopiaPlaneradKostnad(titel = "Nytt tak", beloppOre = 25_000_000, planeratAr = 2030, anteckning = null, bostadIndex = 0)`
- `sakerhetskopiaPlaneradKostnadTillPlaneradKostnad(SakerhetskopiaPlaneradKostnad(titel = " Fönster ", beloppOre = 8_000_000, planeratAr = 2028, anteckning = " byt alla ", bostadIndex = 0), bostadId = 5)` → `PlaneradKostnad(id = 0, bostadId = 5, titel = "Fönster", beloppOre = 8_000_000, planeratAr = 2028, anteckning = "byt alla")`
- Samma som ovan men `anteckning = ""` → resultatets `anteckning` är `null`

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SakerhetskopiaDokumentPlanTest"

## Gör inte
- Ändra andra filer.
- Ändra `SakerhetskopiaModell.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till beroenden.
