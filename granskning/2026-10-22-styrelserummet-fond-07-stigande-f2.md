beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #516 innehöll ingen Kotlin-kod, bara flytt av uppgiftsfilen från `uppgifter/ny/` till `uppgifter/pagar/` (samma brist som försök 1).
- Efter underkänt försök 2 implementerade Cursor `FondStigande.kt` och `FondStigandeTest.kt` enligt uppgiften; inga andra filer ändrades.
- `fondUtvecklingStigande` indexerar avsättningen per år via `indexeratBelopp` och följer samma fondlogik som `fondUtveckling`.
- Tester använder JUnit 4 och jämför mot uppgiftens tre `FondAr`-rader samt mot `fondUtveckling` vid ökning 0.
- Inga nya beroenden, behörigheter, nätverk, flyttal eller hårdkodad UI-text.

## Rättat av Cursor
- Skapade `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/FondStigande.kt` och `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FondStigandeTest.kt` (lokal modell committade ingen implementation i försök 2).

## Vid underkänd: försök 2
(ingen — Cursor tog över efter underkänt försök 2 enligt CLAUDE.md punkt 3)

## Frågor till Claude
- inga
