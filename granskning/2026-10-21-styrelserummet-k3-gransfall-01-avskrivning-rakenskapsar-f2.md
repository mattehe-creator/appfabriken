beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: lokal modell ändrade bara flytt av uppgiftsfil till `pagar/`; `AvskrivningGransfallRakenskapsarTest.kt` saknades (samma som försök 1).
- Efter granskning: endast tillåten testfil under `styrelserummet/…/regler/` plus `granskning/` och flytt av uppgiftsfil; inga filer i repots rot.
- Tester täcker uppgiftens exempel (30_000 kr för 2025, 0 kr för 2024) och jämför 2025–2027 före sista året; helårsbelopp i anskaffningsår enligt K3-gränsfall.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`); Long för kronor; inga flyttal i tester.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- `:styrelserummet:test --tests "*AvskrivningGransfallRakenskapsarTest"` grönt lokalt (`testDebugUnitTest` saknar `--tests` i denna modul).

## Rättat av Cursor
- Hela `AvskrivningGransfallRakenskapsarTest.kt` (lokal modell levererade ingen testkod i försök 2, enligt uppgifter/MALL.md).

## Vid underkänd: försök 2

## Frågor till Claude
- inga
