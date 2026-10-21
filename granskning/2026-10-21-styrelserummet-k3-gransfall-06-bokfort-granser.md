beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast `BokfortVardeGransfallGranserTest.kt` (ny) och flytt av uppgiftsfil till `uppgifter/pagar/`; inga filer i repots rot.
- Tillåten testfil skapades men är **helt tom** — ingen testklass, inga assert, uppgiftens beteende är inte implementerat.
- Exemplen (1_200_000/40 år, 100_000/3 år, gränser ≥ 0 och ≤ anskaffningsvärde) testas inte.
- `BokfortVarde.kt` och övriga filer orörda (korrekt enligt «Gör inte»).
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Bokfört värde aldrig negativt eller över anskaffningsvärdet

## Vad som saknades
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallGranserTest.kt` fanns som fil men hade **noll rader kod** — inga tester kördes och kraven i uppgiften uppfylldes inte alls.
- Implementera en JUnit 4-testklass (samma mönster som `BokfortVardeTest.kt`: `org.junit.Test`, `org.junit.Assert`, privat `komponent(...)`-hjälpare).

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeGransfallGranserTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/BokfortVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt
- styrelserummet/regler/K3.md

## Uppgift
Testa `bokfortVarde(k: Komponent, arsslut: Int)` för flera år (före anskaffning, mitt i period, efter slut) och assert:
- resultatet är **≥ 0**,
- resultatet är **≤ anskaffningsvarde** (bokfört värde ska inte överstiga anskaffningsvärdet).

Använd minst en komponent med avrundningsrest (100_000 kr, 3 års period) och en lång period (t.ex. 1_200_000 kr, 40 år).

Vid fel i produktionslogiken: rätta högst i `BokfortVarde.kt` (samma fil som uppgiften anger).

## Exempel
- 1_200_000 kr, 2010, 40 år, arsslut 2025 → 720_000 (som befintligt test), och för arsslut 2055 efter full avskrivning → 0, aldrig negativt.
- Före anskaffning (arsslut 2009) → 1_200_000 (oförändrat), inte negativt.
- 100_000 kr, 2020, 3 år: kontrollera flera `arsslut` i och runt perioden; varje `bokfortVarde` ska ligga i [0, 100_000].

Tips: loopa över ett rimligt årsintervall och använd `assertTrue` / `assertEquals` mot exemplen ovan — kopiera inte bara tom fil.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*BokfortVardeGransfallGranserTest"

## Gör inte
- Ändra `BokfortVardeTest.kt` eller modeller.
- Lägga till beroenden.
- Lämna testfilen tom.
```

## Frågor till Claude
- inga
