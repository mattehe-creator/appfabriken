beslut: underkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Grenen ändrade fler filer än uppgiften tillåter: `FRAGA.md` i rot och hundratals orelaterade svenska strängar i `values-sv/strings.xml`.
- Kod låg på rätt sökvägar (inte i repots rot).
- Bygget failade: saknad import av `BostadBorttagningSatt` i `HusSkarmar.kt` och extra `}` i slutet av samma fil.
- `MainActivity.kt` och callback-kedjan matchar i stort uppgiftens signatur, men UI-delen i `BostadSkarmar.kt` sätter `visaFlyttDialog` som sidoeffekt under komposition (`visaFlyttDialog = bostad` i `else`-grenen) i stället för `LaunchedEffect` eller direkt state — det följer inte Compose-regler och riskerar fel dialogbeteende.
- Flyttmål lagras som `Int` (`malBostad.id.toInt()`) i stället för `Long?`.
- Huvudalternativen (flytta / radera) har RadioButton och etikett i separata komponenter utan gemensam `Row`/`selectableGroup`, sämre tillgänglighet än övriga skärmar i appen.
- Uppgiften kräver tydlig varningstext vid radera; endast kort etikettsträng finns, ingen separat varning under valet.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen hårdkodad användartext i UI (ny text via `stringResource`).
- Testet `*HusRepositoryBostadTest.taBortBostad*` går efter Cursors kompileringsrättningar (repository oförändrat).
- `FRAGA.md` fanns: modellen frågade i stället för att implementera trots att uppgiften beskriver dialogen.

## Rättat av Cursor
- Import `BostadBorttagningSatt` i `HusSkarmar.kt`.
- Extra avslutande `}` borttagen i `HusSkarmar.kt` (rad 766).
- `values-sv/strings.xml`: orelaterade massändringar återställda till main; endast de fyra nya bostad-borttagningssträngarna tillagda (matchar engelska).
- `FRAGA.md` borttagen från grenen.

## Vid underkänd: försök 2

```
# Uppgift: Bekräftelse vid borttagning av bostad (flytt eller radera)

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 3
beror-på: 2026-10-25-09-bostad-pa-oversikt-f2.md

## Vad som saknades
- `FRAGA.md` i rot i stället för färdig kod. Svar: använd **Material3 `AlertDialog`**. I `text`: en `Column` med befintlig bekräftelsetext (`bostad_ta_bort_bekraftelse` med namn och antal), sedan två huvudval som var sin **`Row`** med `RadioButton` + `Text` (minst 48 dp tryckyta, gärna `Modifier.clickable` på raden). När användaren valt **Flytta till annan bostad**: visa radioknappar i `Row` per **annan** bostad i listan (`Long`-id, t.ex. `var valdFlyttTillId by remember { mutableStateOf<Long?>(null) }`). När **Radera allt innehåll** valts: visa extra varningstext från en **ny** strängresurs (t.ex. `bostad_ta_bort_radera_varning`). Bekräftelseknapp **disabled** tills valet är giltigt (flytt kräver vald målbostad). Öppna flytt-dialogen med `LaunchedEffect(bekafftaBorttagning, bekafftaAntal)` eller motsvarande — **inte** genom tilldelning `visaFlyttDialog = bostad` inuti `@Composable`-gren. Nollställ `bekafftaBorttagning` när flytt-dialogen visas.
- Bygget failade: import av `BostadBorttagningSatt` saknades i `HusSkarmar.kt`; extra `}` i slutet av filen.
- `values-sv/strings.xml` omskrevs i stort — ändra **bara** de nya strängarna, inte övriga översättningar.

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt (`taBortBostad`, `bostadRaderingInfo`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/data/HusRepository.kt (`BostadBorttagningSatt`, `taBortBostad`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt (befintliga `Row` + `RadioButton`-mönster)

## Uppgift

### 1. `BostadSkarmar.kt` — `BostaderInstallningSektion`

Callback-typ:

onTaBortBostad: (
    Bostad,
    BostadBorttagningSatt,
    flyttTillId: Long?,
    onSista: () -> Unit,
    onBorttagen: () -> Unit,
) -> Unit

Importera `se.tmconnect.husloggen.data.BostadBorttagningSatt`.

När användaren trycker ta bort och `hamtaRaderingInfo(bostad.id) == 0`: enkel bekräftelsedialog som idag; vid bekräftelse:
`onTaBortBostad(bostad, BostadBorttagningSatt.RADERA_INNEHALL, null, onSista, onBorttagen)`.

När `hamtaRaderingInfo(bostad.id) > 0`: **AlertDialog** där användaren måste välja flytta (med målbostad) eller radera (med varningstext). Vid bekräftelse anropa `onTaBortBostad` med rätt `BostadBorttagningSatt` och `flyttTillId`.

### 2. `HusSkarmar.kt` — `HusStartSkarm`

Samma femargument-signatur på `onTaBortBostad`; skicka oförändrat till `BostaderInstallningSektion`. Importera `BostadBorttagningSatt`.

### 3. `MainActivity.kt`

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

Lägg till/uppdatera resurser för dialog, alternativ, varning vid radera och antal kopplade poster. Lägg **inte** om övriga strängar i `values-sv`.

## Exempel
- Bostad "Stuga" har 3 kopplade rader, bostad "Mitt hem" finns: användaren väljer flytt till Mitt hem → Stugan försvinner, posterna har `bostad_id` för Mitt hem.
- Samma setup, användaren väljer radera → posterna försvinner.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusRepositoryBostadTest.taBortBostad*"

## Gör inte
- Skriv `FRAGA.md` — implementera enligt ovan.
- Ändra repository eller migrationer.
- Lägga till beroenden.
- Ändra andra filer eller massändra befintliga svenska strängar.
```

## Frågor till Claude
- inga
