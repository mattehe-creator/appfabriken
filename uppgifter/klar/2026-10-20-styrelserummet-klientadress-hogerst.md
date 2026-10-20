# Uppgift: Klientadress från högra adressen i X-Forwarded-For

uppdrag: uppdrag/2026-10-20-styrelserummet-driftklar.md
försök: 1
beror-på: ingen

## Ändra bara dessa filer
- styrelserummet-server/src/main/kotlin/se/tmconnect/styrelserummet/server/KlientAdress.kt
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/KlientAdressTest.kt

## Läs som förebild (ändra inte)
- styrelserummet-server/src/test/kotlin/se/tmconnect/styrelserummet/server/IpGransTest.kt (om den finns; annars bara KlientAdressTest.kt)

## Uppgift
Funktionen `klientAdress(forwardedFor: String?, anslutning: String, proxyPaa: Boolean): String` tar idag den **första** adressen i `X-Forwarded-For`. Det kan förfalskas: en besökare kan skicka ett eget huvud, och värdens proxy lägger sedan till den verkliga adressen sist. Då kringgås gränsen på 5 jobb per timme, som skyddar mot kostnad för tolkningen.

Ändra så att den **sista icke-tomma** adressen används (den som värdens proxy lade till). Allt annat är oförändrat:
- `proxyPaa` är `false`: returnera `anslutning`, ignorera huvudet.
- `forwardedFor` är `null`, tom eller bara blanksteg och kommatecken: returnera `anslutning`.
- Blanksteg runt adresser trimmas. Tomma poster (till exempel efter ett avslutande komma) hoppas över.

Uppdatera kommentaren ovanför funktionen så att den säger "sista adressen (den som värdens proxy lade till)" och varför.

Ändra testfilen: byt det första testet så att det förväntar sig sista adressen, och lägg till testerna i exemplen nedan. Behåll de två andra befintliga testerna oförändrade.

## Exempel
- `klientAdress("203.0.113.5, 10.0.0.1", "127.0.0.1", true)` ger `10.0.0.1`.
- `klientAdress("9.9.9.9, 203.0.113.5", "127.0.0.1", true)` ger `203.0.113.5` (besökarens förfalskade `9.9.9.9` ignoreras).
- `klientAdress("203.0.113.5", "127.0.0.1", true)` ger `203.0.113.5`.
- `klientAdress("203.0.113.5, ", "127.0.0.1", true)` ger `203.0.113.5` (tom post sist).
- `klientAdress(" , ", "10.1.2.3", true)` ger `10.1.2.3`.
- `klientAdress("203.0.113.5, 10.0.0.1", "127.0.0.1", false)` ger `127.0.0.1`.

## Test som ska gå igenom
gradle :styrelserummet-server:test --tests "*KlientAdressTest"
Dessutom ska `gradle :styrelserummet-server:test` fortsätta gå igenom (ApiTest använder samma huvud i båda anropen och påverkas inte).

## Gör inte
- Ändra andra filer (inte heller `ApiTest.kt` eller `DRIFT.md`).
- Lägga till beroenden.
- Ändra databasschemat.

utfall: godkänd 2026-10-20 (inget)
