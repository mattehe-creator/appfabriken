# Uppgift: gissaGrupp för tak, fasad och fönster

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
`fun gissaGrupp(namn: String): Komponentgrupp?`

Jämför mot namnet i gemener (`namn.lowercase()`). Nyckelord får förekomma var som helst i namnet; vanlig svensk böjning ska träffa (t.ex. `fönster` i `Nya fönster väster`).

Implementera träffar för:
- **TAK** vid `yttertak`, `takpapp`, `tegel` (när det avser tak, t.ex. `Tegeltak`)
- **FASAD** vid `fasad`, `puts`, `fasadtegel`
- **FONSTER_DORRAR** vid `fönster`, `balkongdörr`, `entrédörr`

Om flera grupper matchar, välj den grupp vars nyckelord hittas först i namnet (lägst index). Övriga grupper kommer i senare uppgifter.

## Exempel
- `"Yttertak papp 2018"` → `TAK`
- `"Putsad fasad söder"` → `FASAD`
- `"Entrédörr aluminium"` → `FONSTER_DORRAR`
- `"Stomme i betong"` → `null` (stomme är ingen nyckel här)

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppTakFasadFonster"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: underkänd 2026-10-21 av Cursor (PR #325). Ersatt av 2026-10-21-styrelserummet-kvalitet-01-gissa-grupp-tak-fasad-fonster-f2.md
