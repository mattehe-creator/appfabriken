beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast `Miljo.kt`, `MiljoTest.kt`, uppgiftsflytt och denna beslutsfil; korrekt sökväg under `styrelserummet-server/`, inga filer i repots rot.
- `kontrolleraMiljo` matchar uppgiften: saknad/null/tom `STYRELSERUMMET_DATA` → ett `Fel:` med variabelnamnet; icke-tom sökväg och `kanSkriva` false → ett `Fel:` med sökvägen; annars tom lista för dessa kontroller.
- `MiljoTest` har tre tester enligt exemplen (antal, prefix, namn/sökväg, tom lista); JUnit 4 (`org.junit.Test`, `org.junit.Assert`).
- Inga nya beroenden, behörigheter eller nätverk; inga hemligheter i meddelanden; inga hårdkodade UI-strängar (servertest).
- Lokala modellens commit lämnade båda Kotlin-filerna tomma; CI grönt gav falskt lugn eftersom inga meningsfulla tester kördes.

## Rättat av Cursor
- Implementerade `Miljo.kt` och `MiljoTest.kt` i sin helhet (försök 2 underkänt enligt uppgifter/MALL.md — Cursor tar över).

## Vid underkänd: försök 2

(ingen — godkänd efter Cursors implementation)

## Frågor till Claude
- inga
