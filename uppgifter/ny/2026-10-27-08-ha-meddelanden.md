# Uppgift: Meddelanden för HA-adress och saknad HA (Röstfångst)

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 1
beror-på: 2026-10-27-07-ha-installningar-skarm-f2.md

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/MainActivity.kt
- rostfangst/src/main/res/values/strings.xml
- rostfangst/src/main/res/values-sv/strings.xml

## Uppgift
I `MainActivity` finns `when (key)` som översätter meddelandekoder till text (`"tom"`, `"klar"`, `"kalender"`, `"saknar_tid"`, `"sparad"`, `else -> key`). Lägg till två rader före `else`:

```kotlin
"ha_adress" -> getString(R.string.fel_ha_adress)
"ha_saknas" -> getString(R.string.fel_ha_saknas)
```

Strängar, samma nycklar i båda filerna:

| nyckel | values (engelska) | values-sv |
|---|---|---|
| fel_ha_adress | Check the Home Assistant address and token. The address must start with http:// or https://. | Kontrollera adress och token till Home Assistant. Adressen ska börja med http:// eller https://. |
| fel_ha_saknas | Add Home Assistant in Settings to control your home. | Lägg in Home Assistant under Inställningar för att styra hemmet. |

## Exempel
- Meddelandet `"ha_saknas"` visar "Lägg in Home Assistant under Inställningar för att styra hemmet." på svenska.
- Meddelandet `"Lampan är tänd"` (HA:s svar) visas oförändrat via `else -> key`.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest --tests "*StringsNycklarTest"

## Gör inte
- Ändra något annat i `MainActivity`.
- Lägga till beroenden.
