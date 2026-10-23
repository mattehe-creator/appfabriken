# Uppgift: kommandeUnderhall per år

uppdrag: uppdrag/2026-10-23-husloggen-kommande-underhall-plan.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/KommandeUnderhall.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/KommandeUnderhallTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/Kommande.kt
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/UppgiftKostnad.kt

## Uppgift

```kotlin
data class KommandeUnderhallRad(
    val uppgiftId: Long,
    val uppgiftNamn: String,
    val datum: LocalDate,
    val uppskattadKostnadOre: Long?,
)

data class KommandeUnderhallArGrupp(
    val ar: Int,
    val rader: List<KommandeUnderhallRad>,
    val summaOre: Long,
    val harOkandKostnad: Boolean,
)

fun kommandeUnderhall(
    uppgifter: List<Uppgift>,
    loggposter: List<Post>,
    idag: LocalDate,
    antalAr: Int = 5,
): List<KommandeUnderhallArGrupp>
```

- Hoppa över uppgifter med `intervallManader < 1`.
- Använd `senasteKostnadPerUppgift(loggposter)` och befintliga `forekomster(...)` med `fran = idag`, `till = idag.plusYears(antalAr).minusDays(1)`.
- Samma uppskattade kostnad (eller null) för varje förekomst av samma uppgift.
- `summaOre`: summera bara rader där kostnad inte är null.
- `harOkandKostnad`: true om minst en rad i året saknar kostnad.
- Gruppera på `datum.year`, sortera år stigande, rader per år sorterade på datum sedan namn.

## Exempel
- idag 2026-06-01, uppgift månadsintervall, senast gjord 2026-05-01, kostnad 10 000 kr → flera rader 2026 med summa som multipl av 10 000.
- Uppgift utan loggpost kopplad → `uppskattadKostnadOre = null`, `harOkandKostnad = true` för berörda år.
- `intervallManader = 0` → uppgiften ignoreras.

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*KommandeUnderhallTest"

Tester ska täcka: månadsintervall, årsintervall, uppgift utan kostnad, uppgift utan intervall, skottdag (29 feb + 12 mån), `antalAr = 1`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
