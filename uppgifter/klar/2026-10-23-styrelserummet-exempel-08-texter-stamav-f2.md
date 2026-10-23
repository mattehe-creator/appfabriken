# Uppgift: Avstämning mellan plan och årsredovisning i exempeldata

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-07-texter-arsredovisning-f2.md

## Vad som saknades
- `exempelKomponenterFranPlan()` implementerades inte i `ExempelTexter.kt` (eller `ExempelData.kt`).
- `ExempelTexterStamAvTest.kt` är tom — ingen testmetod `planSummaStammerMedByggnadEnligtStamAv`.
- Committen «Lokal modell: … (okommittade ändringar efter Aider)» innehöll ingen Kotlin-logik, bara tom testfil och uppgiftsflytt.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexterStamAvTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt (`EXEMPEL_ATGARDER`, `BYGGNADENS_VARDE`, `summaAtgarderMedKostnad()`)
- styrelserummet/src/main/resources/nyttjandeperioder.csv (intervall och `nyttjandeperiod_forslag_ar` per grupp)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt (`gissaGrupp` — får importeras, filen får inte ändras)

## Uppgift
Lägg till **`fun exempelKomponenterFranPlan(): List<Komponent>`** i `ExempelTexter.kt` (rekommenderat; håll dig till **ett** ställe — ändra inte både `ExempelTexter.kt` och `ExempelData.kt`).

Bygg listan från `EXEMPEL_ATGARDER`:
- **Inkludera** bara åtgärder där `kostnadKronor != null` (13 st — **exkludera Balkonger**).
- För varje åtgärd: skapa en `Komponent` med
  - `namn` = åtgärdens namn,
  - `grupp` = `gissaGrupp(namn)` när det ger träff; annars explicit enligt tabellen nedan,
  - `anskaffningsvarde` = `kostnadKronor`,
  - `anskaffningsar` = `ar`,
  - `nyttjandeperiodAr` = `nyttjandeperiod_forslag_ar` för gruppen i `nyttjandeperioder.csv` (samma siffra som i CSV-kolumnen `nyttjandeperiod_forslag_ar`),
  - `sakerhet` = `Sakerhet.HOG`,
  - `kalla` = `"exempel-underhallsplan"`.

Explicit grupp när `gissaGrupp` returnerar null:

| Namn i planen | Komponentgrupp |
|---|---|
| Entréparti | FONSTER_DORRAR |
| Brandlarm | OVRIGT |

Nyttjandeperioder (förslag från CSV, ska ligga inom intervall):

| Grupp | nyttjandeperiodAr |
|---|---|
| TAK | 50 |
| FASAD | 40 |
| FONSTER_DORRAR | 40 |
| STAMMAR | 50 |
| EL | 50 |
| VARME | 80 |
| VENTILATION | 20 |
| HISS | 25 |
| TVATTSTUGA | 30 |
| MARK_OCH_GARAGE | 0 |
| OVRIGT | 30 |

Importera `Komponent`, `Komponentgrupp`, `Sakerhet`, `stamAv` där det behövs.

## Exempel
- 13 komponenter; `exempelKomponenterFranPlan().sumOf { it.anskaffningsvarde }` = 27_000_000.
- `stamAv(exempelKomponenterFranPlan(), BYGGNADENS_VARDE).stammer == true` och `.summa == summaAtgarderMedKostnad()`.

## Test som ska gå igenom
Skriv `ExempelTexterStamAvTest` med testmetoden **`planSummaStammerMedByggnadEnligtStamAv`** som:

- `Assert.assertEquals(13, exempelKomponenterFranPlan().size)`
- `Assert.assertEquals(27_000_000L, exempelKomponenterFranPlan().sumOf { it.anskaffningsvarde })`
- `val av = stamAv(exempelKomponenterFranPlan(), BYGGNADENS_VARDE)`
- `Assert.assertTrue(av.stammer)`
- `Assert.assertEquals(summaAtgarderMedKostnad(), av.summa)`

Använd JUnit 4 (`org.junit.Test`, `org.junit.Assert`), **inte** `kotlin.test`.

gradle :styrelserummet:testDebugUnitTest --tests "*ExempelTexterStamAvTest"

(Om `--tests` inte stöds i er Gradle-setup: kör `:styrelserummet:testDebugUnitTest` och se till att klassen innehåller testmetoden ovan.)

## Gör inte
- Ändra `stamAv`, modeller eller `ExempelData.kt`.
- Lägga till balkongkomponent med uppskattat värde.
- Committa enbart uppgiftsflytt eller tom testfil utan implementation.

utfall: godkänd 2026-10-23 (STOMME-gren i when för exhaustive compile; borttagen oanvänd import stamAv)
