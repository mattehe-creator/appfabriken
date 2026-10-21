# Uppgift: Avstämning exakt träff, 1 kr och extremvärden

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvstamningGransfallTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*AvstamningGransfallTest"` kan inte köras utan testklassen; uppgiftens exempel och gränsfall är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvstamningGransfallTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt (endast om testerna avslöjar fel i logiken)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ReglerTest.kt (JUnit 4 och privat `komponent(...)`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallRakenskapsarTest.kt
- uppgifter/klar/2026-10-19-styrelserummet-04-avstamning.md

## Uppgift
Skapa testklassen `AvstamningGransfallTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`).

Testa `stamAv(komponenter, byggnadensVarde)`:

1. **Exakt träff:** summa komponenter = byggnadens värde → `stammer == true`, `differens == 0`, `summa` = summan av komponenterna.
2. **Skillnad 1 kr:** byggnad 10_000_000, komponenter som summerar 9_999_999 → `stammer == false`, `differens == 1`.
3. **Ingen tolerans:** `stamAv` räknar bara `differens == 0L` som stämmer. Ett testnamn eller kort kommentar ska dokumentera att skillnad 1 kr **inte** räknas som stämmer.
4. **Byggnadsvärde 0** med komponenter som summerar > 0 → `differens == byggnadensVarde - summa` (negativ), `stammer == false`.
5. **Negativ differens:** komponenter summerar högre än byggnad → `differens < 0`, `stammer == false`.

Vid fel i produktionskoden: rätta **högst** i `Avstamning.kt`.

## Exempel (ska finnas som assert i tester)
- Byggnad 10_000_000, komponenter 6_000_000 + 4_000_000 → stämmer, differens 0.
- Byggnad 0, en komponent 100_000 → differens −100_000, stämmer false.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvstamningGransfallTest"

## Gör inte
- Ändra `ReglerTest.kt`, modeller eller andra filer.
- Lägga till beroenden.
- Lägga filer i repots rot.
