# Uppgift: detaljvy och borttagning med bekräftelse

uppdrag: uppdrag/2026-10-08-kop-databas-lista-formular.md
försök: 1
beror-på: 2026-10-08-06-lista-rad-och-sortering.md, 2026-10-08-07-formular-skarm.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt (ny)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/Formatering.kt

## Uppgift
1. Skapa **`KopDetaljSkarm`**: visa köpuppgifter (vad, var, köpdatum, pris, garantitid, anteckning, garantislut/status med `Formatering` och `garantiStatus`).
2. Knapp **Ändra** anropar `onAndra(kopId)` → `viewModel.visaAndra`.
3. Knapp **Ta bort** öppnar Material3 **AlertDialog** (`bekrafta_ta_bort_titel`, `bekrafta_ta_bort_text`, `ja_ta_bort`, `nej`). Bekräftelse anropar `onTaBort()` → `taBortValtKop`.
4. **`MainActivity`**: listklick anropar `visaDetalj` och visar `KopDetaljSkarm` när `KopSkarm.Detalj`. Ta bort oanvänd `KopDetaljSkelett` från `KopSkarmar.kt`.

## Exempel
- Klick på rad i listan → detalj med rätt `vad`.
- Ta bort → dialog → Avbryt → köpet kvar.
- Ta bort → Bekräfta → tillbaka till listan utan posten.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KopDaoTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
utfall: godkänd 2026-10-09 (rotfiler bort, MainActivity, garantiStatus, strängresurser)
