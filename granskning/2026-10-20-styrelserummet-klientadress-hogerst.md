beslut: godkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast tillåtna filer ändrade (`KlientAdress.kt`, `KlientAdressTest.kt`; uppgiftsfilen flyttades från `pagar/` till `klar/` av granskningen).
- Beteendet följer uppgiften: sista icke-tomma adressen i `X-Forwarded-For` när `proxyPaa` är true, annars `anslutning`; trim och tomma poster hanteras.
- Testerna använder JUnit 4 (`org.junit`) och matchar exemplen i uppgiften; de två befintliga testerna (proxy av, null-header) är oförändrade i innehåll.
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen hårdkodad UI-text (servermodul, ingen Compose).
- `:styrelserummet-server:test` och `:styrelserummet-server:testDebugUnitTest` gröna lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
