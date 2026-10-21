# Uppgift: gissaGrupp för stammar och ventilation

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-01-gissa-grupp-tak-fasad-fonster-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka `gissaGrupp` med:
- **STAMMAR** vid `stammar`, `avlopp`, `vattenledning`
- **VENTILATION** vid `ventilation`, `ftx`, `frånluft`, `tilluft`, `från-/tilluft` (matcha även `från`/`tilluft` som del av `från- och tilluft`)

Behåll tidigare grupper och samma regel vid flera träffar (lägst index i namnet vinner).

## Exempel
- `"Stamledning avlopp"` → `STAMMAR`
- `"FTX-aggregat"` → `VENTILATION`
- `"Från- och tilluft"` → `VENTILATION`
- `"Vattenledning kök"` → `STAMMAR`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppStammarVentilation"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
