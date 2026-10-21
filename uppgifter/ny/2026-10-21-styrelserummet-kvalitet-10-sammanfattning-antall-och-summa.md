# Uppgift: sammanfatta antal och anskaffningssumma

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-08-formatering-kronor.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt

## Uppgift
`fun sammanfatta(komponenter: List<Komponent>, byggnadensVarde: Long?, arsslut: Int): List<String>`

I denna uppgift ska funktionen returnera **exakt två** meningar (fler kommer i senare uppgifter):

1. `"Listan innehåller N komponenter."` där N är antalet i listan (även 0).
2. `"Anskaffningsvärdet är X sammanlagt."` där X är `kronor(summan av anskaffningsvarde)`.

Parametrarna `byggnadensVarde` och `arsslut` används inte ännu men ska finnas i signaturen.

## Exempel
- Tom lista → `["Listan innehåller 0 komponenter.", "Anskaffningsvärdet är 0 kr sammanlagt."]`
- En komponent med 1_000_000 kr → andra meningen `"Anskaffningsvärdet är 1\u00A0000\u00A0000 kr sammanlagt."`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SammanfattningTest.antalOchSumma"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
