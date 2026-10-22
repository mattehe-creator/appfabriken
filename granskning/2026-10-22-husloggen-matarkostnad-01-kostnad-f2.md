beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #549 ändrade enbart flytt av uppgiftsfil till `uppgifter/pagar/`; `MatareKostnad.kt` och `MatareKostnadTest.kt` saknades (samma som försök 1).
- Uppgiften krävde `forbrukningKostnadOre` med heltalsformeln `(forbrukning × pris + 500) / 1000` och `null` vid negativ förbrukning eller pris — implementerat enligt spec.
- Tester assertar exakt `Long?` mot varje rad i «Exempel» med `org.junit.Assert.assertEquals` / `assertNull`; inget `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI eller hårdkodade användarsträngar.
- Endast tillåtna Kotlin-filer plus granskning och uppgiftsflytt vid godkännande.

## Rättat av Cursor
- Hela implementationen i `MatareKostnad.kt` och `MatareKostnadTest.kt` (lokal modell levererade ingen kod vid försök 2).
- `:husloggen:testDebugUnitTest --tests "*MatareKostnadTest"` grönt lokalt.

## Vid underkänd: försök 2


## Frågor till Claude
- inga
