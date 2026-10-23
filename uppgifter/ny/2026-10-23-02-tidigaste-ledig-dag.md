# Uppgift: Tidigaste ledig dag

uppdrag: uppdrag/2026-10-23-flextid-ledighetskalkyl.md
försök: 1
beror-på: 2026-10-23-01-ledighetsutrymme.md

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidigasteLedigDag.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/TidigasteLedigDagTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Ledighet.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt

## Uppgift
Första datumet från och med imorgon då [antalDagar] arbetsdagar i följd kan tas ut (första ledighetsdagen). Arbetsdag = veckodag enligt mask och inte röd dag; parameter [helgdagar] är samma som `aftnarLediga` i appen (räkna aftnar som lediga när true).

```kotlin
fun tidigasteLedigDag(
    idag: LocalDate,
    antalDagar: Int,
    arbetsdagarMask: Int,
    helgdagar: Boolean,
): LocalDate?
```

- `antalDagar <= 0` → `null`.
- Sök startdatum från `idag.plusDays(1)`; för varje kandidat, kontrollera att det finns [antalDagar] planerbara arbetsdagar i följd (nästa planerbara dag efter varje ledig dag, hoppa över helg och röda dagar).
- Använd `ARBETSDAGAR_MANDAG_FREDAG` i testerna om inget annat anges.

## Exempel
- idag 2026-10-22 (tors), 1 dag, standardmask, helgdagar true → 2026-10-23
- idag 2026-10-23 (fre), 1 dag → 2026-10-26
- idag 2026-10-22, 0 dagar → null

## Test som ska gå igenom
- Gränsfall: ledighet över helg — idag 2026-10-22 (tors), 2 dagar → 2026-10-23 (fre + mån räknas som två arbetsdagar i följd)

gradle :flextid:testDebugUnitTest --tests "*TidigasteLedigDagTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
