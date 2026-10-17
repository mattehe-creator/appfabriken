# Uppgift: flik Sammanställning

uppdrag: uppdrag/2026-10-17-husloggen-forsaljning.md
försök: 2
beror-på: 2026-10-17-01-sammanstallning-strangar.md, cursor:2026-10-17-husloggen-forsaljning

## Vad som saknades
- Projektet kompilerar inte: `items(arPoster)` ligger inuti `item { }` i `SammanstallningSkarm.kt`; byt till samma mönster som `LoggFlik` (t.ex. `item` för årskort, sedan `items(arPoster, key = { it.id })` direkt i `LazyColumn`-blocket, inte nästlade `items` inuti `item`).
- Fel strängresurser: använd `R.string.sammanstallning_forbattring_ja`, `sammanstallning_forbattring_bedomning`, `sammanstallning_forbattring_nej` (som i `MainActivity.forbattringTillResId`), inte `forbattring_ja` m.fl.
- Fliken visar inget innehåll: i `HusStartSkarm`, gren `HusFlik.SAMMANSTALLNING`, anropa `SammanstallningSkarm` med `poster`, `forsaljningsAr`, `onForsaljningsArAndras`, `locale`, och `visaSkatteInfo`. Sätt `visaSkatteInfo` i `HusSkarmar.kt` med `arSverigeForSkatteInfo(locale.country)` — ändra inte `MainActivity.kt`.
- `SammanstallningSkarm` ska inte ha egen `Scaffold`, `TopAppBar` eller `onTillbaka`; signatur enligt uppgiften. Flikens appbar kommer från `HusStartSkarm`.
- Stegare/dropdown: inga hårdkodade `"-"`/`"+"` i UI — lägg strängar i `strings.xml` om de ska synas, eller använd ikoner med `contentDescription` från strängar.
- Test: asserta synliga etiketter för exemplen (svenska locale): reparation 2021-06-01 → «Kräver bedömning», 2020-12-31 → «Nej», ombyggnad 2015 → text från `sammanstallning_forbattring_ja`. Använd `onNodeWithText` mot resursvärdena, inte bara klick på årtal. Bygg `Post` enligt `Post.kt` (`bilder: String? = null`, `utforare`, etc.).

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarmTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt (`HusFlik`, loggfliken)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForsaljningSammanstallning.kt (`summeraPerAr`, `kanVaraForbattring`, `arStodjerGransbelopp`, `arSverigeForSkatteInfo`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularFalt.kt (`postTypEtikett`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt (`forbattringTillResId` — strängnycklar)

## Uppgift
1. Lägg till flik `SAMMANSTALLNING` i `HusFlik` och i `NavigationBar` med `flik_sammanstallning`.
2. **`SammanstallningSkarm`:** Composable som tar `poster: List<Post>`, `forsaljningsAr: Int`, `onForsaljningsArAndras: (Int) -> Unit`, `locale: Locale`, `visaSkatteInfo: Boolean` (inga fler parametrar).
3. Lista kalenderår (fallande) som finns i loggen. Per år: summor per `PostTyp` via `summeraPerAr`, totalsumma, och om `arStodjerGransbelopp(poster, ar)` visa `sammanstallning_grans_ar` annars `sammanstallning_grans_ar_ej` (bara när `visaSkatteInfo`).
4. Under varje år, lista poster med datum, beskrivning, typ, belopp och etikett från `kanVaraForbattring(post, forsaljningsAr)` mappad till `sammanstallning_forbattring_*` när `visaSkatteInfo`; annars bara postrad utan bedömning.
5. Välj försäljningsår med stegare eller dropdown (minst 48 dp tryckyta). Standardår kommer från `HusStartSkarm` (`forsaljningsAr` / innevarande år).
6. Nederst: informationsruta med `sammanstallning_skatte_info_rubrik` och `sammanstallning_skatte_info_text` när `visaSkatteInfo` är sant.
7. I `HusStartSkarm`, visa `SammanstallningSkarm` när sammanställningsfliken är vald.
8. `SammanstallningSkarmTest`: Compose-test med exempelposter — reparation 2021-06-01 visar «Kräver bedömning», 2020-12-31 visar «Nej», ombyggnad 2015 visar etikett från `sammanstallning_forbattring_ja` (försäljningsår 2026, `visaSkatteInfo = true`, svenskt locale).

## Exempel
- Försäljningsår 2026, post reparation 2021-06-01 → etikett «Kräver bedömning».
- Försäljningsår 2026, post reparation 2020-12-31 → «Nej».
- Ombyggnad 2015 → strängen `sammanstallning_forbattring_ja`.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SammanstallningSkarmTest"

## Gör inte
- Ändra `HusViewModel.kt` eller `MainActivity.kt` (export och state sköts av Cursor).
- Lägga till beroenden.
- Ändra databasschemat.
- Använda kotlin.test — använd org.junit som i övriga appmodultester.

utfall: godkänd 2026-10-17 (fel sökvägar i rot, LazyColumn items, imports, test Robolectric/strängresurser, innerPadding)
