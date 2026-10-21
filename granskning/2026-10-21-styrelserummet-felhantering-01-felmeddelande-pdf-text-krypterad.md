beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #326 ändrade filer utanför uppgiften: `uppgifter/ny/.gitkeep` togs bort (omdöpt till `FelHantering.kt`) — endast de två angivna serverfilerna får ändras, utan att röra andra sökvägar.
- `FelHantering.kt` och `FelHanteringTest.kt` finns på rätt plats men är **tomma** (0 rader kod); ingen `ServerFel`, ingen `felTillMeddelande`, inga tester.
- Uppgiftens beteende och exempel är inte implementerade; `gradle :styrelserummet-server:test --tests "*FelHanteringTest"` hittar inga tester.
- Inga nya beroenden, behörigheter eller nätverksanrop i diffen (ingen produktionskod levererades).
- Ingen UI-kod; inga hårdkodade strängar i implementation (saknas).
- `:styrelserummet-server:test` (hela modulen) grönt lokalt; det säger inget om uppgiftens test.

## Rättat av Cursor
- inget (saknad implementation — inte småfel)

## Vid underkänd: försök 2

# Uppgift: Felmeddelande för PDF utan text och krypterad PDF

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: ingen

## Vad som saknades
- Commit `05aa7f1` skapade **tomma** filer: `FelHantering.kt` kom från omdöpt `uppgifter/ny/.gitkeep`, `FelHanteringTest.kt` är tom — ingen Kotlin-kod.
- Ingen `sealed class ServerFel`, ingen `felTillMeddelande`, inga JUnit-tester. Uppgiften kräver riktig implementation enligt avsnitten nedan.
- Rör **inte** `.gitkeep` eller andra filer utanför listan «Ändra bara dessa filer».

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/JobbKo.kt (hur PDF utan text beskrivs idag — kopplas in senare)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/pdf/PdfLasare.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IndataTest.kt (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
I paketet `se.tmconnect.styrelserummet.server`, i `FelHantering.kt`:

1. Deklarera `sealed class ServerFel` med minst:
   - `IngenLasbarText` — inskannad PDF eller bild utan markerbar text
   - `KrypteradPdf` — lösenordsskyddad PDF  
   (fler varianter kommer i senare uppgifter; använd t.ex. `data object` för enkla fall.)

2. Implementera:

```kotlin
fun felTillMeddelande(fel: ServerFel): String
```

Meddelandena ska vara på **svenska**, begripliga för en styrelseledamot, utan filnamn, stacktrace, HTTP-/API-koder eller personuppgifter.

## Exempel (innebörd — testerna ska låsa detta)
- `felTillMeddelande(ServerFel.IngenLasbarText)` → förklarar att PDF:en saknar läsbar/markbar text och att användaren behöver ladda upp en textbaserad PDF (inte teknisk jargon).
- `felTillMeddelande(ServerFel.KrypteradPdf)` → förklarar att filen är lösenordsskyddad och ska laddas upp olåst (utan lösenord).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

Skriv `FelHanteringTest.kt` med JUnit 4 (`org.junit`, **inte** `kotlin.test`). Minst två `@Test` som anropar `felTillMeddelande` för vardera varianten och **assertar** mot dina valda formuleringar, t.ex.:

- `IngenLasbarText`: meddelandet innehåller ord som «läsbar» (eller «markera») **och** hint om textbaserad/uppladdning — och **inte** ett filnamn eller «Exception».
- `KrypteradPdf`: meddelandet innehåller «lösenord» (eller «låst») **och** att filen ska laddas upp olåst.

Använd `assertEquals` om du låser exakta strängar, eller `assertTrue(meddelande.contains(...))` för delsträngar — men testerna måste **jämföra** faktiskt resultat mot förväntad innebörd, inte bara anropa funktionen.

## Gör inte
- Ändra andra filer (inklusive `uppgifter/ny/.gitkeep`).
- Lägga till beroenden.
- Implementera HTTP-status eller koppling till API ännu.

## Frågor till Claude
- inga
