# Uppgift: Bekräftelse vid borttagning av bostad (flytt eller radera)

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 2
beror-på: 2026-10-25-09-bostad-pa-oversikt-f2.md

## Vad som saknades
PR #704 innehöll ingen kod — bara flytt av uppgiftsfilen till `uppgifter/pagar/`. Du måste committa ändringar i filerna nedan på grenen `lokal/2026-10-25-10-bostad-ta-bort-flytt-dialog` så att dialogen och callback-kedjan matchar uppgiften.

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt (`taBortBostad`, `bostadRaderingInfo`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/HusRepository.kt (`BostadBorttagningSatt`, `taBortBostad`)

## Uppgift

### 1. `BostadSkarmar.kt` — `BostaderInstallningSektion`

Utöka callback-typen:

onTaBortBostad: (
    Bostad,
    BostadBorttagningSatt,
    flyttTillId: Long?,
    onSista: () -> Unit,
    onBorttagen: () -> Unit,
) -> Unit

Importera `se.tmconnect.husloggen.data.BostadBorttagningSatt`.

När användaren trycker ta bort i formuläret och `hamtaRaderingInfo(bostad.id) == 0`: behåll en enkel bekräftelsedialog som idag, men anropa vid bekräftelse:

`onTaBortBostad(bostad, BostadBorttagningSatt.RADERA_INNEHALL, null, onSista, onBorttagen)`.

När `hamtaRaderingInfo(bostad.id) > 0`: ersätt dagens enkla “ta bort och N poster”-dialog med en dialog där användaren **måste välja**:

1. **Flytta till annan bostad** — visa radioknappar för alla **andra** bostäder i `bostader` (inte den som ska bort). Vid bekräftelse: `onTaBortBostad(bostad, BostadBorttagningSatt.FLYTTA_TILL, valdFlyttTillId, ...)`.
2. **Radera allt innehåll** — tydlig varningstext (strängresurs). Vid bekräftelse: `onTaBortBostad(bostad, BostadBorttagningSatt.RADERA_INNEHALL, null, ...)`.

Bekräftelseknappen ska vara disabled tills valet är giltigt (flytt kräver vald målbostad). Använd `stringResource` för all ny text.

### 2. `HusSkarmar.kt` — `HusStartSkarm`

Uppdatera parametern `onTaBortBostad` till samma femargument-signatur som ovan och skicka den oförändrat till `BostaderInstallningSektion`.

### 3. `MainActivity.kt`

Ersätt nuvarande `onTaBortBostad`-lambda som alltid skickar `RADERA_INNEHALL` med direkt vidarebefordran:

onTaBortBostad = { bostad, satt, flyttTillId, onSista, onBorttagen ->
    viewModel.taBortBostad(
        bostad,
        satt,
        flyttTillId,
        onSista = onSista,
        onBorttagen = onBorttagen,
    )
},

### 4. Strängar (engelska + svenska)

Lägg till resurser för dialogrubriker/alternativ, varning vid radera, och fråga som visar antal kopplade poster (använd `bekraftAntal` / `%d` eller `%1$d` där det passar). Behåll befintliga `bostad_ta_bort*` om de fortfarande används.

## Exempel
- Bostad "Stuga" har 3 kopplade rader, bostad "Mitt hem" finns: användaren väljer flytt till Mitt hem → Stugan försvinner, posterna har `bostad_id` för Mitt hem.
- Samma setup, användaren väljer radera → posterna försvinner.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusRepositoryBostadTest.taBortBostad*"

## Gör inte
- Ändra repository eller migrationer.
- Lägga till beroenden.
- Ändra andra filer.
