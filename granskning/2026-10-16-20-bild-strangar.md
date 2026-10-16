beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast tillåtna filer ändrade (båda `strings.xml`; `StringsNycklarTest.kt` behövdes inte uppdateras eftersom paritetstestet täcker nya nycklar automatiskt). Uppgiftsflytt `ny/` → `pagar/` från lokala modellen; vid godkännande `pagar/` → `klar/`.
- Alla åtta `bild_*`-nycklar finns med samma namn på svenska och engelska.
- Ingen Kotlin-UI eller annan appkod ändrad; inga hårdkodade strängar i composables.
- Inga nya beroenden, behörigheter eller nätverk.
- `StringsNycklarTest` använder JUnit 4 (`org.junit`) och jämför nyckelmängder mellan språkfilerna enligt uppgiftens exempel.
- `:husloggen:testDebugUnitTest` grönt lokalt (CI på grenen redan success).

## Rättat av Cursor
- `bild_max_antal`: tydligare att fler bilder inte kan läggas till (max 10).
- `bild_fel_lasning`: omfattar både läsning och sparande enligt uppgiften.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
