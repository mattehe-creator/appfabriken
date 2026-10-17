# Uppgift: flik Sammanställning

uppdrag: uppdrag/2026-10-17-husloggen-forsaljning.md
försök: 1
beror-på: 2026-10-17-01-sammanstallning-strangar.md, cursor:2026-10-17-husloggen-forsaljning

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarm.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt
- husloggen/src/test/kotlin/se/tmconnect/husloggen/ui/SammanstallningSkarmTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusSkarmar.kt (`HusFlik`, loggfliken)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForsaljningSammanstallning.kt (`summeraPerAr`, `kanVaraForbattring`, `arStodjerGransbelopp`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostFormularFalt.kt (`postTypEtikett`)

## Uppgift
1. Lägg till flik `SAMMANSTALLNING` i `HusFlik` och i `NavigationBar` med `flik_sammanstallning`.
2. **`SammanstallningSkarm`:** Composable som tar `poster: List<Post>`, `forsaljningsAr: Int`, `onForsaljningsArAndras: (Int) -> Unit`, `locale: Locale`, `visaSkatteInfo: Boolean`.
3. Lista kalenderår (fallande) som finns i loggen. Per år: summor per `PostTyp` via `summeraPerAr`, totalsumma, och om `arStodjerGransbelopp(poster, ar)` visa `sammanstallning_grans_ar` annars `sammanstallning_grans_ar_ej` (bara när `visaSkatteInfo`).
4. Under varje år, lista poster med datum, beskrivning, typ, belopp och etikett från `kanVaraForbattring(post, forsaljningsAr)` (`Ja` / `Kräver bedömning` / `Nej`) när `visaSkatteInfo`; annars bara postrad utan bedömning.
5. Välj försäljningsår med stegare eller dropdown (minst 48 dp tryckyta). Standardår kommer från `MainActivity`/`HusStartSkarm` (innevarande år).
6. Nederst: informationsruta med `sammanstallning_skatte_info_rubrik` och `sammanstallning_skatte_info_text` när `visaSkatteInfo` är sant.
7. `SammanstallningSkarmTest`: Compose-test med exempelposter — reparation 2021-06-01 visar «Kräver bedömning», 2020-12-31 visar «Nej», ombyggnad 2015 visar «Ja» (försäljningsår 2026).

## Exempel
- Försäljningsår 2026, post reparation 2021-06-01 → etikett «Kräver bedömning».
- Försäljningsår 2026, post reparation 2020-12-31 → «Nej».
- Ombyggnad 2015 → «Ja».

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*SammanstallningSkarmTest"

## Gör inte
- Ändra `HusViewModel.kt` eller `MainActivity.kt` (export och state sköts av Cursor).
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-17 av Cursor (PR #226). Ersatt av 2026-10-17-02-sammanstallning-flik-f2.md
