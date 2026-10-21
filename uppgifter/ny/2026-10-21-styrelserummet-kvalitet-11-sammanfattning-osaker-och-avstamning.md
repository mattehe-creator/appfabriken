# Uppgift: sammanfatta osäkerhet och avstämning

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-10-sammanfattning-antall-och-summa.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Sakerhet.kt

## Uppgift
Utöka `sammanfatta` så att listan har **fyra** meningar: de två tidigare plus:

3. `"P komponenter (Q) har uppskattat värde eller låg säkerhet."` där P är antalet med `sakerhet == UPPSKATTNING` eller `LAG`, och Q är `procent(P, komponenter.size)`.
4. Avstämning mot `byggnadensVarde` med `stamAv`:
   - Om `byggnadensVarde == null`: `"Byggnadens värde saknas, så avstämning görs inte."`
   - Om `stammer`: `"Komponenternas anskaffningsvärde stämmer med byggnadens värde."`
   - Annars: `"Skillnaden mot byggnadens värde är D."` där D är `kronor(abs(differens))` från `stamAv` (visa absolut differens).

## Exempel
- 2 av 4 med `LAG`, procent `"50,0 %"`
- Byggnad 10_000_000, summa 9_000_000 → `"Skillnaden mot byggnadens värde är 1\u00A0000\u00A0000 kr."`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SammanfattningTest.osakerOchAvstamning"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
