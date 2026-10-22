beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #542 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/` — samma fel som försök 1: varken `ArsOversiktCsv.kt` eller `ArsOversiktCsvTest.kt` skapades.
- Uppgiften (försök 2) krävde `arsOversiktCsv`, semikolon-CSV med avslutande `\n`, validering av rubriker/manadsnamn och minst tre JUnit 4-tester mot exemplen — saknades helt.
- Endast tillåtna filer tillagda av Cursor: `ArsOversiktCsv.kt` och `ArsOversiktCsvTest.kt` (rätt sökvägar under `flextid/`).
- `ArsOversikt.kt` från `main` finns på grenen enbart så att `ManadsRad` kompilerar (beror-på uppfyllt); inga andra filer utanför uppgiften ändrades av den lokala modellen.
- Implementationen följer uppgiften: rubrikrad, datarader med `manadsnamn[manad - 1]` och `formateraMinuterSomTid` / `formateraSaldoMinuter`, `IllegalArgumentException` vid fel listlängd.
- Tester använder `org.junit.Test` och `assertEquals` mot hela CSV-strängen inklusive avslutande `\n`, samt `@Test(expected = IllegalArgumentException::class)` för rubriker och månadsnamn.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar (testdata i testfilen).
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- Hel implementation i `ArsOversiktCsv.kt` och `ArsOversiktCsvTest.kt` (lokal modell levererade ingen kod igen, försök 2 enligt uppgifter/MALL.md).
- Hämtade `ArsOversikt.kt` från `origin/main` på grenen så att `:flextid` kompilerar.
- Justerade förväntad CSV-sträng i testet (extra radbrytning från `trimIndent`).

## Vid underkänd: försök 2


## Frågor till Claude
- inga
