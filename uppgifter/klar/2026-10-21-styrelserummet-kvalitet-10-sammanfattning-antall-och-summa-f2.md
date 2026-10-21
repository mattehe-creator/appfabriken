# Uppgift: sammanfatta antal och anskaffningssumma

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-08-formatering-kronor-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Varken `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt` eller `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt` skapades.
- `gradle :styrelserummet:test --tests "*SammanfattningTest.antalOchSumma"` hittar ingen testklass; exemplen i uppgiften är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt

## Uppgift
Skapa `Sammanfattning.kt` med:

`fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String>`

Funktionen ska returnera **exakt två** strängar (fler kommer i senare uppgifter):

1. `"Listan innehåller N komponenter."` där N är `komponenter.size` (även 0).
2. `"Anskaffningsvärdet är X sammanlagt."` där X är `kronor(komponenter.sumOf { it.anskaffningsvarde })` — anropa befintliga `kronor` i samma paket.

Parametrarna `byggnadensVarde` och `arsslut` ska finnas i signaturen men används inte ännu (inga `_`-prefix krävs, men de får inte påverka resultatet).

Skapa `SammanfattningTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test` eller Jupiter). Testmetoden ska heta `antalOchSumma` och asserta **hela listan** mot exemplen nedan (gärna med en privat `komponent(...)`-hjälpare som i `SummeringTest`).

## Exempel
- Tom lista → `["Listan innehåller 0 komponenter.", "Anskaffningsvärdet är 0 kr sammanlagt."]`
- En komponent med `anskaffningsvarde = 1_000_000` → `["Listan innehåller 1 komponenter.", "Anskaffningsvärdet är 1\u00A0000\u00A0000 kr sammanlagt."]`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SammanfattningTest.antalOchSumma"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
- Lägga filer i repots rot.

utfall: godkänd 2026-10-21 (implementerade Sammanfattning.kt och SammanfattningTest.kt — tomma filer i försök 2)
