# Uppgift: strängar för sammanställning och försäljning

uppdrag: uppdrag/2026-10-17-husloggen-forsaljning.md
försök: 1
beror-på: cursor:2026-10-17-husloggen-forsaljning

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt
- husloggen/src/main/res/values/strings.xml (befintliga `post_typ_*`, `falt_*`)

## Uppgift
Lägg till samma nycklar på engelska och svenska:

| Nyckel | Syfte |
|--------|--------|
| `flik_sammanstallning` | Flik «Sammanställning» / «Summary» |
| `sammanstallning_rubrik` | Rubrik på skärmen |
| `sammanstallning_forsaljningsar` | Etikett för val av försäljningsår |
| `sammanstallning_ar_summa` | Rubrik för ett år med totalsumma (t.ex. «2024 — %1$s») |
| `sammanstallning_typ_summa` | Rad «Typ: belopp» per typ inom året |
| `sammanstallning_totalt` | «Totalt» / «Total» |
| `sammanstallning_forbattring_ja` | «Kan vara förbättringsutgift» / «May be an improvement expense» |
| `sammanstallning_forbattring_bedomning` | «Kräver bedömning» / «Requires assessment» |
| `sammanstallning_forbattring_nej` | «Nej» / «No» |
| `sammanstallning_skatte_info_rubrik` | «Information om förbättringsutgifter» / «Improvement expenses (information)» |
| `sammanstallning_skatte_info_text` | Exakt: «Förbättringsutgifter kan dras av när bostaden säljs. Reparation och underhåll räknas bara för försäljningsåret och de fem åren före. Utgifterna ska sammanlagt vara minst 5 000 kr per år. Det här är information, inte skatteråd. Kontrollera alltid med Skatteverket.» (engelska motsvarighet) |
| `sammanstallning_grans_ar` | «Minst 5 000 kr detta år» / «At least SEK 5,000 this year» när gränsen uppfylls |
| `sammanstallning_grans_ar_ej` | «Under 5 000 kr detta år» / «Below SEK 5,000 this year» |
| `export_hus_historik_pdf` | «Exportera PDF (Husets historik)» / «Export PDF (House history)» |
| `export_hus_historik_csv` | «Exportera CSV» / «Export CSV» |
| `export_pdf_sparad` | «PDF sparad» / «PDF saved» |
| `export_pdf_misslyckades` | «PDF kunde inte skapas» / «Could not create PDF» |
| `export_csv_sparad` | «CSV sparad» / «CSV saved» |
| `export_csv_misslyckades` | «CSV kunde inte skapas» / «Could not create CSV» |
| `historik_pdf_rubrik` | «Husets historik» / «House history» |
| `historik_pdf_skatt_rubrik` | «Skatteinformation (Sverige)» / «Tax information (Sweden)» |
| `historik_pdf_skatt_text` | Samma innehåll som `sammanstallning_skatte_info_text` plus kort lagrumstext utan belopp (Cursor fyller PDF från `HusHistorikTexter`; strängen ska matcha reglerna i `regler/`) |

## Exempel
- `StringsNycklarTest` passerar.
- Svenska `flik_sammanstallning` är «Sammanställning».

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
