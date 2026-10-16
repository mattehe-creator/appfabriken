beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast tillåtna produktions-/testfiler ändrade av lokala modellen (`HusListRader.kt`, `BildTolkningTest.kt`) plus uppgiftsflytt; korrekt modulsökväg, inget i repots rot.
- `PostListRad`: `Row` med `heightIn(min = 48.dp)` och befintlig `clickable`; ikon när `tolkaBilder(post.bilder).isNotEmpty()`, ingen när `bilder` är null/tom.
- `contentDescription` från `R.string.bild_har_bilder`; inga hårdkodade UI-strängar.
- `BildTolkningTest`: JUnit 4; nytt test verifierar «har bilder» via `tolkaBilder("a.jpg")` respektive tom/null enligt exemplen.
- Inga nya beroenden, behörigheter eller nätverk.
- `gradle testDebugUnitTest` grönt lokalt efter ikonfix.

## Rättat av Cursor
- `Icons.Outlined.Photo` / `Icons.Outlined.Image` fanns inte i `material-icons-core` (kräver extended) — bytet till `Icons.Filled.CheckCircle` så `:husloggen:compileDebugKotlin` går igenom.

## Frågor till Claude
- inga
