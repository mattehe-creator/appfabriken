beslut: underkänd
granskare: cursor
datum: 2026-10-15

## Kontrollerat
- PR #153 / grenen innehåller ingen ändring av `KvittoTolkare.kt` eller `KvittoTolkareTest.kt`, bara flytt av uppgiftsfilen `uppgifter/ny/` → `uppgifter/pagar/`.
- Uppgiftens beteende (fält `garantiManader`, `arTomt`, `tolkaKvitto`, nya test) är därför inte implementerat; inga nya testfall som jämför mot exemplen.
- `GarantiTolkare.kt` med `tolkaGarantiManader` finns redan på main (föregående uppgift); inget behov av import utöver att anropa funktionen i samma paket.
- Bygget blev grönt eftersom ingen kod ändrades (samma mönster som BESLUTSLOGG 2026-10-14 om tom körning).
- Inga filer i fel sökväg, inga nya beroenden, behörigheter eller nätverk i diffen.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: garantitiden som en del av kvittoförslaget

uppdrag: ingen (Mattias önskemål 2026-10-14)
försök: 2
beror-på: 2026-10-14-01-garantitolkare.md

## Vad som saknades
Grenen innehöll ingen kod. `KvittoTolkare.kt` och `KvittoTolkareTest.kt` ändrades inte. Uppgiften nedan ska genomföras i de två filerna.

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkare.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/KvittoTolkareTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GarantiTolkare.kt (funktionen `tolkaGarantiManader(text: String): Int?`)

## Uppgift
I `KvittoTolkare.kt`:
1. Lägg till fältet `val garantiManader: Int? = null` sist i `data class KvittoForslag` (efter `kontakt`).
2. `arTomt` ska också kräva `garantiManader == null`.
3. I `tolkaKvitto(text, idag)` sätts `garantiManader = tolkaGarantiManader(text)`.
4. Inget annat ändras (inte `fyllTommaFalt`, inte `Ifyllning`).

I `KvittoTolkareTest.kt` läggs **nya** `@Test`-funktioner till längst ned i klassen (ändra inte befintliga tester):
- `tolkaKvitto("Elgiganten\n2026-08-01\nSumma att betala 1 299,00\n3 års garanti", LocalDate.of(2026, 10, 14)).garantiManader` är 36.
- `tolkaKvitto("Kjell & Company\n01.08.2026\nTotal 349.90 SEK", LocalDate.of(2026, 10, 14)).garantiManader` är `null`.
- `tolkaKvitto("Garanti 24 mån", LocalDate.of(2026, 10, 14)).arTomt` är `false` (garantitiden räknas som ett fynd).
- `tolkaKvitto("", LocalDate.of(2026, 10, 14)).arTomt` är `true`.

Använd JUnit 4: `org.junit.Test`, `org.junit.Assert.assertEquals` / `assertTrue` / `assertNull`. Inte `kotlin.test`.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*KvittoTolkareTest"
gradle testDebugUnitTest

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
- Committa enbart uppgiftsflytt utan kod — då startar granskning men uppgiften är inte gjord.
```

## Frågor till Claude
- inga
