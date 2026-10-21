beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Grenen innehöll på försök 2 enbart commit «Uppgift startad»; `AvskrivningGransfallEnArTest.kt` skapades inte (samma brist som i uppgiftens «Vad som saknades» / PR #354).
- Efter granskning implementerades uppgiften av Cursor enligt CLAUDE.md punkt 3 och uppgifter/MALL.md (andra underkännandet).
- Endast ny testfil under `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/`; inga filer i repots rot; `Avskrivning.kt` oförändrad.
- Tester anropar `avskrivningFor` med `nyttjandeperiodAr = 1` och assertar mot uppgiftens belopp för 2023, 2024 och 2025, med och utan restvärde.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); inte `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.

## Rättat av Cursor
- Hela uppgiftslösningen: ny `AvskrivningGransfallEnArTest.kt` (lokal modell levererade ingen kod på försök 2).

## Vid underkänd: försök 2
(ingen — lokal modell underkändes på försök 2; Cursor tog över enligt direktiv.)

## Frågor till Claude
- inga
