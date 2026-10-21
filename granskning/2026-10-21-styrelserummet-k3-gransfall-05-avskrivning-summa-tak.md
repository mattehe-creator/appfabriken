beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/` (commit «Uppgift startad»); ingen produktions- eller testkod.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallSummatakTest.kt` saknas — inte bland tillåtna filer enligt uppgiften.
- Uppgiftens beteende (summa av `avskrivningFor` över hela nyttjandeperioden = avskrivningsbart belopp) är inte testat; exemplen 1_000_000 kr/30 år och 100_000 kr/3 år finns inte som assert.
- Inga nya beroenden, behörigheter eller nätverk (ingen kod levererad).
- Ingen UI-kod eller hårdkodade strängar i appen från denna PR.
- `:styrelserummet:testDebugUnitTest` grönt lokalt (befintliga tester).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Summan av avskrivningar överstiger aldrig anskaffningsvärdet

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallSummatakTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*AvskrivningGransfallSummatakTest"` hittar ingen testklass; uppgiftens exempel är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallSummatakTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt (endast om testerna avslöjar fel i logiken)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt (hjälpfunktionen `komponent(...)` och JUnit 4-mönster)
- styrelserummet/regler/K3.md

## Uppgift
Skapa testklassen `AvskrivningGransfallSummatakTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

För en komponent: summera `avskrivningFor(k, ar)` för varje år från `k.anskaffningsar` till sista avskrivningsåret `k.anskaffningsar + k.nyttjandeperiodAr - 1` (inklusive). Summan ska **exakt** motsvara avskrivningsbart belopp `k.anskaffningsvarde - k.restvarde`, aldrig mer.

Testa minst:
1. **Lång period med avrundning:** 1_000_000 kr, anskaffningsår 2000, nyttjandeperiod **30** år, restvärde 0 — summa år 2000–2029 ska vara **1_000_000** exakt (normalt årsbelopp 33_333 kr; sista året tar resten).
2. **Kort period med avrundning uppåt:** 100_000 kr, anskaffningsår 2020, period **3**, restvärde 0 — summa år 2020–2022 ska vara **100_000** exakt (33_333 + 33_333 + 33_334).

Varje enskilt årsbelopp i loop/assert ska behandlas som `Long` (hela kronor). Gärna ett extra assert att inget enskilt årsbelopp är negativt.

Vid fel i produktionskoden: rätta **högst** i `Avskrivning.kt`.

## Exempel (ska finnas som assert i tester)
- 1_000_000 kr, anskaffningsår 2000, period 30, restvärde 0 → summa `avskrivningFor` för år 2000–2029 = **1_000_000**L.
- 100_000 kr, anskaffningsår 2020, period 3, restvärde 0 → summa för år 2020–2022 = **100_000**L.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallSummatakTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra andra testfiler (`AvskrivningTest.kt` m.fl.).
- Lägga till beroenden.
- Lägga filer i repots rot.

## Frågor till Claude
- inga
