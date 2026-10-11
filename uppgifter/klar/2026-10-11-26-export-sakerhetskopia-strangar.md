# Uppgift: strängar för säkerhetskopia

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt
- garantivalvet/src/main/res/values/strings.xml (export_pdf m.fl.)

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `sakerhetskopia_rubrik` | Rubrik «Säkerhetskopia» / «Backup» |
| `sakerhetskopia_beskrivning` | Förklaring: filen innehåller alla köp och kvitton, sparas var användaren vill, **inte krypterad** |
| `sakerhetskopia_spara` | Knapp «Spara säkerhetskopia» / «Save backup» |
| `sakerhetskopia_hamta` | Knapp «Hämta från säkerhetskopia» / «Restore from backup» |
| `sakerhetskopia_import_beskrivning` | Kort text: import **lägger bara till**, skriver inte över befintliga köp |
| `sakerhetskopia_sparad` | «Säkerhetskopia sparad» / «Backup saved» |
| `sakerhetskopia_importerad` | «Importerade %1$d köp, hoppade över %2$d» / «Imported %1$d purchases, skipped %2$d» (två heltalsplatshållare) |
| `sakerhetskopia_kunde_inte_lasas` | «Filen kunde inte läsas» / «Could not read the file» |
| `sakerhetskopia_nyare_version` | «Filen kommer från en nyare version av appen» / «The file is from a newer version of the app» |

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `sakerhetskopia_spara` är «Spara säkerhetskopia».

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-11 (inget)
