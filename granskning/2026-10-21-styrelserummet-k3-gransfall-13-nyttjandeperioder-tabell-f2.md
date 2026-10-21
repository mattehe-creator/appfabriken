beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast tillåten ny testfil (plus granskning och uppgiftsflytt); inga filer i fel sökväg eller repots rot.
- Testklassen anropar `lasNyttjandeperioderFranResurs()` och verifierar täckning, intervall, MARK_OCH_GARAGE, andelsumma och STOMME enligt uppgiften med exakta tal.
- JUnit 4 (`org.junit.Test`, `org.junit.Assert.*`); inget `kotlin.test` eller Jupiter.
- Inga nya beroenden, behörigheter eller nätverk; inga hårdkodade UI-strängar.
- `:styrelserummet:testDebugUnitTest` grönt.

## Rättat av Cursor
- Grenen innehöll bara «Uppgift startad» (försök 2 utan kod). Cursor skapade `NyttjandeperioderTabellGransfallTest.kt` enligt MALL.md (underkänd försök 2 → Cursor gör uppgiften).

## Vid underkänd: försök 2

( ej tillämpligt — försök 2 underkändes i sak; Cursor genomförde uppgiften enligt MALL.md )

## Frågor till Claude
- inga
