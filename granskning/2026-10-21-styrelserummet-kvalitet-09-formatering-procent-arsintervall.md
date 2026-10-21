beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade (efter rättelse; lokala modellen hade lagt kod i repots rot).
- Ingen ändring utanför uppgiftens scope (inga beroenden, behörigheter, nätverk eller datamodell).
- `procent` och `arsintervall` matchar uppgiftens beteende och exempel.
- Test `procentOchArsintervall` jämför mot exemplen; JUnit 4 (`org.junit`).
- Inga hårdkodade UI-strängar (ren formateringslogik).
- `:styrelserummet:test` och `testDebugUnitTest` gröna lokalt.

## Rättat av Cursor
- Flyttade implementation och test från `Formatering.kt` / `FormateringTest.kt` i repots rot till `styrelserummet/src/main/kotlin/.../Formatering.kt` respektive `styrelserummet/src/test/kotlin/.../FormateringTest.kt`.
- En-dash `\u2013` explicit vid `helhet == 0` i `procent`; `Locale.US` i `String.format` för stabil decimalpunkt före kommaersättning.

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
