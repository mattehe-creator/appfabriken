beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `ButikSkarmbilderTest.kt` och Roborazzi-PNG:er under `garantivalvet/butik/skarmbilder/en/` ändrade; ingen appkod, inga filer i repots rot; inga `butik/sv/*.txt`, `en/*.txt` eller `data-safety.md`.
- `exempelLista()`, `exempelDetalj()` och `exempelFormular()` är språkspecifika; engelska texter enligt uppgiften; svenska oförändrade; `formulär()` hämtar `initialVad`, `initialVar`, garanti och köpdatum från `exempelFormular()`.
- JUnit 4 (`org.junit`); befintliga Roborazzi-tester och `skarmQualifiersLaddarRattSprak` jämför mot förväntade strängar/resurser.
- `gradle testDebugUnitTest` grönt lokalt (hela portföljen).
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar i appkod (testdata i testklasser är avsedd).

## Rättat av Cursor
- Körde `recordRoborazziDebug` och committade omgenererade engelska skärmbilder (`en/01–04`) som saknades i PR:en.
- Tog bort oanvända importer (`Configuration`, `Locale`) efter omställning till enbart `RuntimeEnvironment.setQualifiers`.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
