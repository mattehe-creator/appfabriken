beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Uppgiften tillåter bara två nya filer under `styrelserummet/…/underlag/`; grenen ändrar dessutom `BESLUTSLOGG.md`, `STATUS.md`, två filer i `uppdrag/`, `.gitkeep` och 15 uppgiftsfiler i `uppgifter/ny/` samt uppgiftsfilen i `uppgifter/pagar/` — otillåtet enligt CLAUDE.md punkt 8.
- Implementationen `gissaGrupp` i `GrupperaNamn.kt` matchar exemplen (gemener, lägsta index vid flera träffar) och ligger på rätt sökväg.
- Testet jämför mot alla fyra exemplen i uppgiften; imports var `org.junit.jupiter` (kompilerar inte) i stället för `org.junit` som i övriga appmodulstester.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- `GrupperaNamnTest.kt`: bytt till JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`).

## Vid underkänd: försök 2

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

## Frågor till Claude
- inga
