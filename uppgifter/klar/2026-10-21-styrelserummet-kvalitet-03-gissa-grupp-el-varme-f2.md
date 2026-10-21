# Uppgift: gissaGrupp för el och värme

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-02-gissa-grupp-stammar-ventilation-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Varken nyckelord för EL/VARME i `GrupperaNamn.kt` eller testmetoden `gissaGruppElVarme` i `GrupperaNamnTest.kt` lades till.
- `gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppElVarme"` kan inte köras utan testmetoden; uppgiftens exempel är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka befintlig `gissaGrupp` (samma logik som tidigare uppgifter: gemener, lägst index vinner; vid samma index längre nyckelord före kortare enligt `thenByDescending` på ordlängd).

Lägg till träffar för:
- **EL** vid `elcentral`, `elstammar`, och kortordet `el` **endast** som eget ord (gräns: början/slut av sträng eller icke-bokstav före/efter). Ska **inte** träffa `elisabeth`, `elit` eller liknande — enkel `indexOf("el")` räcker inte för kortordet `el`.
- **VARME** vid `värme`, `fjärrvärme`, `radiator`, `undercentral`

Behåll alla befintliga nyckelord och tester (`gissaGruppTakFasadFonster`, `gissaGruppStammarVentilation`).

Skapa testmetoden `gissaGruppElVarme` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`).

## Exempel (ska finnas som assert i `gissaGruppElVarme`)
- `"Elcentral"` → `EL`
- `"Elisabeths trädgård"` → `null`
- `"Fjärrvärme undercentral"` → `VARME` (fjärrvärme har lägre index än undercentral om båda finns – vid samma index gäller ordningen i listan ovan)
- `"Radiatorer trapphus"` → `VARME`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppElVarme"

## Gör inte
- Ändra andra filer (inklusive befintliga testmetoder utöver att lägga till `gissaGruppElVarme`).
- Lägga till beroenden.
- Ändra datamodellen.
- Lägga filer i repots rot.

utfall: godkänd 2026-10-21 (hel implementation efter försök 2 utan kod från lokala modellen)
