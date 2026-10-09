beslut: godkänd
granskare: cursor
datum: 2026-10-09

## Kontrollerat
- Endast `KopFormularSkarm.kt` och `MainActivity.kt` ändrade enligt uppgiften; ingen `KvittoHelskarm.kt`; uppgiftsfil flyttad från `pagar/` till `klar/`.
- `KopFormularSkarm` har parametrarna `kvittoUri`, `onValjBild`, `onTaFoto`, `onTaBortKvitto`, knappar/strängar enligt spec, förhandsvisning ca 120 dp med `ContentScale.Crop`, ta-bort-knapp när URI finns.
- `MainActivity` registrerar `PickVisualMedia` och `TakePicture`, kopplar ViewModel (`kvittoUriForFormular`, `sattTillfalligtKvitto`, `skapaKameraKvittoUri`, `markeraKvittoForBorttagning`) till både lägg-till- och ändra-formulär; ingen helskärm/dela/`onOppnaKvitto`.
- `FormularValideringTest` (JUnit 4) grönt; inga nya beroenden, behörigheter eller nätverk; UI-texter från `strings.xml`.
- Testerna jämför validering mot konkreta exempel (befintligt beteende i uppgiften); kvitto-UI testas inte separat i denna uppgift.

## Rättat av Cursor
- Stängning av antecknings-`OutlinedTextField` (`)` i stället för `}`).
- Bildval: `PickVisualMediaRequest(PickVisualMedia.ImageOnly)` i stället för `launch(null)`.
- Kamera: spara URI i fältvariabel och `launch(uri)` med icke-null `Uri`.
- Miniatyr: `LocalContext` + `contentResolver.openInputStream` och `remember(kvittoUri)` (ingen Coil).

## Frågor till Claude
- inga
