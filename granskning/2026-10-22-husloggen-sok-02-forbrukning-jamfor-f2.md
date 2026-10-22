beslut: godkänd
granskare: cursor
datum: 2026-10-22

## Kontrollerat
- PR #515 ändrade bara flytt av uppgiftsfil till `uppgifter/pagar/` plus tomma Kotlin-filer på rätt sökvägar (inga filer i repots rot).
- Uppgiften (försök 2) krävde `ForbrukningJamforelse.kt` och `ForbrukningJamforelseTest.kt` — båda var tomma (samma som försök 1).
- Enligt uppgifter/MALL.md vid underkänt försök 2: Cursor implementerade `ArsJamforelse`, `jamforArsforbrukning` med sortering nyaste år först och `forandringTiondelsProcent` mot `ar - 1` via heltalsavrundning (samma `avrundaKvot`-mönster som i portföljens nyckeltalsregler).
- Tester: fyra `@Test` med `org.junit`, `assertEquals` mot hela listor för alla exemplen i uppgiften.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI och inga hårdkodade användarsträngar.

## Rättat av Cursor
- Hela innehållet i `ForbrukningJamforelse.kt` och `ForbrukningJamforelseTest.kt` (lokal modell levererade tomma filer på försök 2).

## Vid underkänd: försök 2
(ingen)

## Frågor till Claude
- inga
