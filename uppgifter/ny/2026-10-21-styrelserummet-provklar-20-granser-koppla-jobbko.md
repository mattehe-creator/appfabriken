# Uppgift: Kostnadsskydd före Tolkare

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar-forsta-korning.md
försök: 1
beror-på: 2026-10-21-styrelserummet-provklar-19-granser-for-stor-felkod.md

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/JobbKoTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt

## Uppgift
I `bearbeta`, **efter** att PDF-text är extraherad och **före** anrop till `byggUtkast`/`Tolkare`:

1. Beräkna total textmängd (underhållsplan + årsredovisning som ska tolkas) med `skattaTecken`.
2. Om summan > `MAX_TECKEN_TILL_TOLKNING`: markera jobb som **FEL** med meddelande från `Felmeddelanden.forklara(Felkoder.FOR_STOR_HANDLING)` (eller motsvarande `ServerFel` om ni inför det — håll det konsekvent med övriga jobbfel), **returnera utan** att anropa tolkare.

Test `forStorHandlingAnroparInteTolkare`:

- Spion-`Tolkare` som räknar anrop.
- PDF (eller direkt injicerad väg) med text så totalen **överstiger** gränsen (generera lång text i testet, påhittad).
- Assert: `foreslaKomponenter` anropades **0** gånger, meta status FEL, felmeddelande matchar FOR_STOR_HANDLING-text.

Test `underGransenAnroparTolkare`: motsatt med kort text, minst 1 anrop.

## Exempel
- Plan med 401_000 tecken 'x' → 0 tolkaranrop, FEL-status.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*JobbKoTest.forStorHandling*"

## Gör inte
- Ändra `MAX_TECKEN_TILL_TOLKNING` utan att uppgift 18 matchar.
- Skicka riktiga handlingar.
