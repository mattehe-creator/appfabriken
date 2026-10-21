beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil `uppgifter/ny/` → `uppgifter/pagar/`; inga ändringar i `GrupperaNamn.kt` eller `GrupperaNamnTest.kt`.
- Uppgiftens kodfiler ligger på rätt sökvägar under `styrelserummet/`; inga filer i repots rot.
- `gissaGrupp` har inte utökats med STAMMAR/VENTILATION; testmetoden `gissaGruppStammarVentilation` saknas.
- Befintligt test `gissaGruppTakFasadFonster` använder JUnit 4 (`org.junit`); inga nya beroenden, behörigheter eller nätverk.
- `:styrelserummet:testDebugUnitTest` grönt lokalt (uppgiftens nya test körs inte eftersom det saknas).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: gissaGrupp för stammar och ventilation

## Vad som saknades
- Grenen innehåller ingen implementation: varken nyckelord i `GrupperaNamn.kt` eller testet `gissaGruppStammarVentilation` i `GrupperaNamnTest.kt`.
- Endast uppgiftsfilen flyttades till `uppgifter/pagar/` (körskriptets startsteg; räcker inte som PR-innehåll).
- `gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppStammarVentilation"` kan inte köras eftersom testmetoden saknas.

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-01-gissa-grupp-tak-fasad-fonster-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt (befintligt test `gissaGruppTakFasadFonster` ska ligga kvar)

## Uppgift
Utöka listan `nyckelord` och behåll samma logik i `gissaGrupp` (gemener, lägst index vinner vid flera träffar; vid samma index längre nyckelord vinner enligt befintlig kod).

Lägg till träffar för:
- **STAMMAR** vid delsträngarna `stammar`, `avlopp`, `vattenledning`
- **VENTILATION** vid `ventilation`, `ftx`, `frånluft`, `tilluft` (strängen `tilluft` ska träffa t.ex. «Från- och tilluft» i gemener)

Ta inte bort befintliga nyckelord för TAK, FASAD och FONSTER_DORRAR.

## Exempel
- `"Stamledning avlopp"` → `STAMMAR`
- `"FTX-aggregat"` → `VENTILATION`
- `"Från- och tilluft"` → `VENTILATION`
- `"Vattenledning kök"` → `STAMMAR`

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*GrupperaNamnTest.gissaGruppStammarVentilation"

Lägg till testmetoden `gissaGruppStammarVentilation` med `assertEquals` för alla fyra exemplen ovan. Använd `import org.junit.Test` och `import org.junit.Assert.assertEquals`.

## Gör inte
- Ändra andra filer (inga `uppgifter/`, `granskning/`, `BESLUTSLOGG.md` eller rotfiler).
- Lägga till beroenden.
- Ändra datamodellen.

## Frågor till Claude
- inga
