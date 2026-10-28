beslut: underkänd
granskare: spärren
datum: 2026-10-28

## Kontrollerat
- Inga filer är ändrade.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2
# Uppgift: Koppla in Home Assistant i RostfangstViewModel (Röstfångst)

## Vad som saknades
- Inga filer är ändrade.

uppdrag: uppdrag/claude/2026-10-27-rostfangst-ha-reserv.md
försök: 2
beror-på: 2026-10-27-04-ha-installningar-lager.md

Kräver att uppgifterna 01–04 är mergade (`HaKlient`, `HaHttpKlient`, `HaKonfig`, `haKonfig`, `haSteg`, `korHaSteg`, `InstallningarLager.haAdress/haToken/haTodo/sparaHa`).

## Ändra bara dessa filer
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ui/RostfangstViewModel.kt

## Läs som förebild (ändra inte)
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ha/HaKonfig.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ha/HaKorning.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/ha/HaKlient.kt
- rostfangst/src/main/kotlin/se/tmconnect/rostfangst/data/InstallningarLager.kt

## Uppgift
1. Ny konstruktorparameter direkt efter `tolkareFabrik`, med standardvärde så att `RostfangstViewModelFactory` inte behöver ändras:
   `private val haKlientFabrik: (HaKonfig) -> HaKlient = { HaHttpKlient(it.adress, it.token) },`
2. `InstallningarUi` får tre fält med standardvärde: `val haAdress: String = "", val haToken: String = "", val haTodo: String = ""`. `installningarUi` byggs med `combine` av alla fem flöden (`apiNyckel`, `modell`, `haAdress`, `haToken`, `haTodo`).
3. Ny funktion `fun sparaHaInstallningar(adress: String, token: String, todo: String)` i `viewModelScope.launch`:
   - Alla tre tomma (efter trim): `installningar.sparaHa("", "", "")`, meddelande `"sparad"`.
   - Annars `val k = haKonfig(adress, token, todo)`. Är `k` null: meddelande `"ha_adress"` och inget sparas. Annars `installningar.sparaHa(k.adress, k.token, k.todoEntity)` och meddelande `"sparad"`.
4. Ny privat funktion:
   ```kotlin
   private suspend fun skickaTillHa(forslag: Forslag, original: String): String?
   ```
   - Läs `adress`, `token`, `todo` med `installningar.haAdress.first()` osv. `val konfig = haKonfig(adress, token, todo)`.
   - Om `forslag.typ == ForslagTyp.HEM` och `konfig == null`: returnera `"ha_saknas"`.
   - `val steg = haSteg(forslag.typ, konfig)`. Tom lista: returnera `null`.
   - Annars `val res = withContext(Dispatchers.IO) { korHaSteg(steg, haKlientFabrik(konfig!!), konfig, forslag, original) }` och returnera `res.svar ?: res.fel.firstOrNull()`.
5. I `utforForslag`: spara `val original = tillstand.text` först. I grenen `UtforResultat.Klar` blir meddelandet `skickaTillHa(forslag, original) ?: "klar"` i stället för `"klar"`. I grenen `is UtforResultat.Kalender` anropas `skickaTillHa(forslag, original)` efter `onKalender`, och om den ger en text sätts den som meddelande, annars `"kalender"` som i dag. Grenen `IngenHandling` ändras inte.

Importera `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext` och funktionerna i `se.tmconnect.rostfangst.ha`.

## Exempel
- Ingen HA ifylld, typ ATT_GORA: som i dag, meddelandet `"klar"`, inga anrop.
- Ingen HA ifylld, typ HEM: meddelandet `"ha_saknas"`.
- HA ifylld, typ HEM, HA svarar "Lampan är tänd": meddelandet `"Lampan är tänd"`.
- HA ifylld men nere, typ KALENDER: kalendern öppnas, meddelandet `"Når inte Home Assistant just nu."`.
- `sparaHaInstallningar("ha.local", "abc", "")`: meddelandet `"ha_adress"`, inget sparas.

## Test som ska gå igenom
gradle :rostfangst:testDebugUnitTest
(Alla befintliga tester i modulen ska fortsätta gå igenom. Logiken är testad i `HaKonfigTest` och `HaKorningTest`.)

## Gör inte
- Ändra andra filer, även `RostfangstViewModelFactory` och `MainActivity`.
- Anropa HA på huvudtråden.
- Skriva token i meddelanden eller loggar.
- Lägga till beroenden.

## Frågor till Claude
- inga
