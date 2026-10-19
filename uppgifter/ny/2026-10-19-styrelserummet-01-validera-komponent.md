# Uppgift: Validera komponent

uppdrag: uppdrag/2026-10-19-styrelserummet-raknekarna.md
försök: 1
beror-på: cursor:2026-10-19-styrelserummet-raknekarna

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Validera.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ValideraTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponent.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Sakerhet.kt

## Uppgift
Inför `data class Fel(val kod: String, val meddelande: String)` och:

`fun validera(k: Komponent, nuAr: Int): List<Fel>`

Returnera tom lista om allt är giltigt. Annars ett eller flera fel (svenska meddelanden):

| Kod | Villkor |
|---|---|
| `NYTTJANDEPERIOD` | `nyttjandeperiodAr < 1` |
| `NEGATIVT_VARDE` | `anskaffningsvarde < 0` |
| `RESTVARDE` | `restvarde > anskaffningsvarde` |
| `ANSKAFFNINGSAR` | `anskaffningsar < 1800` eller `anskaffningsar > nuAr + 1` |

## Exempel
- Komponent med nyttjandeperiod 0 → minst ett fel med kod `NYTTJANDEPERIOD`.
- anskaffningsvarde 100_000, restvarde 100_001 → kod `RESTVARDE`.
- nuAr 2025, anskaffningsar 2027 → kod `ANSKAFFNINGSAR`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ValideraTest"

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra databasschemat.
