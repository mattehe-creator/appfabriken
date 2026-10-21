# Uppgift: Tolka förslag med okänd komponentgrupp

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- Grenen innehöll bara commit «Uppgift startad» (flytt till `pagar/`). Filen `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallOkandGruppTest.kt` skapades inte.
- `gradle :styrelserummet:test --tests "*TolkaForslagGransfallOkandGruppTest"` kan inte köras utan testklassen; uppgiftens exempel är inte verifierade i kod.

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagGransfallOkandGruppTest.kt (ny)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt (endast om testerna avslöjar fel i logiken)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslagTest.kt

## Uppgift
Skapa testklassen `TolkaForslagGransfallOkandGruppTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert` — **inte** `kotlin.test`).

Testa `tolkaForslag(jsonText, filnamn)` när fältet `grupp` är **okänd** (t.ex. `"OGILTIG"`, `"BALCONY"`, eller fel casing som `"Stomme"` när enum kräver `STOMME`).

Förväntat beteende (ska assertas):
- **Tydlig anmärkning** om okänd grupp — minst en sträng i `anmarkningar` som innehåller `okänd grupp` och gruppnamnet från JSON.
- Komponenten ska **inte** finnas i `komponenter` (hoppas över).
- Anropet ska **inte** kasta okontrollerat undantag (testerna ska bara anropa `tolkaForslag` normalt).

Vid fel i produktionskoden: rätta **högst** i `TolkaForslag.kt`.

## Exempel (ska finnas som assert i tester)
- JSON med en komponent `"grupp":"OGILTIG"` (övriga obligatoriska fält giltiga enligt `TolkaForslagTest`) → `komponenter` tom, minst en anmärkning med `okänd grupp` och `OGILTIG`.
- Giltig komponent (`grupp":"TAK"`) + ogiltig (`grupp":"OGILTIG"`) i samma `komponenter`-lista → exakt en post i `komponenter` (TAK), anmärkning för den ogiltiga.

Tips: bygg JSON-strängar som i `TolkaForslagTest.kt`; använd `assertEquals`, `assertTrue` och `assertEquals(0, result.komponenter.size)` där det passar.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*TolkaForslagGransfallOkandGruppTest"

## Gör inte
- Ändra `TolkaForslagTest.kt`.
- Lägga till beroenden.
- Lägga filer i repots rot.
- Lämna testfilen tom eller utan assert mot exemplen ovan.

utfall: godkänd 2026-10-21 (hela testklassen — PR #377 hade ingen testkod)
