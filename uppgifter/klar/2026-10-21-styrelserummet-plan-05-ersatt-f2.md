# Uppgift: Ersätt en komponent vid utbyte

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 2
beror-på: 2026-10-21-styrelserummet-plan-04-utrangering-f2.md

## Vad som saknades
Försök 1 flyttade bara uppgiftsfilen till `uppgifter/pagar/` och lade inte till `data class Utbyte`, funktionen `ersattKomponent` eller några nya tester. Utöka befintliga `Utbyte.kt` och `UtbyteTest.kt` enligt nedan. Rör inte implementationen av `utrangeringsbelopp`.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Utbyte.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/UtbyteTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/UtbyteTest.kt (befintliga `utrangeringsbelopp`-tester ska fortsätta gå)

## Uppgift
Lägg till i `Utbyte.kt` (behåll `utrangeringsbelopp`):

data class Utbyte(val gammal: Komponent, val ny: Komponent, val utrangering: Long)

fun ersattKomponent(
    gammal: Komponent,
    utbytesAr: Int,
    nyttAnskaffningsvarde: Long,
    nyNyttjandeperiodAr: Int = gammal.nyttjandeperiodAr,
    kalla: String,
): Utbyte

- Importera `Sakerhet` från `se.tmconnect.styrelserummet.modell` om det behövs.
- `ny` ska vara `gammal.copy(anskaffningsvarde = nyttAnskaffningsvarde, anskaffningsar = utbytesAr, nyttjandeperiodAr = nyNyttjandeperiodAr, restvarde = 0, kalla = kalla, sakerhet = Sakerhet.MEDEL)`.
- `utrangering` i returvärdet = `utrangeringsbelopp(gammal, utbytesAr)`.
- Kasta `IllegalArgumentException` om `nyttAnskaffningsvarde < 0` eller `nyNyttjandeperiodAr <= 0`.

## Exempel
Bygg `gammal` i testerna med privat hjälpare (som befintliga `komponent(...)` men med `namn = "Tak"`, övriga fält som i tabellen):

| namn | anskaffningsvärde | period | anskaffningsår | restvärde |
|------|-------------------|--------|----------------|-----------|
| Tak | 300 000 | 10 | 2020 | 0 |

- `ersattKomponent(gammal, 2026, 450_000, kalla = "Underhållsplan 2026")` → `ny.anskaffningsvarde` 450 000, `ny.anskaffningsar` 2026, `ny.nyttjandeperiodAr` 10, `ny.restvarde` 0, `ny.sakerhet` MEDEL, `ny.namn` "Tak", `ny.kalla` "Underhållsplan 2026"; `utrangering` 120 000; `gammal` oförändrad i resultatet (samma värden som indata).
- Samma anrop med `nyNyttjandeperiodAr = 30` → `ny.nyttjandeperiodAr` 30.
- `nyttAnskaffningsvarde = -1` → `IllegalArgumentException`.
- `nyNyttjandeperiodAr = 0` → `IllegalArgumentException`.

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*UtbyteTest"

Lägg till minst fyra `@Test` för `ersattKomponent` enligt exemplen ovan. Använd `org.junit.Test`, `org.junit.Assert.assertEquals` och `@Test(expected = IllegalArgumentException::class)` för undantagen. Använd inte `kotlin.test`. Befintliga fem tester för `utrangeringsbelopp` ska ligga kvar och fortsätta passera.

## Gör inte
- Ändra andra filer.
- Ändra `utrangeringsbelopp`.
- Lägga till beroenden.

utfall: godkänd 2026-10-21 (hela implementationen `Utbyte`, `ersattKomponent` och fyra tester — försök 2 saknade kod)
