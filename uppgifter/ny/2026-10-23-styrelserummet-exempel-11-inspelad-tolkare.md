# Uppgift: Inspelat AI-svar för exempelkomponenter

uppdrag: uppdrag/2026-10-23-styrelserummet-exempelhandlingar.md
försök: 1
beror-på: 2026-10-23-styrelserummet-exempel-08-texter-stamav.md

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelning.kt (ny)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/exempel/ExempelInspelningTest.kt (ny)

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/TolkaForslag.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/SyntetiskPlanSaknadKostnadTest.kt

## Uppgift
`fun inspeladTolkareJson(): String` — giltig JSON enligt tolkningsschemat med:

- en post per åtgärd i `EXEMPEL_ATGARDER` **utom** att Balkonger ska ha `"anskaffningsvarde": null`, `"anskaffningsar": null`, `"sakerhet": "LAG"` (kostnad saknas i planen)
- övriga poster: `anskaffningsvarde` = planens kostnad, `anskaffningsar` = planens år, rimlig grupp och `sakerhet` `HOG` eller `MEDEL`
- minst en `anmarkningar`-text som nämner att kostnad saknas för balkong

`fun inspeladTolkare(): Tolkare` returnerar stub som alltid ger `Result.success(inspeladTolkareJson())`.

Test `inspeladJsonTolkasTill14Komponenter`:

- tolka JSON via befintlig pipeline eller `byggUtkast` med tom plan (kort) — enklare: deserialisera med samma kod som `TolkaForslag` använder och assert 14 komponenter, en med `Sakerhet.LAG` för balkong.

## Exempel
- JSON innehåller `"Balkonger"` och `"sakerhet":"LAG"`.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*ExempelInspelningTest"

## Gör inte
- Anropa riktig AI.
- Ändra `TolkaForslag.kt`.
