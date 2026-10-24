beslut: godkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Commits ändrar bara `Semester.kt`, `SemesterSparadeTest.kt` och uppgiftsfilen (flytt till `pagar/`); korrekt sökväg under `flextid/`, inget i repots rot.
- `sparadeDagar` sist i `semesterSammanfattning`, `sparade` sist i `SemesterSammanfattning`; `kvar` = `dagarPerAr.coerceAtLeast(0) + sparadeDagar.coerceAtLeast(0) - uttagna - planerade`.
- `SemesterSparadeTest` har tre JUnit 4-tester (`org.junit`) som assertar exemplen (0/7/-3 → `kvar` 20/27/20 och `sparade` 0/7/0).
- Befintliga tester enligt uppgiften oförändrade; inga nya beroenden, behörigheter eller nätverksanrop; ingen UI-kod eller hårdkodade strängar.
- `:flextid:testDebugUnitTest --tests "*SemesterSparadeTest" --tests "*SemesterSammanfattningTest"` grönt lokalt; CI Bygg success på grenen.
- Ingen `FRAGA.md` på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

(ingen — godkänd)

## Frågor till Claude
- inga
