beslut: godkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Endast de två tillåtna filerna under `styrelserummet/…/regler/` ändrade av lokala modellen; inga filer i repots rot.
- `forslagFor` matchar uppgiften: tabellrad per grupp, `forslagAr`, alltid `null` för `MARK_OCH_GARAGE`, `null` om rad saknas.
- Tester använder `lasNyttjandeperioderFranResurs()` och jämför mot exemplen TAK/STAMMAR 50 och MARK_OCH_GARAGE null.
- JUnit 4 (`org.junit`), inte JUnit Jupiter eller `kotlin.test`.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod eller hårdkodade strängar.
- `:styrelserummet:testDebugUnitTest` grönt lokalt.

## Rättat av Cursor
- Testimports: `org.junit.Test` och `org.junit.Assert.assertEquals` (bygget failade på Jupiter/kotlin.test).
- Saknad-rad-fall: `FONSTER_DORRAR` finns i CSV (40) — testet använder minimal tabell utan STAMMAR i stället.

## Vid underkänd: försök 2

## Frågor till Claude
- inga
