beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `JobbLagring.kt`, `JobbLagringTest.kt` och flytt av uppgiftsfilen `uppgifter/pagar/` (ingen fil i fel sökväg i rot).
- Beteendet stämmer: stress-test med många samtidiga `uppdateraAktivitet`, `synchronized` kring `meta.json`-skrivning utan API-ändring.
- Testet verifierar att `lasMeta` returnerar giltig `JobbMeta` efter join (motsvarar exemplat med lyckad JSON-dekodning).
- Inga nya beroenden, behörigheter eller nätverksanrop.
- Ingen UI-kod; inga hårdkodade användarsträngar utöver testdata i enhetstest.
- Tester använder JUnit 4 (`org.junit`); `:styrelserummet-server:test --tests "*JobbLagringTest.samtidigSkrivMetaGerGiltigJson"` grönt lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
