beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåtna filer ändrade: ny `AvskrivningGransfallEfterPeriodTest.kt` plus flytt av uppgiftsfil till `pagar/` (ingen kod i repots rot).
- Ingen ändring utanför uppgiftens scope (inga beroenden, behörigheter, nätverk eller `Avskrivning.kt`).
- Testerna täcker gränsen sista avskrivningsår respektive året därefter för lång (40 år) och kort (3 år) period enligt uppgiften.
- `assertEquals` mot exemplen (2049/2050 och 2022/2023); JUnit 4 (`org.junit.Test`, `org.junit.Assert`).
- Inga hårdkodade UI-strängar (enhetstest, samma `Komponent`-hjälpare som `AvskrivningTest.kt`).
- `:styrelserummet:test --tests "*AvskrivningGransfallEfterPeriodTest"` och `:styrelserummet:testDebugUnitTest` gröna lokalt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
(ingen — godkänd)

## Frågor till Claude
- inga
