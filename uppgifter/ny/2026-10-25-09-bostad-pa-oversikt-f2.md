# Uppgift: Bostadshanterare på Översikt

uppdrag: uppdrag/2026-10-25-husloggen-flera-bostader.md
försök: 2
beror-på: cursor:2026-10-25-husloggen-flera-bostader

## Vad som saknades
PR #700 innehöll ingen kod — bara flytt av uppgiftsfilen till `uppgifter/pagar/`. Du måste faktiskt ändra de tre Kotlin-filerna enligt stegen nedan och committa dem på grenen `lokal/2026-10-25-09-bostad-pa-oversikt`.

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BostadSkarmar.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt

## Uppgift

Flytta bostadshanteringen från inställningar till **Översikt**-fliken (`HusFlik.SAMMANSTALLNING`).

### 1. `HusSkarmar.kt` — `HusStartSkarm`

Lägg till samma bostads-callbacks som `InstallningarSkarm` redan tar idag (kopiera signaturerna från `InstallningarSkarm` rad ~615–621):

- `valdBostadId: Long? = null` (eller härled från `valdBostad?.id`)
- `onSparaNyBostad: (String, String, String, (Boolean) -> Unit) -> Unit = { _, _, _, _ -> }`
- `onUppdateraBostad: (Bostad) -> Unit = {}`
- `onTaBortBostad: (Bostad, onSista: () -> Unit, onBorttagen: () -> Unit) -> Unit = { _, _, _ -> }`
- `hamtaRaderingInfo: suspend (Long) -> Int = { 0 }`

I grenen `HusFlik.SAMMANSTALLNING -> { ... }` (idag bara `SammanstallningSkarm(...)`):

- Byt till en `Column(Modifier.fillMaxSize().padding(innerPadding))`.
- **Först:** anropa `BostaderInstallningSektion(...)` med `bostader`, `valdBostadId`, och alla callbacks ovan (samma som i inställningar idag).
- **Därefter:** `SammanstallningSkarm(..., modifier = Modifier.weight(1f))` med samma poster/kommande/export-parametrar som idag.

`BostadValjareRad` i topBar ska vara oförändrad (snabb byte när fler än en bostad).

### 2. `HusSkarmar.kt` — `InstallningarSkarm`

- Ta bort hela anropet till `BostaderInstallningSektion(...)` (och tillhörande bostads-parametrar i funktionssignaturen om de inte längre används).
- Inställningar ska bara visa version, utseende, säkerhetskopia m.m. — **ingen** bostadslista.

### 3. `MainActivity.kt`

- I `HusStartSkarm(...)`: skicka `valdBostadId = valdBostadId`, `onSparaNyBostad = viewModel::sparaNyBostad`, `onUppdateraBostad = viewModel::uppdateraBostad`, samma `onTaBortBostad`/`hamtaRaderingInfo` som idag går till `InstallningarSkarm`.
- I `InstallningarSkarm(...)`: ta bort alla bostads-argument (de ska inte längre finnas på signaturen efter steg 2).

Du behöver **inte** ändra `BostadSkarmar.kt` om du återanvänder `BostaderInstallningSektion` oförändrad. Rör den filen bara om du skapar en tunn wrapper `BostadOversiktSektion` som delegerar till samma composable.

Formuläret ska fortfarande använda `BostadFormularFalt` med **namn**, **adress** och **anteckning** (redan i `BostaderInstallningSektion`).

## Exempel
- Användaren öppnar Översikt: sektionen "Bostäder" med radioknappar, knapparna Lägg till och Ändra syns, ovanför sammanställningen.
- Användaren öppnar Inställningar: ingen bostadslista längre.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*HusNavigeringTest*"

Kör detta lokalt innan du pushar.

## Gör inte
- Ändra databasschemat eller `HusRepository`.
- Lägga till beroenden.
- Ändra andra filer.
- Lägg filer i repots rot.
