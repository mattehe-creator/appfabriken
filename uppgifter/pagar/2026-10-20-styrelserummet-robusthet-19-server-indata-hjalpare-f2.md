# Uppgift: Serverhjälpare för filnamn, PDF och validering

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- PR #308 levererade **ingen implementation**: varken `styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Indata.kt` eller `IndataTest.kt` skapades.
- Enda ändringen var att uppgiftsfilen flyttades till `uppgifter/pagar/` — det räcker inte; uppgiften kräver kod och tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Indata.kt (ny)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/KlientAdressTest.kt (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
Skapa filen `Indata.kt` i paketet `se.tmconnect.styrelserummet.server` med rena funktioner:

- `fun sakertFilnamn(namn: String): String` — ta bort sökvägsdelar (`/` och `\`) och `..`, rensa kontrolltecken (Unicode-kategori `Cc` räcker), begränsa längd till högst 200 tecken, tomt resultat efter rensning blir `fil.pdf`.
- `fun arPdf(forstaByte: ByteArray): Boolean` — `true` om minst fem byte och bytestarten är exakt `%PDF-` (ASCII).
- `fun valideraForeningsnamn(namn: String): String?` — `null` om ok; annars ett kort **svenskt** felmeddelande (sträng) för: tomt namn, längre än 120 tecken, eller innehåller kontrolltecken.
- `fun valideraArsslut(text: String, nuAr: Int): Int?` — `null` om texten inte är ett giltigt heltal i intervallet **2000 … nuAr + 1** (inklusive); annars det parsade heltalet.

## Exempel
- `sakertFilnamn("../../hemlig.pdf")` → `hemlig.pdf`
- `arPdf("%PDF-1.4 rest".encodeToByteArray())` → `true`
- `valideraForeningsnamn("")` → felmeddelande (inte `null`)
- `valideraArsslut("2025", nuAr = 2025)` → `2025`
- `valideraArsslut("1999", nuAr = 2025)` → `null`

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*IndataTest"

Skriv `IndataTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull` / `assertNotNull`) och **asserta exakt** uppgiftens exempel ovan (plus minst ett fall där `arPdf` är `false`, t.ex. tom byte-array eller fel prefix). Jämför faktiska resultat mot förväntade värden — inga tester som bara anropar funktionen utan assertion.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Lägg filer i repots rot — endast sökvägarna under `styrelserummet-server/` ovan.
