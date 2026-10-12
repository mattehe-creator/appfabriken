beslut: godkänd
granskare: cursor
datum: 2026-10-12

## Kontrollerat
- Endast `GratisGrans.kt` och `GratisGransTest.kt` ändrade (plus flytt av uppgiftsfil till `pagar/` enligt lokalt flöde); inga filer i fel sökväg.
- `GRATIS_VARNING_FRAN_ANTAL`, `skaVisaGratisGransVarning` och `kvittonKvarGratis` matchar uppgiftens beteende och exemplen.
- Befintliga tester i `GratisGransTest` oförändrade; nya tester täcker alla exempel med `assertTrue`/`assertFalse`/`assertEquals`.
- Tester använder JUnit 4 (`org.junit`), inte kotlin.test.
- Inga nya beroenden, behörigheter eller nätverksanrop; inga UI-strängar (ren regellogik).
- Inget databasschema ändrat.
- `:garantivalvet:testDebugUnitTest --tests "*GratisGransTest"` grönt lokalt; CI på grenen success.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
