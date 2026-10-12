# Uppgift: regel för varning när gratisgränsen närmar sig

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/GratisGransTest.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt (befintliga `kanLaggaTill`)

## Uppgift
Lägg till i `GratisGrans.kt`:
- `const val GRATIS_VARNING_FRAN_ANTAL = 12`
- `fun skaVisaGratisGransVarning(antal: Int, arPro: Boolean): Boolean` — true när `!arPro` och `antal >= GRATIS_VARNING_FRAN_ANTAL` och `antal < GRATIS_MAX_KOP`.
- `fun kvittonKvarGratis(antal: Int): Int` — `(GRATIS_MAX_KOP - antal).coerceAtLeast(0)`.

Lägg till tester i `GratisGransTest.kt` för exemplen nedan. Ändra inte befintliga tester.

## Exempel
- 11 köp, inte Pro → `skaVisaGratisGransVarning` false.
- 12 köp, inte Pro → true, `kvittonKvarGratis(12)` = 3.
- 14 köp, inte Pro → true, `kvittonKvarGratis(14)` = 1.
- 15 köp, inte Pro → false (gränsen nådd, FAB visar Pro-dialogen som i dag).
- 12 köp, Pro → false.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
