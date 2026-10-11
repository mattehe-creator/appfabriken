# Uppgift: säkerhetskopia i listskärmens topp

uppdrag: uppdrag/2026-10-11-export-sakerhetskopia.md
försök: 1
beror-på: 2026-10-11-26-export-sakerhetskopia-strangar.md, cursor:2026-10-11-export-sakerhetskopia

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopViewModel.kt (Pro-dialog, `visaProDialog`)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt (Pro-gate på export)

## Uppgift
Under påminnelsereglaget i `KopListaSkarm`, lägg till en sektion för säkerhetskopia:

1. Rubrik `sakerhetskopia_rubrik` och beskrivning `sakerhetskopia_beskrivning` (bodySmall för beskrivning).
2. Text `sakerhetskopia_import_beskrivning` under beskrivningen.
3. Två knappar (minst 48 dp tryckyta vardera): `sakerhetskopia_spara` och `sakerhetskopia_hamta`.
4. **Pro-gate:** Om `arPro` är false ska båda knapparna anropa `onVisaProDialog()` i stället för spara/hämta-callbacks. Med Pro anropas `onSparaSakerhetskopia()` respektive `onHamtaSakerhetskopia()`.

`MainActivity` skickar tomma callbacks `{}` tills uppgift 28/29 kopplar dem (signaturerna ska finnas redan här).

## Exempel
- Utan Pro, tryck Spara → Pro-dialog.
- Med Pro, tryck Spara → `onSparaSakerhetskopia()` anropas.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Implementera filväljare eller ZIP-logik här.
- Lägga till beroenden eller behörigheter.
utfall: godkänd 2026-10-11 (tog bort oanvända säkerhetskopia-kontrakt i MainActivity)
