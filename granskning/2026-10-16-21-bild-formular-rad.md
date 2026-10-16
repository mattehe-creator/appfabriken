beslut: godkänd
granskare: cursor
datum: 2026-10-16

## Kontrollerat
- Endast tillåtna filer ändrade (`PostBildFalt.kt`, `PostFormularSkarm.kt`, `MainActivity.kt`) plus uppgiftsflytt `ny/` → `pagar/` från lokala modellen; vid godkännande `pagar/` → `klar/`.
- `PostBildFalt` visar miniatyrer via FileProvider-URI, knappar Välj bild/Ta foto med minst 48 dp, borttagning med `bild_ta_bort`, inaktiverade lägg-till-knappar och `bild_max_antal` när `kanLäggaTillBild` är false.
- `PostFormularSkarm` kopplar `PostBildFalt` till `PostFormularTillstand` och `bildUri` från ViewModel; `MainActivity` registrerar `PickVisualMedia`/`TakePicture` enligt uppgiften.
- Inga nya beroenden, manifestbehörigheter eller nätverk.
- UI-text från `strings.xml`; inga hårdkodade användarsträngar i composables.
- Angivna enhetstester använder JUnit 4 (`org.junit`) och går igenom lokalt.

## Rättat av Cursor
- Kompilationsfel: `PickVisualMediaRequest`, borttagna ogiltiga ikoner/`androidx.core`-resurs och icke-existerande strängnyckel.
- Placeholders i `PostFormularSkarm` (alltid `kanLaggaTill = true`, `bildUri` alltid null).
- `PostBildFalt`: miniatyrer från URI (mönster från reklamationsbilder), 48 dp tryckytor, korrekt max-antal-text.
- `MainActivity`: sparar kamera-URI före intent (som i Garantivalvet) i stället för att anropa `skapaKameraFotoUri()` igen i callback.

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
