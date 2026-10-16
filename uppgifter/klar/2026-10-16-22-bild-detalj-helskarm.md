# Uppgift: miniatyrer, helskärm och dela

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: 2026-10-16-20-bild-strangar.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BildHelskarm.kt (ny)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostDetaljSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt

## Uppgift
1. **`PostDetaljSkarm`**: rad med miniatyrer för postens bilder (via `viewModel`/callbacks: filnamnlista och URI). Tryck öppnar helskärm på vald index.
2. **`BildHelskarm`**: fullskärm med svepning mellan bilder (`HorizontalPager` eller motsvarande), pinch/dubbeltryck-zoom, top bar med **Stäng** (`bild_stang`) och **Dela** (`bild_dela`). Dela: `Intent.ACTION_SEND`, `image/jpeg`, FileProvider-URI, `FLAG_GRANT_READ_URI_PERMISSION`.
3. **`MainActivity`**: navigering till `BildHelskarm` när ViewModel signalerar aktiv bildvy (t.ex. tillstånd eller callback från `HusViewModel`).

Minsta tryckyta 48 dp på knappar.

## Exempel
- Post med två bilder → svep byter bild i helskärm.
- Dela öppnar systemets delningsmeny.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra repository eller lagret.
- Lägga till beroenden.
- Nya manifestbehörigheter (FileProvider finns redan).

utfall: underkänd 2026-10-16 av Cursor (PR #190). Ersatt av 2026-10-16-22-bild-detalj-helskarm-f2.md
