beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- PR #343 / diff mot main: endast flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/` — ingen ändring i `FelHantering.kt` eller `FelHanteringTest.kt`.
- `ServerFel` saknar `ForStorFil` och `IntePdf`; `felTillMeddelande` hanterar bara `IngenLasbarText` och `KrypteradPdf`.
- Inga nya tester för storlek eller filtyp; befintliga tester täcker inte uppgiftens exempel.
- Inga otillåtna filer i repots rot; inga nya beroenden, behörigheter eller nätverk i PR:en (implementation saknas).
- Bygget grönt eftersom ingen kod ändrades — uppgiften är inte utförd.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Felmeddelande för för stor fil och fel filtyp

uppdrag: uppdrag/2026-10-21-styrelserummet-felhantering.md
försök: 2
beror-på: 2026-10-21-styrelserummet-felhantering-01-felmeddelande-pdf-text-krypterad-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). `FelHantering.kt` och `FelHanteringTest.kt` ändrades inte.
- Ingen `ServerFel.ForStorFil`, ingen `ServerFel.IntePdf`, inga utökade grenar i `felTillMeddelande`, inga nya JUnit-tester enligt exemplen nedan.

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/FelHantering.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Granser.kt (`MAX_BYTES_PER_JOBB` = 25 MB)
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/Application.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/FelHanteringTest.kt (befintliga tester för `IngenLasbarText` och `KrypteradPdf`)

## Uppgift
Utöka befintlig `sealed class ServerFel` i `FelHantering.kt` med:
- `ForStorFil` — total uppladdning över `MAX_BYTES_PER_JOBB` (importera eller referera gränsen från samma paket via `Granser.kt`)
- `IntePdf` — filen börjar inte med `%PDF-`

Utöka `felTillMeddelande` för dessa fall. Meddelandena ska vara på **svenska**, begripliga för en styrelseledamot, utan filnamn, stacktrace eller HTTP-koder.

För `ForStorFil`: nämn att filerna **tillsammans** är för stora och ungefärlig gräns **25 MB** i klartext (samma som `MAX_BYTES_PER_JOBB`).

För `IntePdf`: förklara att **bara PDF-filer** accepteras (detta ersätter inte hårdkodad sträng i `Application.kt` förrän senare uppgift kopplar in).

## Exempel
- `felTillMeddelande(ServerFel.ForStorFil)` → svenskt meddelande om att filerna är för stora tillsammans, med ungefärlig gräns (t.ex. innehåller «25» och «MB» eller «megabyte», samt hint om att minska total storlek / färre filer).
- `felTillMeddelande(ServerFel.IntePdf)` → svenskt meddelande om att bara PDF-filer accepteras (t.ex. innehåller «PDF» och «Ladda upp» eller liknande).

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*FelHanteringTest"

Lägg till minst **två** nya `@Test` i `FelHanteringTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`):

1. `forStorFil` — anropa `felTillMeddelande(ServerFel.ForStorFil)`; assert att meddelandet innehåller indikation på storlek/gräns (t.ex. «25» och «MB» eller «megabyte») och att det handlar om filer tillsammans / total storlek; assert **inte** «Exception».
2. `intePdf` — anropa `felTillMeddelande(ServerFel.IntePdf)`; assert att meddelandet innehåller «PDF» (eller «pdf») och accepteras/uppladdning; jämför **innehåll** mot förväntad innebörd, inte bara att funktionen returnerar en sträng.

Befintliga tester för `IngenLasbarText` och `KrypteradPdf` ska fortsätta gå igenom.

## Gör inte
- Ändra andra filer (inklusive `Application.kt`, `Granser.kt`, `uppgifter/`).
- Lägga till beroenden.
```

## Frågor till Claude
- inga
