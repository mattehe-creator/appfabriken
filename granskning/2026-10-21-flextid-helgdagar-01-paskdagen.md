beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Helgdagar.kt`, `HelgdagarTest.kt` och flytt av uppgiftsfil till `pagar/` (lokalt workflow); inga filer i fel sökväg.
- Ingen ändring utanför uppgiftens avsikt: `Helgdag`, `paskdagen` med Meeus/Jones/Butcher (heltal), `IllegalArgumentException` för år < 1900 eller > 2200.
- Testerna jämför alla sju datumexempel och 1899 (samt 2201) mot förväntat resultat; inte bara att funktionen körs.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; inga flyttal i algoritmen.
- Ingen UI-kod; undantagsmeddelande i produktionskod är acceptabelt här (inte användarsträng i gränssnitt).
- `:flextid:testDebugUnitTest --tests "*HelgdagarTest"` grönt lokalt.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
