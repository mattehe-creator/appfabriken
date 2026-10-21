# Uppgift: Summering tom lista, grupper och dold tom grupp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- `SummeringGransfallGrupperTest.kt` är **tom** — ingen testklass, inga `@Test`-metoder.
- Uppgiftens beteende (tom lista, en grupp med flera komponenter, flera grupper i enum-ordning, utelämnad grupp utan komponenter) är inte testat.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringGransfallGrupperTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Summering.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/SummeringTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt

## Uppgift
Implementera testklassen `SummeringGransfallGrupperTest` med **JUnit 4** (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`).

Testa `summeraPerGrupp(komponenter: List<Komponent>, arsslut: Int)`:

1. **Tom lista** → tom resultatlista (`emptyList()`).
2. **En grupp, flera komponenter** → exakt en `GruppSumma` med summerade `anskaffning`, `avskrivningAret` och `bokfort` för den gruppen.
3. **Flera grupper** → en rad per grupp som har minst en komponent, i **enum-ordning** (`Komponentgrupp.entries`), med assert på alla tre summfält per rad.
4. **Grupp utan komponenter** (t.ex. `VENTILATION` eller `HISS` saknas i indatalistan) → ska **inte** finnas i resultatet.

Återanvänd samma mönster som `SummeringTest.kt`: privat hjälpfunktion `komponent(grupp, varde, anskaffningsar, period)` som skapar `Komponent` med `Sakerhet.HOG`, `restvarde = 0`, `kalla = "test"`.

Om ett test visar fel i produktionslogik får du **endast** ändra `Summering.kt` (lägg till filen under «Ändra bara dessa filer» och håll dig inom cirka 150 rader totalt).

## Exempel (asserta exakt med `assertEquals` på Long-värden)

- **Tom lista:** `summeraPerGrupp(emptyList(), 2025)` → `emptyList()` (t.ex. `assertTrue(…isEmpty())` eller `assertEquals(emptyList(), …)`).

- **En grupp (STOMME), två komponenter** — samma indata som `SummeringTest.summeraPerGruppStommeOchEl2025` men **bara** de två STOMME-raderna (1_200_000/2010/40 och 100_000/2020/3), `arsslut = 2025`:
  - storlek 1
  - `grupp = STOMME`, `anskaffning = 1_300_000L`, `avskrivningAret = 30_000L`, `bokfort = 720_000L`

- **Tre grupper STOMME + TAK + EL** (ingen `VENTILATION`), `arsslut = 2025`:
  - STOMME: en komponent 1_200_000 / 2010 / 40 (samma som i `SummeringTest`)
  - TAK: en komponent 800_000 / 2015 / 40
  - EL: en komponent 500_000 / 2015 / 25 (samma som i `SummeringTest`)
  - Förväntat **3 rader** i ordning **STOMME, TAK, EL** (enum-ordning — TAK kommer före EL i `Komponentgrupp`)
  - Förväntade summor: STOMME som ovan ensam; TAK `anskaffning = 800_000L`, `avskrivningAret = 20_000L`, `bokfort = 580_000L`; EL `anskaffning = 500_000L`, `avskrivningAret = 20_000L`, `bokfort = 280_000L`
  - Assert att ingen rad har `grupp == VENTILATION` (och gärna `HISS`).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*SummeringGransfallGrupperTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra `SummeringTest.kt`.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (hela testklassen — PR hade tom fil)
