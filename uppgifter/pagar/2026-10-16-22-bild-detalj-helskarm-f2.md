# Uppgift: miniatyrer, helskärm och dela

## Vad som saknades
- Kompileringsfel: sträng- och drawable-nycklar som inte finns i projektet (`falt_bilder`, `bild_fornhandsvisning`, `bild_miniatyr`, `placeholder_image`). Använd **bara** befintliga `bild_*`-nycklar från uppgift 20 (t.ex. `bild_har_bilder` som innehållsbeskrivning på miniatyr och helskärmsbild).
- `post.bilder` är `String?` (CSV), inte en lista. Parsa med `se.tmconnect.husloggen.regler.tolkaBilder(post.bilder)` eller ta emot `List<String>` från `MainActivity` via `viewModel.lasBilderForPost(post)`.
- Miniatyrer ska laddas från FileProvider-URI (`viewModel.bildUri(filnamn)`), inte från en drawable. Förebild: miniatyr i `garantivalvet/.../KopDetaljSkarm.kt` (`BitmapFactory` + `ContentResolver`, valfritt `inSampleSize` för miniatyr).
- Tryck på miniatyr ska öppna helskärm på rätt index; `MainActivity` ska sätta `bildUrier`, `valdBildIndex` och `visaBildHelskarm = true` (lokal navigation räcker — ändra inte `HusViewModel` i den här uppgiften).
- I `BildHelskarm`: vid Dela, använd **aktuell sida** i `HorizontalPager` (`pagerState.currentPage`), inte bara startindex.

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 2
beror-på: 2026-10-16-20-bild-strangar.md

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/BildHelskarm.kt (ny eller uppdatera)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/PostDetaljSkarm.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/MainActivity.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KvittoHelskarm.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopDetaljSkarm.kt (miniatyr + klick)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/HusViewModel.kt (`lasBilderForPost`, `bildUri`)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/ui/Tryckyta.kt

## Uppgift
1. **`PostDetaljSkarm`**: om posten har bilder, visa en rad med miniatyrer (en per filnamn). Parametrar ungefär: `bildFilnamn: List<String>`, `bildUri: (String) -> Uri`, `onOppnaBild: (index: Int) -> Unit`. Ladda bitmap från URI; klick anropar `onOppnaBild(index)`. Minsta tryckyta 48 dp. Ingen etikettrad som kräver nya strängnycklar (visa bara miniatyrerna, som kvittot i Garantivalvet).
2. **`BildHelskarm`**: fullskärm med `HorizontalPager`, pinch/dubbeltryck-zoom som `KvittoHelskarm`, top bar med **Stäng** (`bild_stang`) och **Dela** (`bild_dela`, minst 48 dp). Dela: `Intent.ACTION_SEND`, `image/jpeg`, URI från listan för **aktuell** pager-sida, `FLAG_GRANT_READ_URI_PERMISSION`.
3. **`MainActivity`**: när detaljvyn visas, skicka `viewModel.lasBilderForPost(post)` och `viewModel::bildUri` till `PostDetaljSkarm`. Vid `onOppnaBild`: bygg `List<Uri>` från filnamnen, sätt index och visa `BildHelskarm`; stäng sätter `visaBildHelskarm = false`.

## Exempel
- Post med två bilder (`"a.jpg,b.jpg"`) → två miniatyrer; tryck på den andra → helskärm startar på index 1; svep byter bild.
- Dela efter svep till bild 2 → delar andra bildens URI.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra `strings.xml`, repository, `HusViewModel` eller manifest.
- Lägga till beroenden eller nya drawable-/strängnycklar.
- Hårdkoda synlig text i composables.
