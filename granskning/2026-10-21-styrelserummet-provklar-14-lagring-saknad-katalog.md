beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `JobbLagring.kt` och `JobbLagringTest.kt` ändrade; inga filer i fel sökväg.
- Beteendet stämmer: saknad jobbkatalog ger `null`/`emptyList()` och tyst `return` på skrivning utan okontrollerat undantag (`lasMeta`, `lasUtkast`, `listaFiler`, `sparaUtkast`, `sparaFil`).
- Testet skapar jobb, raderar katalogen manuellt och assertar mot exemplat (null meta/utkast, tom fillista) utan krasch.
- Inga nya beroenden, behörigheter eller nätverksanrop; HTTP-lager orört.
- Ingen UI-kod eller hårdkodade användarsträngar.
- Tester använder JUnit 4 (`org.junit`); `:styrelserummet-server:test --tests "*JobbLagringTest*"` grönt lokalt.

## Rättat av Cursor
- `JobbLagringTest.kt`: import `kotlin.io.path.exists` och korrekt `UtkastDto`/`AvstamningDto` (CI kompileringsfel efter försök att rätta DTO).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
