beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Endast tillåten testfil ändrad (plus uppgiftsflytt och granskning); korrekt sökväg under husloggen/src/test.
- Testerna täcker sorteraAvlasningar och senasteAvlasningForeDatum enligt uppgiftens exempel.
- JUnit 4 (org.junit), inga kotlin.test-imports.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod eller hårdkodade strängar.
- Bygget failade i CI eftersom assertEquals jämförde Int med Long (Avlasning.id); rättat.

## Rättat av Cursor
- AvlasningUrvalTest: Long-literaler (1L, 2L, 3L), assertNull för null-fall, listOf(1L, 2L, 3L) via map { it.id } som i uppgiften.

## Vid underkänd: försök 2
(inget — godkänd)

## Frågor till Claude
- inga
