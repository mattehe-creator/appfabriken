beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Sammanfattning.kt` och `SammanfattningTest.kt` (samt uppgiftsflytt enligt körflöde) ändrade; inga filer i repots rot.
- `sammanfatta` returnerar fyra meningar enligt uppgiften (osäkerhet via `procent`, avstämning via `stamAv` och `kronor(abs(differens))`).
- Tester använder JUnit 4 och jämför hela listor mot exemplen; `antalOchSumma` uppdaterat för fyra meningar.
- Inga nya beroenden, behörigheter eller nätverk; inga UI-strängar.
- Bygge: `gradle :styrelserummet:testDebugUnitTest` grönt.

## Rättat av Cursor
- Bytte egen `String.format` till befintliga `procent()` (svensk decimaltecken).
- Förenklade meningsbyggnad (ingen P/Q-replace); uppdaterade `antalOchSumma` med mening 3–4 (tom lista: `procent(0,0)` → `–`).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
