# Uppgift: säkerhetskopia i inställningar

uppdrag: uppdrag/2026-10-16-husloggen-sakerhetskopia.md
försök: 1
beror-på: 2026-10-16-24-sakerhetskopia-strangar.md, cursor:2026-10-16-husloggen-sakerhetskopia-v2

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/InstallningarSkarm.kt (säkerhetskopia-avsnittet)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt

## Uppgift
Utöka `InstallningarSkarm` med ett avsnitt för manuell säkerhetskopia (gratis, ingen Pro-dialog):

1. Rubrik `sakerhetskopia_rubrik` och beskrivning `sakerhetskopia_beskrivning` (bodySmall, onSurfaceVariant).
2. Text `sakerhetskopia_import_beskrivning`.
3. Två knappar med minst 48 dp tryckyta: `sakerhetskopia_spara` och `sakerhetskopia_hamta`.
4. Parametrar `onSparaSakerhetskopia: () -> Unit` och `onHamtaSakerhetskopia: () -> Unit`.

`MainActivity` skickar tomma callbacks `{}` tills uppgift 26/30 kopplar dem.

## Exempel
- Inställningar visar avsnittet under versionsraden.
- Tryck på Spara anropar callback (tom tills uppgift 26).

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Implementera filväljare eller ZIP här.
- Pro-gate eller nya behörigheter.
