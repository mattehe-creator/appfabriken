# Uppgift: gissaGrupp för tak, fasad och fönster

## Vad som saknades
- Försök 1 ändrade många filer utanför listan (bland annat `BESLUTSLOGG.md`, `STATUS.md`, `uppdrag/*`, `uppgifter/ny/*`). En uppgift får bara röra filerna under «Ändra bara dessa filer».
- Enhetstestet använde JUnit Jupiter (`org.junit.jupiter`), som projektet inte har på classpath; använd `org.junit` som i `ModellTest.kt` och andra tester i `:styrelserummet`.
- Bygget failade på `compileTestKotlin` tills testimports var rätt.

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt (ny eller befintlig på grenen)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt (ny eller befintlig på grenen)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/modell/ModellTest.kt (JUnit 4-import)

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
gradle :styrelserummet:testDebugUnitTest --tests "*GrupperaNamnTest.gissaGruppTakFasadFonster"

Använd `import org.junit.Test` och `import org.junit.Assert.assertEquals`.

## Gör inte
- Ändra andra filer (inga `BESLUTSLOGG.md`, `STATUS.md`, `uppdrag/`, `uppgifter/`).
- Lägga till beroenden.
- Ändra datamodellen.
