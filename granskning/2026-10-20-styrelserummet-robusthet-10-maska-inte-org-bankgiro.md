beslut: underkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- PR ändrar bara flytt av uppgiftsfilen (`uppgifter/ny/` → `uppgifter/pagar/`), inte de listade käll- eller testfilerna.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt` saknas i grenen; testet `maskaInteFalskaTrffar` är inte tillagt.
- Uppgiften kräver verifiering av beteendet mot exemplen via test; inget sådant test finns.
- Inga otillåtna filer utanför listan (men uppgiften är i praktiken oimplementerad).
- Inga nya beroenden, behörigheter eller nätverk i PR-diffen.
- Ingen UI-kod ändrad.
- FRAGA.md fanns inte.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Maskera inte org, bankgiro, belopp och datum

## Vad som saknades
- Grenen innehöll ingen implementation: varken `MaskaPersonuppgifter.kt` eller `MaskaPersonuppgifterTest.kt` ändrades eller skapades.
- Testmetoden `maskaInteFalskaTrffar` som uppgiften anger saknas helt (`MaskaPersonuppgifterTest.kt` finns inte i repot).
- Beteendet i exemplen (org/bankgiro/belopp/datum/fastighet oförändrat; personnummer och telefon maskade i blandad text) är inte verifierat med test.

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/MaskaPersonuppgifterTest.kt

## Uppgift
Säkerställ att `maskaPersonuppgifter` **inte** maskar:
- organisationsnummer `769612-3456`,
- bankgiro `123-4567`,
- belopp `1 234 567`, årtal `2024`, datum `2025-12-31`,
- fastighetsbeteckningar som `BRF Solen 1`.

Personnummer och telefon i samma text ska fortsatt maskas.

`MaskaPersonuppgifter.kt` har redan `serUtSomPersonnummer` som skiljer många falska träffar från riktiga personnummer — använd och justera vid behov så att exemplen stämmer.

Skapa testfilen om den saknas. Lägg till `@Test fun maskaInteFalskaTrffar()` som jämför `maskaPersonuppgifter(...)` mot förväntat resultat för alla tre exemplen nedan. Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`).

## Exempel
- Indata: `Org 769612-3456, bankgiro 123-4567` → oförändrat
- Indata: `Värde 1 234 567 kr, år 2024, per 2025-12-31, BRF Solen 1` → oförändrat
- Indata: `Org 769612-3456, kontakt 850709-1234, 070-123 45 67` → `Org 769612-3456, kontakt [personnummer], [telefon]`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*MaskaPersonuppgifterTest.maskaInteFalskaTrffar"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
```

## Frågor till Claude
- inga
