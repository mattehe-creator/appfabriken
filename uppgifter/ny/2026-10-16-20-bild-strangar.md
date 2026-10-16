# Uppgift: strängar för bildfunktioner

uppdrag: uppdrag/2026-10-16-husloggen-bilder.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/res/values/strings.xml
- husloggen/src/main/res/values-sv/strings.xml
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/res/values/strings.xml (nycklar `kvitto_*`)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/StringsNycklarTest.kt

## Uppgift
Lägg till svenska och engelska strängar för bilderna på poster, minst:

- `bild_valj_bild` — knapp Välj bild
- `bild_ta_foto` — knapp Ta foto
- `bild_ta_bort` — innehållsbeskrivning för att ta bort en miniatyr
- `bild_max_antal` — kort text när 10 bilder redan är valda (förklarar att fler inte kan läggas till)
- `bild_har_bilder` — innehållsbeskrivning för listikon när posten har bilder
- `bild_dela` — knapp Dela i helskärm
- `bild_stang` — stäng helskärm
- `bild_fel_lasning` — meddelande när bilden inte kunde sparas/läsas

Samma nycklar i båda `strings.xml`. Uppdatera inte `StringsNycklarTest` mer än nödvändigt (paritetstestet ska fortsätta gälla alla nycklar).

## Exempel
- Svenska `bild_valj_bild` och engelska motsvarighet finns med samma nyckelnamn.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra Kotlin-kod utöver testfilen om det behövs.
- Lägga till beroenden.
- Hårdkoda synlig text i composables (det kommer i senare uppgifter).
