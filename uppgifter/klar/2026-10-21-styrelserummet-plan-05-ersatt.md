# Uppgift: Ersätt en komponent vid utbyte

uppdrag: uppdrag/claude/2026-10-21-styrelserummet-avskrivningsplan.md
försök: 1
beror-på: 2026-10-21-styrelserummet-plan-04-utrangering-f2.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Utbyte.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/UtbyteTest.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt

## Uppgift
Lägg till i `Utbyte.kt`:

```kotlin
data class Utbyte(val gammal: Komponent, val ny: Komponent, val utrangering: Long)

fun ersattKomponent(
    gammal: Komponent,
    utbytesAr: Int,
    nyttAnskaffningsvarde: Long,
    nyNyttjandeperiodAr: Int = gammal.nyttjandeperiodAr,
    kalla: String,
): Utbyte
```

- `ny` är `gammal.copy(anskaffningsvarde = nyttAnskaffningsvarde, anskaffningsar = utbytesAr, nyttjandeperiodAr = nyNyttjandeperiodAr, restvarde = 0, kalla = kalla, sakerhet = Sakerhet.MEDEL)`.
- `utrangering` = `utrangeringsbelopp(gammal, utbytesAr)`.
- `IllegalArgumentException` om `nyttAnskaffningsvarde < 0` eller `nyNyttjandeperiodAr <= 0`.

## Exempel
- Gammal: "Tak", 300 000 kr, 10 år från 2020. `ersattKomponent(gammal, 2026, 450 000, kalla = "Underhållsplan 2026")` → ny har 450 000 kr, år 2026, 10 år, restvärde 0, säkerhet MEDEL, namn "Tak"; utrangering 120 000.
- Samma med `nyNyttjandeperiodAr = 30` → ny har 30 år.
- `nyttAnskaffningsvarde = -1` → IllegalArgumentException.
- `nyNyttjandeperiodAr = 0` → IllegalArgumentException.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*UtbyteTest"

## Gör inte
- Ändra andra filer.
- Ändra `utrangeringsbelopp`.
- Lägga till beroenden.

utfall: underkänd 2026-10-21 av Cursor (PR #475). Ersatt av 2026-10-21-styrelserummet-plan-05-ersatt-f2.md
