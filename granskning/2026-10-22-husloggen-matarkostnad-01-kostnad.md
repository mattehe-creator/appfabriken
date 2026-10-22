beslut: underkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #548 ändrade enbart flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/`; inga tillåtna Kotlin-filer skapades eller committades.
- `husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareKostnad.kt` och `MatareKostnadTest.kt` saknas helt på grenen (varken tomma filer).
- Uppgiften kräver `forbrukningKostnadOre` med heltalsavrundning och JUnit 4-tester mot alla sex exemplen — inget av detta finns.
- Inga filer i repots rot eller utanför uppgiftens lista (bortsett från den tillåtna uppgiftsflytten).
- Inga nya beroenden, behörigheter eller nätverk (ingen produktionskod tillagd).
- Bygget kan vara grönt eftersom inga nya tester lades till som skulle fallera.

## Rättat av Cursor
- inget (saknad implementation — den lokala modellens uppgift)

## Vid underkänd: försök 2

# Uppgift: Kostnad för förbrukning

## Vad som saknades (försök 1)
- Grenen innehöll bara commit «Uppgift startad» med flytt `uppgifter/ny/` → `uppgifter/pagar/`.
- Varken `MatareKostnad.kt` eller `MatareKostnadTest.kt` skapades. Implementera funktionen och testerna enligt avsnittet «Uppgift» nedan.
- Filerna ska ligga under `husloggen/src/main/kotlin/...` respektive `husloggen/src/test/kotlin/...`, **inte** i repots rot.
- Minst ett JUnit 4-test (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertNull`) som assertar **exakt** `Long?` mot varje rad i «Exempel» (inga flyttal, inget `kotlin.test`).

uppdrag: uppdrag/claude/2026-10-22-husloggen-matarkostnad.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/MatareKostnad.kt (ny)
- husloggen/src/test/kotlin/se/tmconnect/husloggen/regler/MatareKostnadTest.kt (ny)

## Läs som förebild (ändra inte)
- husloggen/src/main/kotlin/se/tmconnect/husloggen/regler/ForbrukningPrognos.kt

## Uppgift
```kotlin
fun forbrukningKostnadOre(forbrukningTusendel: Long, prisOrePerEnhet: Long): Long?
```

- Förbrukningen är i tusendelar av enheten (1 kWh = 1 000). Priset är i öre per hel enhet.
- Kostnad = förbrukning × pris / 1 000, avrundat till närmaste öre, halva uppåt: `(forbrukningTusendel * prisOrePerEnhet + 500) / 1000`.
- Negativ förbrukning eller negativt pris → `null`.
- Använd bara heltalsaritmetik (`Long`), inga `Double`/`Float`.

## Exempel
- 1 000 000 (1 000 kWh), pris 150 öre → 150 000
- 2 500 (2,5 kWh), pris 133 öre → 333 (332,5 avrundas uppåt)
- 1 (0,001 kWh), pris 100 öre → 0
- 0, pris 200 → 0
- −1 000, pris 100 → null
- 1 000, pris −1 → null

## Test som ska gå igenom
gradle :husloggen:testDebugUnitTest --tests "*MatareKostnadTest"

## Gör inte
- Ändra andra filer.
- Använda flyttal.

## Frågor till Claude
- inga
