beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main innehåller ingen kod: endast flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/`.
- `Formatering.kt` och `FormateringTest.kt` under `styrelserummet/src/.../regler/` saknas; `fun kronor(belopp: Long): String` är inte implementerad.
- Uppgiftens test `gradle :styrelserummet:test --tests "*FormateringTest.kronor"` kan inte köras — testklassen finns inte.
- Inga filer utanför uppgiftens lista (förutom uppgiftsflytten); inga nya beroenden, behörigheter eller nätverk.
- Ingen `FRAGA.md` i repots rot.
- CI-bygge success beror på att ingen ny kod eller test kördes för uppgiften.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: formatera kronor

uppdrag: uppdrag/2026-10-21-styrelserummet-kvalitet-utkast.md
försök: 2
beror-på: ingen

## Vad som saknades
- PR #331 levererade **ingen implementation**: varken `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt` eller `FormateringTest.kt` skapades.
- Enda ändringen var att uppgiftsfilen flyttades till `uppgifter/pagar/` — uppgiften kräver kod och tester enligt avsnitten nedan.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Formatering.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FormateringTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/HittaByggnadensVarde.kt (belopp med mellanslag i PDF-text)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/BokfortVardeTest.kt (JUnit 4: `org.junit.Test`, `org.junit.Assert`)

## Uppgift
Skapa `Formatering.kt` i paketet `se.tmconnect.styrelserummet.regler` med:

`fun kronor(belopp: Long): String`

- Positiva tal och noll: formatera **absolutbeloppet** med hårt mellanslag (U+00A0) som tusentalsavgränsare, avsluta med `" kr"`.
- Negativa tal: unicode-minus (U+2212) före samma formaterade absolutbelopp, avsluta med `" kr"`.
- `0` → `"0 kr"` (inget tusentalsavgränsare).

Tips: gruppera siffror från höger i treor och sätt `\u00A0` mellan grupperna; använd inte `NumberFormat` om det ger fel tecken för minus eller mellanslag.

## Exempel
- `kronor(1_234_567)` → `"1\u00A0234\u00A0567 kr"`
- `kronor(0)` → `"0 kr"`
- `kronor(-500)` → `"\u2212500 kr"`

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*FormateringTest.kronor"

Skriv `FormateringTest.kt` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals`, **inte** `kotlin.test`). Lägg testmetoden `kronor` som **assertar exakt** alla tre exemplen ovan (jämför hela strängen inklusive `\u00A0` och `\u2212`).

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra datamodellen.
- Lägg filer i repots rot — endast sökvägarna under `styrelserummet/` ovan.

## Frågor till Claude
- inga
