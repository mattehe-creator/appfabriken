beslut: underkänd
granskare: cursor
datum: 2026-10-23

## Kontrollerat
- PR #610 ändrar rätt sökvägar (`styrelserummet/.../exempel/`), inga filer i repots rot; plus flytt `uppgifter/ny/` → `uppgifter/pagar/`.
- `ExempelInspelning.kt` och `ExempelInspelningTest.kt` är **tomma** (0 byte) — ingen `inspeladTolkareJson()`, ingen `inspeladTolkare()`, inget test `inspeladJsonTolkasTill14Komponenter`.
- Uppgiftens beteende (14 komponenter, Balkonger med `LAG` och null-kostnad, inspelad `Tolkare`-stub) är inte implementerat.
- Gradle `:styrelserummet:test --tests "*ExempelInspelningTest"` misslyckas med «No tests found»; `:styrelserummet:testDebugUnitTest` utan filter är grönt (tom testfil ger ingen testklass).
- Inga nya beroenden, behörigheter eller nätverk i diffen; `TolkaForslag.kt` oförändrad.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Inspelat AI-svar för exempelkomponenter

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 2
beror-på: 2026-10-23-styrelserummet-exempel-08-texter-stamav-f2.md

## Vad som saknades
- `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelning.kt` skapades som tom fil (0 byte) — ingen implementation.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelningTest.kt` skapades som tom fil — inget test.
- Commits «Uppgift startad» och «Lokal modell: … (okommittade ändringar efter Aider)» innehåller ingen Kotlin-kod.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelning.kt (ersätt tom fil — skriv full kod)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelningTest.kt (ersätt tom fil — skriv full kod)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt (`tolkaForslag`)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt (JSON med null-kostnad och `sakerhet` `LAG`)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelTexter.kt (grupp, nyttjandeperiod, `gissaGrupp` + undantag Entréparti/Brandlarm)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelData.kt (`EXEMPEL_ATGARDER`)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/tolking/Tolkare.kt

## Uppgift

Skapa i `ExempelInspelning.kt`:

### `fun inspeladTolkareJson(): String`
Returnera **giltig JSON** som `tolkaForslag` kan läsa (fält `komponenter` som array, valfritt `anmarkningar` som array av strängar):

- **En komponent per** post i `EXEMPEL_ATGARDER` (14 st), i samma ordning som listan.
- Fält per komponent (samma namn som i `SyntetiskPlanSaknadKostnadTest`): `namn`, `grupp` (enum-namn t.ex. `"TAK"`), `anskaffningsvarde`, `anskaffningsar`, `nyttjandeperiodAr`, `kalla`, `sakerhet` (`"HOG"`, `"MEDEL"` eller `"LAG"`).
- **Balkonger:** `"anskaffningsvarde": null`, `"anskaffningsar": null`, `"sakerhet": "LAG"`. Välj rimlig `grupp` (t.ex. `"FASAD"`) och `nyttjandeperiodAr` (t.ex. 40) eftersom `gissaGrupp("Balkonger")` är null.
- **Övriga 13:** `anskaffningsvarde` = planens `kostnadKronor`, `anskaffningsar` = planens `ar`. Grupp och `nyttjandeperiodAr`: samma logik som i `exempelKomponenterFranPlan()` i `ExempelTexter.kt` (`gissaGrupp` + `Entréparti` → `FONSTER_DORRAR`, `Brandlarm` → `OVRIGT`, `when (grupp)` för period). `sakerhet`: `"HOG"` (eller `"MEDEL"` där det känns rimligt).
- `kalla`: t.ex. `"exempel-underhallsplan.pdf, sida 2"` för alla (konsekvent sträng räcker).
- **`anmarkningar`:** minst en text som nämner att **kostnad saknas** och **balkong** (t.ex. «Kostnad saknas i planen för Balkonger.»).

JSON får byggas med `buildString`, `joinToString` över `EXEMPEL_ATGARDER`, eller liknande — hårdkoda inte 14 separata kopior om en loop räcker.

### `fun inspeladTolkare(): Tolkare`
Returnera en anonym `Tolkare` (eller `object`) där `foreslaKomponenter(...)` alltid returnerar `Result.success(inspeladTolkareJson())` oavsett indata (ingen riktig AI).

## Exempel
- JSON innehåller `"Balkonger"` och `"sakerhet":"LAG"` (ev. mellanslag efter kolon i JSON är ok).
- Efter `tolkaForslag(inspeladTolkareJson(), "exempel.pdf")` → exakt **14** komponenter; den som heter Balkonger har `Sakerhet.LAG`.

## Test som ska gå igenom

Skriv `ExempelInspelningTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert`), **inte** `kotlin.test`.

Testmetod **`inspeladJsonTolkasTill14Komponenter`**:

1. `val json = inspeladTolkareJson()` — assert att json innehåller `Balkonger` och `LAG` (delsträngar).
2. `val resultat = tolkaForslag(json, "exempel-underhallsplan.pdf")` — `assertEquals(14, resultat.komponenter.size)`.
3. Balkong: `resultat.komponenter.single { it.namn == "Balkonger" }` → `assertEquals(Sakerhet.LAG, balkong.sakerhet)`.
4. `assertTrue(resultat.anmarkningar.any { it.contains("kostnad", ignoreCase = true) && it.contains("balkong", ignoreCase = true) })`.
5. (Rekommenderat) `assertEquals(json, inspeladTolkare().foreslaKomponenter("", emptyList(), "x").getOrThrow())`.

gradle :styrelserummet:test --tests "*ExempelInspelningTest"

## Gör inte
- Anropa riktig AI eller Anthropic.
- Ändra `TolkaForslag.kt`, `ExempelData.kt`, `ExempelTexter.kt` eller andra filer.
- Committa enbart uppgiftsflytt eller tomma filer utan kod.
- Lägga till beroenden.
```

## Frågor till Claude
- inga
