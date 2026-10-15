# Uppgift: garantitiden som en del av kvittoförslaget

uppdrag: ingen (Mattias önskemål 2026-10-14)
försök: 1
beror-på: 2026-10-14-01-garantitolkare.md

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkare.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkareTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantiTolkare.kt (funktionen `tolkaGarantiManader(text: String): Int?`, finns efter föregående uppgift)

## Uppgift
I `KvittoTolkare.kt`:
1. Lägg till fältet `val garantiManader: Int? = null` sist i `data class KvittoForslag` (efter `kontakt`).
2. `arTomt` ska också kräva `garantiManader == null`.
3. I `tolkaKvitto(text, idag)` sätts `garantiManader = tolkaGarantiManader(text)`.
4. Inget annat ändras (inte `fyllTommaFalt`, inte `Ifyllning`).

I `KvittoTolkareTest.kt` läggs testfall till, de befintliga ändras inte:
- `tolkaKvitto("Elgiganten\n2026-08-01\nSumma att betala 1 299,00\n3 års garanti", LocalDate.of(2026, 10, 14)).garantiManader` är 36.
- `tolkaKvitto("Kjell & Company\n01.08.2026\nTotal 349.90 SEK", LocalDate.of(2026, 10, 14)).garantiManader` är `null`.
- `tolkaKvitto("Garanti 24 mån", LocalDate.of(2026, 10, 14)).arTomt` är `false` (garantitiden räknas som ett fynd).
- `tolkaKvitto("", LocalDate.of(2026, 10, 14)).arTomt` är `true`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoTolkareTest"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.

utfall: underkänd 2026-10-15 av Cursor (PR #153). Ersatt av 2026-10-14-02-garanti-i-kvittoforslag-f2.md
