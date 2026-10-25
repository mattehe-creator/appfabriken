beslut: underkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Endast `uppgifter/pagar/2026-10-25-10-test-saldo-vid-och-med.md` (flytt från `ny/`) ändrad mot main; ingen fil i fel sökväg.
- Uppgiftens enda kodleverans `flextid/src/test/kotlin/se/tmconnect/flextid/regler/SaldoVidOchMedTest.kt` saknas helt på grenen — inga tester för `arKomplettDag`, `avrundaTillHelMinut` eller `saldoVidOchMed`.
- Inga nya beroenden, behörigheter eller nätverksanrop tillagda.
- Ingen UI-kod; inga hårdkodade användarsträngar i PR:en.
- Databasschemat oförändrat.
- Bygget kan vara grönt utan att uppgiften är utförd (ingen ny testklass att kompilera eller köra).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Test av saldo till och med ett datum i Flextid

## Vad som saknades
- Ingen testfil skapades. PR #683 innehåller bara flytten av uppgiftsfilen till `uppgifter/pagar/`; `SaldoVidOchMedTest.kt` finns inte under `flextid/src/test/kotlin/se/tmconnect/flextid/regler/`.
- Uppgiften kräver JUnit 4-tester (`org.junit.Test`, `org.junit.Assert`) med assertioner mot varje exempel nedan. Committa testklassen på grenen `lokal/2026-10-25-10-test-saldo-vid-och-med`.

uppdrag: uppdrag/claude/2026-10-25-natt-testtackning.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/SaldoVidOchMedTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ManadsRapport.kt (funktionerna `arKomplettDag` och `saldoVidOchMed`)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/GenvagStampel.kt (funktionen `avrundaTillHelMinut`)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ManadsRapportTest.kt

## Uppgift
Skriv JUnit 4-tester (`org.junit.Test`, `assertEquals`, `assertTrue`, `assertFalse`) för:

- `arKomplettDag(dag: Dag): Boolean` — sant när både `start` och `slut` finns.
- `avrundaTillHelMinut(nu: LocalTime): LocalTime` — sekunder och nanosekunder blir 0 (avrundar alltid nedåt).
- `saldoVidOchMed(dagar: List<Dag>, installning: Installning, tillOchMed: LocalDate): Int` — ingångssaldot plus utfallet för alla dagar till och med `tillOchMed`.

Importera `se.tmconnect.flextid.data.Dag`, `se.tmconnect.flextid.data.Installning`, `java.time.LocalDate` och `java.time.LocalTime`.

Utfall per dag (normaltid 480 minuter måndag till fredag, 0 på lördag och söndag):
- ARBETE med start och slut: arbetade minuter (slut − start − rast) minus dagens normaltid.
- ARBETE utan slut: 0.
- FLEX: minus dagens normaltid.
- SEMESTER: 0.

Använd dessa dagar (inga röda dagar eller aftnar finns 19–26 oktober 2026):

```kotlin
private val installning = Installning(ingangssaldoMin = 60)
private val dagar = listOf(
    Dag(datum = LocalDate.of(2026, 10, 19), start = LocalTime.of(8, 0), slut = LocalTime.of(17, 0), rastMin = 30), // måndag, 510 min, +30
    Dag(datum = LocalDate.of(2026, 10, 20), start = LocalTime.of(8, 0), slut = LocalTime.of(16, 15), rastMin = 30), // tisdag, 465 min, -15
    Dag(datum = LocalDate.of(2026, 10, 21), typ = DagTyp.FLEX), // onsdag, -480
    Dag(datum = LocalDate.of(2026, 10, 22), start = LocalTime.of(8, 0)), // torsdag, ej utstämplad, 0
    Dag(datum = LocalDate.of(2026, 10, 23), typ = DagTyp.SEMESTER), // fredag, 0
    Dag(datum = LocalDate.of(2026, 10, 24), start = LocalTime.of(10, 0), slut = LocalTime.of(12, 0)), // lördag, 120 min, +120
    Dag(datum = LocalDate.of(2026, 10, 26), start = LocalTime.of(8, 0), slut = LocalTime.of(18, 0)), // måndag, 600 min, +120
)
```

## Exempel
- `arKomplettDag(Dag(datum = LocalDate.of(2026, 10, 19), start = LocalTime.of(8, 0), slut = LocalTime.of(17, 0)))` → `true`
- `arKomplettDag(Dag(datum = LocalDate.of(2026, 10, 19), start = LocalTime.of(8, 0)))` → `false`
- `arKomplettDag(Dag(datum = LocalDate.of(2026, 10, 19), slut = LocalTime.of(17, 0)))` → `false`
- `avrundaTillHelMinut(LocalTime.of(8, 5, 59, 999_000_000))` → `LocalTime.of(8, 5)`
- `avrundaTillHelMinut(LocalTime.of(23, 59, 30))` → `LocalTime.of(23, 59)`
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 18))` → `60` (inga dagar ännu)
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 19))` → `90`
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 20))` → `75`
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 21))` → `-405`
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 23))` → `-405` (ej utstämplad dag och semester ger 0)
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 24))` → `-285` (lördag har normaltid 0)
- `saldoVidOchMed(dagar, installning, LocalDate.of(2026, 10, 31))` → `-165`
- `saldoVidOchMed(dagar.reversed(), installning, LocalDate.of(2026, 10, 31))` → `-165` (ordningen spelar ingen roll)
- `saldoVidOchMed(emptyList(), Installning(ingangssaldoMin = -30), LocalDate.of(2026, 10, 31))` → `-30`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*SaldoVidOchMedTest"

## Gör inte
- Ändra andra filer.
- Ändra `ManadsRapport.kt`, `GenvagStampel.kt`, `Arbetstid.kt` eller `TidRegler.kt`. Om ett exempel inte stämmer med koden: skriv det i PR-beskrivningen och låt testet följa exemplet.
- Lägga till fler dagar i listan `dagar`, särskilt inte på röda dagar eller aftnar (till exempel 31 oktober eller i december). `tillOchMed = 2026-10-31` är bara en gräns och går bra.
- Lägga till beroenden.

## Frågor till Claude
- inga
