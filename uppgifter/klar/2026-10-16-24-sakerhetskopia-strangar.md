# Uppgift: strängar för säkerhetskopia

uppdrag: uppdrag/2026-10-16-husloggen-sakerhetskopia.md
försök: 1
beror-på: cursor:2026-10-16-husloggen-sakerhetskopia-v2

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt
- garantivalvet/src/main/res/values/strings.xml (`sakerhetskopia_*`)

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `sakerhetskopia_rubrik` | Rubrik «Säkerhetskopia» / «Backup» |
| `sakerhetskopia_beskrivning` | Filen innehåller alla uppgifter, poster och bilder, sparas var användaren vill, **inte krypterad** |
| `sakerhetskopia_spara` | Knapp «Spara säkerhetskopia» / «Save backup» |
| `sakerhetskopia_hamta` | Knapp «Hämta från säkerhetskopia» / «Restore from backup» |
| `sakerhetskopia_import_beskrivning` | Import **lägger bara till**, skriver inte över befintliga poster |
| `sakerhetskopia_sparad` | «Säkerhetskopia sparad» / «Backup saved» |
| `sakerhetskopia_importerad` | «Importerade %1$d poster, hoppade över %2$d» / «Imported %1$d log entries, skipped %2$d» (två heltalsplatshållare) |
| `sakerhetskopia_kunde_inte_lasas` | «Filen kunde inte läsas» / «Could not read the file» |
| `sakerhetskopia_nyare_version` | «Filen kommer från en nyare version av appen» / «The file is from a newer version of the app» |

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `sakerhetskopia_spara` är «Spara säkerhetskopia».

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-16 (svenska sakerhetskopia-strängar: borttagna markdown-asterisker **, meningar enligt garantivalvet-förebild)
