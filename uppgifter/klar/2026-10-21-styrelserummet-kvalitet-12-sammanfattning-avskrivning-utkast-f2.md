# Uppgift: sammanfatta avskrivning och utkast

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: 2026-10-21-styrelserummet-kvalitet-11-sammanfattning-osaker-och-avstamning.md

## Vad som saknades
- PR #395 innehåller bara commit «Uppgift startad» (flytt av uppgiftsfil). **`Sammanfattning.kt` och `SammanfattningTest.kt` är oförändrade** jämfört med föregående uppgift.
- `sammanfatta` returnerar fortfarande **fyra** strängar; avskrivningsmeningen och utkastvarningen saknas.
- Testmetoden `avskrivningOchUtkast` saknas; uppgiftens testkommando hittar inget test.
- När du lägger till mening 5–6 måste **befintliga** assert-listor i samma testfil (`antalOchSumma`, `osakerOchAvstamning`) utökas med de två sista meningarna i varje scenario, annars blir hela testsviten röd.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Sammanfattning.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SammanfattningTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avskrivning.kt

## Uppgift
Utöka `sammanfatta` till **högst sex** meningar (behåll ordningen från tidigare uppgifter). Lägg till:

5. `"Avskrivningen för räkenskapsåret är A totalt."` där A är `kronor(summan av avskrivningFor(k, arsslut) för alla k i komponenter)`. Anropa befintliga `avskrivningFor` (importera från samma paket).
6. `"Detta är ett utkast – kontrollera uppgifterna mot handlingarna innan beslut."` (alltid sist).

Listan ska aldrig ha fler än sex element.

## Exempel
- Två komponenter med avskrivning 10_000 + 5_000 kr för `arsslut`, t.ex. anskaffningsår 2020, nyttjandeperiod 40, restvärde 0, anskaffningsvärden så att `avskrivningFor` ger 10_000 respektive 5_000 kr → `"Avskrivningen för räkenskapsåret är 15\u00A0000 kr totalt."`
- Tom lista, valfritt `arsslut` → avskrivningssumman 0 → `"Avskrivningen för räkenskapsåret är 0 kr totalt."` plus utkastmeningen sist.
- Sista mening exakt som i punkt 6 ovan **även för tom lista**.

## Test som ska gå igenom
Lägg till `@Test fun avskrivningOchUtkast()` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`) som assertar hela listan (sex meningar) för minst:
- tom lista (0 kr avskrivning + utkastmening),
- två komponenter enligt avskrivningsexemplet ovan.

Uppdatera även förväntade listor i `antalOchSumma` och `osakerOchAvstamning` så att varje scenario får exakt sex strängar.

gradle :styrelserummet:test --tests "*SammanfattningTest.avskrivningOchUtkast"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.

utfall: godkänd 2026-10-21 (hela implementationen — lokala modellen lämnade bara commit «Uppgift startad»)
