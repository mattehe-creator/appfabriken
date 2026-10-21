beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil till `pagar/` och ny fil `AvskrivningGransfallRakenskapsarTest.kt` — rätt sökväg, inga filer i repots rot.
- Tillåten fil enligt uppgiften är ändrad, men innehållet saknas: testfilen är tom (0 rader kod).
- Uppgiften kräver JUnit-tester för `avskrivningFor(k, ar)` med exemplen (30_000 kr för anskaffningsår 2025, 0 kr för 2024); inget test implementerat.
- Inga nya beroenden, behörigheter eller nätverk; `Avskrivning.kt` och övriga filer orörda.
- `:styrelserummet:testDebugUnitTest` grönt lokalt (tom `.kt`-fil kompilerar men uppfyller inte uppgiften).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Avskrivning när anskaffningsår är räkenskapsår

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Commit «Lokal modell: … (okommittade ändringar efter Aider)» lade till `AvskrivningGransfallRakenskapsarTest.kt` men filen är **tom** (ingen `package`, ingen klass, inga tester).
- Uppgiftens beteende och exempel är inte testade; `gradle :styrelserummet:test --tests "*AvskrivningGransfallRakenskapsarTest"` hittar ingen `@Test`-metod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningGransfallRakenskapsarTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/AvskrivningTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Implementera testklassen `AvskrivningGransfallRakenskapsarTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

Testa `avskrivningFor(k: Komponent, ar: Int)` när komponenten **anskaffas samma kalenderår som räkenskapsåret** (köp mitt i året modelleras som helt räkenskapsår enligt K3.md).

Kontrollera att:
1. Första avskrivningsåret (anskaffningsåret) ger **samma** helårsbelopp som övriga år i perioden (inte pro rata dagar).
2. Avskrivning sker det året (belopp > 0 när året ligger i perioden).
3. År före anskaffningsår ger 0 kr.

Återanvänd samma mönster som `AvskrivningTest.kt` för att skapa `Komponent` (privat hjälpfunktion `komponent(...)` med `Komponentgrupp`, `Sakerhet` osv.). Använd **Long** för kronor, inga flyttal i tester.

Om ett test visar fel beteende får du **endast** ändra `Avskrivning.kt` (lägg till filen under «Ändra bara dessa filer» i PR:en och håll dig inom cirka 150 rader totalt).

## Exempel
- Indata: anskaffningsvärde 90_000 kr, anskaffningsår 2025, nyttjandeperiod 3 år, restvärde 0, `ar` = 2025. Förväntat: 30_000 kr (samma som 2026 och 2027 före sista året).
- Indata: samma komponent, `ar` = 2024. Förväntat: 0 kr.

Lägg minst till tester som assertar exakt dessa värden med `assertEquals(30_000L, …)` respektive `assertEquals(0L, …)`. Gärna ett extra test som jämför 2025, 2026 och 2027 (före sista året) så att alla tre ger 30_000 kr.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AvskrivningGransfallRakenskapsarTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra `AvskrivningTest.kt`, `Komponent.kt`, `Forening.kt` eller andra filer (utom `Avskrivning.kt` om test visar bugg och då enligt regeln ovan).
- Lägga till beroenden.
- Använda flyttal för pengar i tester eller implementation.
```

## Frågor till Claude
- inga
