# Uppgift: kort när gratisgränsen närmar sig

uppdrag: uppdrag/2026-10-12-ny-design.md
försök: 1
beror-på: cursor:2026-10-12-ny-design-f2

## Ändra bara dessa filer
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/regler/GratisGrans.kt
- garantivalvet/src/test/kotlin/se/tmconnect/garantivalvet/regler/GratisGransTest.kt
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopSkarmar.kt

## Läs som förebild (ändra inte)
- garantivalvet/src/main/kotlin/se/tmconnect/garantivalvet/ui/KopListaSkarm

## Uppgift
1. I `GratisGrans.kt`, lägg till:
   - `const val GRATIS_VARNING_FRAN_ANTAL = 12`
   - `fun skaVisaGratisGransVarning(antal: Int, arPro: Boolean): Boolean` — true när inte Pro och `antal >= GRATIS_VARNING_FRAN_ANTAL` och `antal < GRATIS_MAX_KOP`.
   - `fun kvittonKvarGratis(antal: Int): Int` — `GRATIS_MAX_KOP - antal` (för giltiga antal under gränsen).

2. I `KopSkarmar.kt` (listan): när `skaVisaGratisGransVarning` är true, visa ett diskret **Card** överst i listan (före köp) med text från nya strängar `pro_kvar_gratis` (sv «%1$d kvitton kvar i gratisversionen», en «%1$d receipts left in the free version») och en textknapp/länk som anropar `onVisaProDialog`.

3. Enhetstest i `GratisGransTest.kt` för gränserna 11, 12 och 15 (Pro=false).

## Exempel
- 11 köp, inte Pro → ingen varning.
- 12 köp → varning, `kvittonKvarGratis` = 3.
- 15 köp, inte Pro → FAB blockeras som idag; varning kan fortfarande visas om listan har 15 poster.

## Test som ska gå igenom
gradle :garantivalvet:testDebugUnitTest --tests "*GratisGransTest"

## Gör inte
- Ändra andra filer (lägg strängar i samma commit endast om uppgiften utökas — annars återanvänd befintliga Pro-strängar eller be Cursor lägga strängar i del A).
- Lägga till beroenden.
- Ändra databasschemat.
