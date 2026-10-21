beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Granser.kt`, `GranserTest.kt` (ny) och flytt av uppgiftsfilen till `uppgifter/pagar/` — inga filer i fel sökväg.
- `MAX_TECKEN_TILL_TOLKNING = 400_000` och `skattaTecken` summerar `length` enligt uppgiften.
- Testerna matchar exemplen (tom lista → 0, tre strängar → 350, 200_000+200_001 → 400_001).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Ingen koppling till JobbKo.

## Rättat av Cursor
- `GranserTest`: bytte från JUnit Jupiter och `kotlin.test.assertEquals` till `org.junit.Test` och `org.junit.Assert.assertEquals` så att testerna hittas (`useJUnit()` i modulen).

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
