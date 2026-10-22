beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- Grenen innehåller enbart commit «Uppgift startad» (flytt `uppgifter/ny/` → `uppgifter/pagar/`); `NyckeltalKontroll.kt` och `NyckeltalKontrollTest.kt` saknas helt.
- Uppgiftens funktion `kontrolleraUnderlag` och fem kontrollregler är inte implementerade; inga tester mot exemplen.
- Inga otillåtna filer i repots rot; inga nya beroenden, behörigheter eller nätverk (ingen kod levererad).
- Bygget kan vara grönt eftersom inget nytt kompilerades; `:styrelserummet:testDebugUnitTest` kördes lokalt och är grönt.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Kontroll av underlaget för nyckeltal

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 2
beror-på: ingen

## Vad som saknades
- Du committade bara flytten av uppgiftsfilen till `uppgifter/pagar/`. Skapa och committa **båda** kodfilerna som uppgiften listar — utan dem är uppgiften tom.
- Implementera `fun kontrolleraUnderlag(u: NyckeltalUnderlag): List<String>` i ny fil `NyckeltalKontroll.kt` (samma paket som `Nyckeltal.kt`). Använd befintlig `avrundaKvot` från `Nyckeltal.kt`; inga flyttal.
- Returnera svenska meddelanden **i den ordning reglerna listas nedan**, bara de som gäller:
  1. `kvmBostadsratt == 0` och `arsavgifter > 0` → exakt: `Ytan upplåten med bostadsrätt saknas, men det finns årsavgifter.`
  2. `arsavgifter > rorelseintakter` och `rorelseintakter > 0` → exakt: `Årsavgifterna är större än de totala rörelseintäkterna. Kontrollera beloppen.`
  3. `energikostnad > rorelseintakter` och `rorelseintakter > 0` → exakt: `Energikostnaden är större än de totala rörelseintäkterna. Kontrollera beloppen.`
  4. `kvmBostadsratt > 0` och `avrundaKvot(arsavgifter, kvmBostadsratt) < 100` och `arsavgifter > 0` → exakt: `Årsavgiften är under 100 kr per kvm och år. Är beloppet angivet i tusental kronor?`
  5. `kvmBostadsratt + kvmHyresratt > 0` och `avrundaKvot(rantebarandeSkulder, kvmBostadsratt + kvmHyresratt) > 50_000` → exakt: `Skulden är över 50 000 kr per kvm. Kontrollera beloppet och ytan.`
- Skriv `NyckeltalKontrollTest.kt` med JUnit 4 (`org.junit`, inte `kotlin.test`). Bygg ett standardunderlag som i `NyckeltalTest` (årsavgifter 4_200_000, skulder 38_000_000, rörelseintäkter 4_600_000, energi 1_150_000, kvm bostadsrätt 5_200, hyresrätt 300; övriga fält valfria men konsekventa).
- Assert **hela listan** mot uppgiftens exempel:
  - standardunderlag → tom lista
  - samma med `arsavgifter = 4_200` → enbart meddelande 4
  - samma med `kvmBostadsratt = 0`, `kvmHyresratt = 0` → enbart meddelande 1
  - samma med `rorelseintakter = 1_000_000` → meddelande 2 och 3 i den ordningen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalKontroll.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalKontrollTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalTest.kt (underlagsexempel)

## Uppgift
Fånga troliga inmatningsfel innan nyckeltalen visas. Det är kontroller av siffrorna, inga råd.

```kotlin
fun kontrolleraUnderlag(u: NyckeltalUnderlag): List<String>
```

Returnera svenska meddelanden i denna ordning, bara de som gäller:
1. `kvmBostadsratt == 0` och `arsavgifter > 0` → "Ytan upplåten med bostadsrätt saknas, men det finns årsavgifter."
2. `arsavgifter > rorelseintakter` och `rorelseintakter > 0` → "Årsavgifterna är större än de totala rörelseintäkterna. Kontrollera beloppen."
3. `energikostnad > rorelseintakter` och `rorelseintakter > 0` → "Energikostnaden är större än de totala rörelseintäkterna. Kontrollera beloppen."
4. `kvmBostadsratt > 0` och `avrundaKvot(arsavgifter, kvmBostadsratt) < 100` och `arsavgifter > 0` → "Årsavgiften är under 100 kr per kvm och år. Är beloppet angivet i tusental kronor?"
5. `kvmBostadsratt + kvmHyresratt > 0` och `avrundaKvot(rantebarandeSkulder, kvmBostadsratt + kvmHyresratt) > 50_000` → "Skulden är över 50 000 kr per kvm. Kontrollera beloppet och ytan."

## Exempel
- Underlaget från uppgift 02 (årsavgifter 4 200 000, skulder 38 000 000, rörelseintäkter 4 600 000, energi 1 150 000, 5 200 + 300 kvm) → tom lista
- Samma med årsavgifter 4 200 (tusental av misstag) → [meddelande 4]
- Samma med kvm bostadsrätt 0 och hyresrätt 0 → [meddelande 1]
- Samma med rörelseintäkter 1 000 000 → [meddelande 2, meddelande 3]

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyckeltalKontrollTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.
- Committa enbart uppgiftsflytt utan kod.
```

## Frågor till Claude
- inga
