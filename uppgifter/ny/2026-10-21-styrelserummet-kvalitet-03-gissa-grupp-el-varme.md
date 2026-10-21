# Uppgift: gissaGrupp för el och värme

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-02-gissa-grupp-stammar-ventilation-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka `gissaGrupp` med:
- **EL** vid `elcentral`, `elstammar`, och kortordet `el` **endast** som eget ord (gräns: början/slut av sträng eller icke-bokstav före/efter). Ska **inte** träffa `elisabeth`, `elit` eller liknande.
- **VARME** vid `värme`, `fjärrvärme`, `radiator`, `undercentral`

## Exempel
- `"Elcentral"` → `EL`
- `"Elisabeths trädgård"` → `null`
- `"Fjärrvärme undercentral"` → `VARME` (fjärrvärme har lägre index än undercentral om båda finns – använd ordningen i listan ovan vid samma index)
- `"Radiatorer trapphus"` → `VARME`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppElVarme"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
