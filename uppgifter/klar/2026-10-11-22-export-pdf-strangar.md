# Uppgift: strängar för PDF-export

uppdrag: uppdrag/2026-10-11-export-pdf.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/res/values/strings.xml
- garantivalvet/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/StringsNycklarTest.kt
- garantivalvet/src/main/res/values/strings.xml (befintliga etiketter för fält)

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `underlag_rubrik` | Rubrik i PDF «Reklamationsunderlag» / «Claim document» |
| `underlag_vad` | Etikett «Vad» / «What» i PDF |
| `underlag_var_kopt` | «Var köpt» / «Where purchased» |
| `underlag_kopdatum` | «Köpdatum» / «Purchase date» |
| `underlag_pris` | «Pris» / «Price» |
| `underlag_garanti_manader` | «Garantitid (månader)» / «Warranty (months)» |
| `underlag_garanti_slut` | «Garanti gäller till» / «Warranty valid until» |
| `underlag_reklamation_slut` | «Reklamationsrätt till» / «Right to claim until» |
| `underlag_presumtion_slut` | «Presumtion till» / «Presumption until» |
| `underlag_reklamation_status` | «Reklamationsstatus» / «Claim status» |
| `underlag_status_fel_antas_funnits` | Värde när felet antas ha funnits vid köpet |
| `underlag_status_galler` | Värde när reklamationsrätten gäller (efter presumtion) |
| `underlag_status_utgangen` | Värde när reklamationsfristen gått ut |
| `underlag_anteckning` | «Anteckning» / «Notes» |
| `underlag_skapad` | «Underlag skapat» / «Document created» |
| `export_pdf` | Knapp «Exportera PDF» / «Export PDF» |
| `export_dela` | Knapp «Dela» / «Share» (dela PDF) |
| `export_pdf_sparad` | «PDF sparad» / «PDF saved» |
| `export_pdf_misslyckades` | «PDF kunde inte skapas» / «Could not create PDF» |

Statusvärdena ska motsvara `ReklamationStatus` i PDF-innehållet (kort, utan juridik).

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `export_pdf` är «Exportera PDF».

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-11 (inget)
