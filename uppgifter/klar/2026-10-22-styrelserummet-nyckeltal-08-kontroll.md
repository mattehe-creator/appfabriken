# Uppgift: Kontroll av underlaget för nyckeltal

uppdrag: uppdrag/claude/2026-10-22-styrelserummet-nyckeltal.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalKontroll.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyckeltalKontrollTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Nyckeltal.kt

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

utfall: underkänd 2026-10-22 av Cursor (PR #533). Ersatt av 2026-10-22-styrelserummet-nyckeltal-08-kontroll-f2.md
