beslut: godkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- Endast `UppgiftStatus.kt`, `UppgiftStatusTest.kt` plus uppgiftsflytt `ny/` → `pagar/` (lokal modell); inga filer i fel sökväg.
- `uppgiftStatus` följer uppgiften: `FORSENAD` vid `nasta.isBefore(idag)`, `SNART` när `ChronoUnit.DAYS.between(idag, nasta) <= UPPGIFT_SNART_DAGAR`, annars `SENARE` — samma mönster som `garantiStatus` i förebilden.
- Alla fyra exemplen i uppgiften finns som `assertEquals` i `UppgiftStatusTest`.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inget kotlin.test.
- Inga nya beroenden, behörigheter eller nätverk; ren Kotlin med `java.time.LocalDate` utan Android.
- Ingen UI-kod eller hårdkodade användarsträngar.

## Rättat av Cursor
- inget

## Frågor till Claude
- inga
