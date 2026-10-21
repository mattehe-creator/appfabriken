beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna kodfiler ändrade (plus uppgiftsflytt till `pagar/` från körskriptet); inga filer i repots rot.
- Beteendet matchar uppgiften: normalisering, NY/BORTTAGEN/ANDRAD, oförändrad utesluten, sortering på namn.
- Tester använder JUnit 4 och assertar exemplen NY, BORTTAGEN, ANDRAD, oförändrad och namnnormalisering.
- Inga nya beroenden, behörigheter eller nätverk; ingen hårdkodad UI-text; datamodellen oförändrad.
- `:styrelserummet:testDebugUnitTest` grönt efter granskning.

## Rättat av Cursor
- `Jamforelse.kt`: lade till `ForandringTyp` och `Forandring` i `regler` (uppgiften); tog bort ogiltig import från `modell`.
- `JamforelseTest.kt`: blandat fall förväntade tre rader (inkl. `Hiss` BORTTAGEN) i alfabetisk ordning.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
