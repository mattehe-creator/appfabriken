# Uppgift: meddelande efter «Markera som gjord» vid gräns

uppdrag: uppdrag/2026-10-16-husloggen-pro-gratisgrans.md
försök: 1
beror-på: 2026-10-16-25-pro-strangar.md, cursor:2026-10-16-husloggen-pro-gratisgrans

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt (`HusUiMeddelande`)
- husloggen/src/main/res/values/strings.xml

## Uppgift
`HusViewModel` skickar `HusUiMeddelande.UppgiftMarkeradGjordPostEjSparad` när uppgiften flyttades men ingen loggpost sparades (gräns utan Pro). Utöka `LaunchedEffect` för `viewModel.meddelanden` i `MainActivity`:

- `UppgiftMarkeradGjord` → befintlig snackbar (`uppgift_markerad_gjord`).
- `UppgiftMarkeradGjordPostEjSparad` → snackbar med `uppgift_gjord_post_ej_sparad`.

## Exempel
- 15 poster, markera uppgift som gjord → snackbar om att uppgiften flyttades men posten inte sparades utan Pro; antalet poster oförändrat.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusRepositoryTest"

## Gör inte
- Ändra ViewModel eller repository (Cursor äger gränslogiken).
- Ändra andra filer.
