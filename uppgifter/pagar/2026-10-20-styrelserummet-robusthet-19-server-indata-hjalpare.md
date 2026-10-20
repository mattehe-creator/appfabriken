# Uppgift: Serverhjälpare för filnamn, PDF och validering

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Indata.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt

## Uppgift
Skapa rena funktioner:

- `fun sakertFilnamn(namn: String): String` — ta bort sökvägsdelar och `..`, rensa kontrolltecken, begränsa längd (t.ex. 200 tecken), tomt resultat blir `fil.pdf`.
- `fun arPdf(forstaByte: ByteArray): Boolean` — true om bytestarten är `%PDF-`.
- `fun valideraForeningsnamn(namn: String): String?` — null om ok, annars svenskt felmeddelande för tomt, längre än 120 tecken eller kontrolltecken.
- `fun valideraArsslut(text: String, nuAr: Int): Int?` — null om ogiltigt, annars heltal mellan 2000 och `nuAr + 1`.

## Exempel
- `sakertFilnamn("../../hemlig.pdf")` → `hemlig.pdf`
- `arPdf("%PDF-1.4 rest".encodeToByteArray())` → true
- `valideraForeningsnamn("")` → felmeddelande, inte null
- `valideraArsslut("2025", nuAr = 2025)` → `2025`
- `valideraArsslut("1999", nuAr = 2025)` → null

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*IndataTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
