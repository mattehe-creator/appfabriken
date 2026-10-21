# Uppgift: forslagFor nyttjandeperiod

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslag.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslagTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt
- styrelserummet/src/main/resources/nyttjandeperioder.csv

## Uppgift
`fun forslagFor(grupp: Komponentgrupp, tabell: List<NyttjandeperiodRad>): Int?`

- Hitta raden i `tabell` med samma `grupp`.
- Returnera `forslagAr` från raden.
- För `MARK_OCH_GARAGE`, returnera alltid `null` (tabellens noll är ett förslag om andel, inte nyttjandeperiod i år).
- Saknas rad → `null`.

Använd `lasNyttjandeperioderFranResurs()` i test för verklig tabell.

## Exempel
- `forslagFor(Komponentgrupp.TAK, lasNyttjandeperioderFranResurs())` → `50`
- `forslagFor(Komponentgrupp.STAMMAR, tabell)` → `50`
- `forslagFor(Komponentgrupp.MARK_OCH_GARAGE, tabell)` → `null`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyttjandeperiodForslagTest.forslagForGrupp"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `nyttjandeperioder.csv` eller datamodellen.

utfall: godkänd 2026-10-21 (JUnit 4-import och saknad-rad-test mot minimal tabell)
