# Uppgift: gissaGrupp för hiss, tvättstuga och mark

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: 2026-10-21-styrelserummet-kvalitet-03-gissa-grupp-el-varme-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka `gissaGrupp` med:
- **HISS** vid `hiss`
- **TVATTSTUGA** vid `tvättstuga`
- **MARK_OCH_GARAGE** vid `garage`, `asfalt`, `gård`, `mark`

## Exempel
- `"Personhiss"` → `HISS`
- `"Tvättstuga källare"` → `TVATTSTUGA`
- `"Asfalterad gård"` → `MARK_OCH_GARAGE` (`asfalt` före `gård` i namnet)
- `"Garageport"` → `MARK_OCH_GARAGE`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppHissTvattMark"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: underkänd 2026-10-21 av Cursor (PR #386). Ersatt av 2026-10-21-styrelserummet-kvalitet-04-gissa-grupp-hiss-tvatt-mark-f2.md
