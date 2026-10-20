beslut: underkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast tillåtna filer plus uppgiftsflytt `ny/` → `pagar/`; inga filer i repots rot.
- Bygget faller: testfilen använder `org.junit.jupiter` och `kotlin.test.assertEquals`, som inte finns i `:styrelserummet` — ska vara JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`).
- Testfilen ersätter innehållet från main (t.ex. `parseBeloppAvgransare`) i stället för att lägga till `hittaByggnadensVardeNegativa`; det bryter mot uppgiften och återinför en äldre `parseBelopp` utan tusentalsavgränsare från uppgift 01.
- `parseBelopp` uppfyller inte exemplen `parseBelopp("(1 234)", …)` och `parseBelopp("-500", iTusentals = true)` — negativ tecken hanteras bara via parametern `isNegative` från `hittaByggnadensVarde`, inte i själva beloppstexten.
- Testet `hittaByggnadensVardeNegativa` täcker inte `parseBelopp`-exemplen direkt.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.

## Rättat av Cursor
- inget (större fel: fel testramverk, regression av `parseBelopp`, ofullständig negativ parsning)

## Vid underkänd: försök 2

```
# Uppgift: Negativa belopp i årsredovisningen

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Testfilen ska använda JUnit 4 (`import org.junit.Test`, `import org.junit.Assert.assertEquals`), inte JUnit Jupiter eller `kotlin.test`.
- Behåll befintliga tester i `HittaByggnadensVardeTest.kt` (särskilt `parseBeloppAvgransare` från main) och **lägg till** testmetoden `hittaByggnadensVardeNegativa` — ersätt inte hela filen.
- `parseBelopp` på main har redan logik för tusentalsavgränsare (NBSP, smalt mellanslag, punkt). Utöka den versionen; skriv inte om till den enklare varianten som bara tar bort mellanslag.
- `parseBelopp` ska själv känna igen negativa belopp när texten börjar med `-` eller ligger inom parentes `( … )`, så exemplen i uppgiften stämmer utan extra parameter om du inte behöver den internt.
- Lägg till tester som anropar `parseBelopp` direkt enligt exemplen nedan.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVardeTest.kt

## Läs som förebild (ändra inte)
- `HittaByggnadensVarde.kt` och `HittaByggnadensVardeTest.kt` på main (särskilt `parseBelopp` och `parseBeloppAvgransare`)

## Uppgift
Utöka `parseBelopp` och/eller `hittaByggnadensVarde` så att negativa tal kan skrivas med minus eller inom parentes, t.ex. `(1 234)` eller `-500 tkr`. Returnera negativt `Long` i kronor när raden avser negativt värde. Befintlig positiv parsning och tusentalsavgränsare ska fortsatt fungera.

## Exempel
- Indata: `parseBelopp("(1 234)", iTusentals = false)` → `-1234`
- Indata: `parseBelopp("-500", iTusentals = true)` → `-500000`
- Indata: `Byggnader och mark (2 500) tkr` → `Belopp(-2500000, iTusentals = true)`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest

Lägg till tester (i samma testklass) som verifierar minst:
- `parseBelopp("(1 234)", iTusentals = false) == -1234L`
- `parseBelopp("-500", iTusentals = true) == -500000L`
- `hittaByggnadensVarde("Byggnader och mark (2 500) tkr") == Belopp(-2500000, iTusentals = true)`
- `hittaByggnadensVarde("Byggnader och mark -500 tkr") == Belopp(-500000, iTusentals = true)`
- `hittaByggnadensVarde("Byggnader och mark (1 234)") == Belopp(-1234, iTusentals = false)`
- Befintligt test `parseBeloppAvgransare` ska fortsatt vara grönt.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
```

## Frågor till Claude
- inga
