# Uppgift: Syntetisk underhållsplan två sidor

uppdrag: uppdrag/2026-10-21-styrelserummet-provklar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Bygget failar: `Forening(..., orgnummer = ...)` — använd `Forening("Testföreningen BRF", 1975)` som i `SyntetiskPlanTabellTest`.
- Underhållsplanen ska läsas med `PdfLasare().lasSomText(pdfBytes).getOrThrow()`, inte `pdfBytes.toString(Charsets.UTF_8)`.
- JSON måste använda giltiga `Komponentgrupp`-namn: `VENTILATION` och `HISS` (inte `VVS` / `ELEKTRISK`).
- Assert att stubben registrerat sidnummer (minst att både sida 1 och 2 förekommer i något `sidor`-anrop) och att slutligt utkast har båda komponenterna.
- Assert LAG-säkerhet på komponenterna och motsvarande anmärkningstext («låg säkerhet») i `utkast.anmarkningar`.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTvaSidorTest.kt (ny/ersätt)

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanTabellTest.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasareTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt

## Uppgift
Skriv testklass `SyntetiskPlanTvaSidorTest` med metoden `planTvaSidorFortsattning`:

1. Skapa syntetisk PDF med två sidor (PdfBox, som i `PdfLasareTest.pdfMedText`): sida 1 text ungefär «Ventilation 800 000», sida 2 «Hiss 200 000».
2. `val planText = PdfLasare().lasSomText(pdfBytes).getOrThrow()`.
3. Stub `Tolkare` som append:ar varje `sidor: List<Int>` till en lista `registreradeSidorAnrop: MutableList<List<Int>>` och returnerar JSON:
   - om anropet bara gäller sida 1: en komponent Ventilation, grupp `VENTILATION`, anskaffningsvarde 800000, nyttjandeperiodAr 15, sakerhet `LAG`;
   - om bara sida 2: Hiss, grupp `HISS`, 200000, nyttjandeperiodAr 20, sakerhet `LAG`;
   - om båda sidor i samma anrop: båda komponenterna i samma JSON.
   Anrop kan ske en eller flera gånger beroende på `delaUpp` — det spelar ingen roll så länge slututkastet innehåller båda.
4. Årsredovisningstext: t.ex. `Balansräkning\nByggnader och mark 1 000 000` så `byggnadensVarde` blir 1_000_000.
5. `byggUtkast(planText, arText, Forening("Testföreningen BRF", 1975), tolkare, nuAr = 2025)`.
6. Assert:
   - registrerade sidnummer 1 och 2 förekommer (t.ex. `registreradeSidorAnrop.flatten().contains(1)` och samma för 2);
   - `utkast.komponenter.size == 2`;
   - Ventilation 800000, Hiss 200000;
   - `utkast.avstamning!!.stammer`;
   - båda komponenterna har `Sakerhet.LAG`;
   - `utkast.anmarkningar` innehåller «låg säkerhet» för båda namnen (eller motsvarande standardtext från `anmarkningarFor`).

## Exempel
- Sida 1: Ventilation 800 000, sida 2: Hiss 200 000, byggnad 1 000 000 → 2 komponenter, `stammer`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SyntetiskPlanTvaSidorTest"

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`).

## Gör inte
- Ändra `Komponent`, `Forening` eller produktionskod.
- Riktiga PDF-filer i repot.
- kotlin.test eller JUnit Jupiter.
