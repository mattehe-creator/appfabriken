beslut: godkänd
granskare: cursor
datum: 2026-10-13

## Kontrollerat
- Endast tillåtna filer ändrade (`KopFormularSkarm.kt`, `values/strings.xml`, `values-sv/strings.xml`); inga filer i repots rot eller fel sökväg.
- Formuläret föreslår varutyp via `gissaVarutyp(vad)` när användaren inte själv valt; chip-klick sätter `varutypForslag = false`; redigering med `initialVarutyp` skrivs inte över; hint-texten `varutyp_forslag` visas under chips enligt uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; UI-text från `strings.xml`.
- Angivna tester (`StringsNycklarTest`, `ButikSkarmbilder*`, `testDebugUnitTest`) gröna efter rättningar.

## Rättat av Cursor
- Återställde `values-sv/strings.xml` (lokal modell hade orelaterat omskrivit hundratals svenska strängar; endast `varutyp_forslag` skulle till).
- Återställde onödiga ändringar i `values/strings.xml` (endast `varutyp_forslag` kvar utöver bas).
- `LaunchedEffect(vad)`: sätter `varutyp = gissaVarutyp(vad)` och `varutypForslag = (resultat != null)` även när gissningen blir `null` (t.ex. "Present" efter tidigare förslag).

## Vid underkänd: försök 2
(inget)

## Frågor till Claude
- inga
