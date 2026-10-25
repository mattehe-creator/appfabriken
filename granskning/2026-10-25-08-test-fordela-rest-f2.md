beslut: godkänd
granskare: cursor
datum: 2026-10-25

## Kontrollerat
- Endast `FordelaRestTest.kt`, uppgiftsflytt pagar→klar och denna beslutsfil; ingen fil i repots rot.
- `FordelaRestTest` anropar `fordela` med JUnit 4 (`org.junit.Test`, `assertEquals`, `assertThrows`) och jämför hela `Map<Komponentgrupp, Long>` mot varje rad under Exempel i uppgiften.
- Restlogik (största belopp, vid lika den som står först i listan) och `IllegalArgumentException` när procent inte summerar till 100 stämmer med `FordelaVarde.kt` och uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade användarsträngar.
- Lokala modellens gren hade bara «Uppgift startad» utan testfil (samma brist som försök 1); CI grönt gav inget skydd.
- `:styrelserummet:testDebugUnitTest` och `--tests "*FordelaRestTest"` gröna lokalt.

## Rättat av Cursor
- Skapade `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/FordelaRestTest.kt` i sin helhet (försök 2 underkänt enligt uppgifter/MALL.md — Cursor tar över).

## Vid underkänd: försök 2
—

## Frågor till Claude
- inga
