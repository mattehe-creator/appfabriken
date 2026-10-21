# Uppgift: gissaGrupp för hiss, tvättstuga och mark

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-03-gissa-grupp-el-varme-f2.md

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Varken `GrupperaNamn.kt` utökades med nyckelord eller `GrupperaNamnTest.kt` med testmetoden `gissaGruppHissTvattMark`.
- `gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppHissTvattMark"` kan inte verifiera uppgiften utan implementation och test.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamn.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/GrupperaNamnTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Utöka listan `nyckelord` i `GrupperaNamn.kt` så att `gissaGrupp` returnerar:
- **HISS** vid delsträngen `hiss` (gemener via befintlig `lowercase()`)
- **TVATTSTUGA** vid `tvättstuga`
- **MARK_OCH_GARAGE** vid `garage`, `asfalt`, `gård`, `mark`

Använd samma logik som befintliga nyckelord: träffar samlas, vinner lägst startindex i namnet, vid lika index längre nyckelord. Ändra inte `indexSomEgetOrd` eller el-hanteringen om det inte behövs för testerna.

Lägg till JUnit 4-test (`org.junit.Test`, `org.junit.Assert.assertEquals` — **inte** `kotlin.test`):

@Test
fun gissaGruppHissTvattMark() { ... }

med assert för alla fyra exemplen nedan. Rör inte befintliga testmetoder om det inte krävs.

## Exempel (ska finnas som assert i `gissaGruppHissTvattMark`)
- `"Personhiss"` → `Komponentgrupp.HISS`
- `"Tvättstuga källare"` → `Komponentgrupp.TVATTSTUGA`
- `"Asfalterad gård"` → `Komponentgrupp.MARK_OCH_GARAGE` (`asfalt` före `gård` i namnet)
- `"Garageport"` → `Komponentgrupp.MARK_OCH_GARAGE`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*GrupperaNamnTest.gissaGruppHissTvattMark"

## Gör inte
- Ändra andra filer (inklusive filer i repots rot).
- Lägga till beroenden.
- Ändra datamodellen.
