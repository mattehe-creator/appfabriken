beslut: underkänd
granskare: cursor
datum: 2026-10-24

## Kontrollerat
- Lokala modellens kodcommit ändrar bara `flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaliseringTest.kt` (rätt sökväg, ingen fil i repots rot). Uppgiftsfilen flyttades till `uppgifter/pagar/` i startcommit.
- PR-diff mot `main` innehåller många andra filer från grenens bas (Claude-merge som ännu inte ligger på `main`); det är inte lokala modellens ändringar enligt commit `44f5f479` / `b2c0ac10`.
- `NormaliseringTest.kt` är tom (0 byte): inga tester, inga `assertEquals` mot uppgiftens exempel, inget som verifierar `normaliseraVanligStartMin`, `normaliseraVanligSlutMin`, `normaliseraVanligRastMin`, `arGiltigtStamplaInPaminnelseMinEfterMidnatt` eller `veckodagBit`.
- Inga nya beroenden, behörigheter eller nätverk i lokala modellens filer.
- CI på grenen: `failure` — `:husloggen:testDebugUnitTest` (t.ex. `SakerhetskopiaWorkerTest.setUp`), inte Flextid-normalisering. Uppgiftens testkommando skulle ändå inte täcka beteendet tills testfilen är ifylld.
- `testDebugUnitTest` kunde inte köras lokalt i denna miljö (saknar Android SDK); bedömningen bygger på källkod och CI-logg.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Test av normalisering av sparade inställningar i Flextid

## Vad som saknades
- `flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaliseringTest.kt` fanns som ny fil men var helt tom — inga JUnit 4-tester, inga assertioner mot exemplen i uppgiften.
- Commit-meddelandet nämnde "okommittade ändringar efter Aider"; testkoden ska ligga i testfilen och vara komplett.

uppdrag: uppdrag/claude/2026-10-24-testtackning-natt.md
försök: 2
beror-på: ingen

## Ändra bara dessa filer
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/NormaliseringTest.kt (ny/ifyll)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/VanligDagForslag.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/StamplaInPaminnelse.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/Arbetsdagar.kt
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/StamplaInPaminnelseTest.kt (JUnit 4-stil)

## Uppgift
Skriv klassen `NormaliseringTest` med JUnit 4 (`org.junit.Test`, `org.junit.Assert.assertEquals` / `assertTrue` / `assertFalse`). Importera funktionerna från `se.tmconnect.flextid.regler` — de ligger redan i produktionskoden ovan.

Minst en `@Test` per funktion som ska täckas, och **varje rad under Exempel** ska motsvaras av minst ett `assertEquals` (eller assertTrue/assertFalse för boolean).

Funktioner:
- `normaliseraVanligStartMin(sparad: Int?): Int`
- `normaliseraVanligSlutMin(sparad: Int?): Int`
- `normaliseraVanligRastMin(sparad: Int?): Int`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(min: Int): Boolean`
- `veckodagBit(veckodag: DayOfWeek): Int` — använd `java.time.DayOfWeek`

## Exempel
- `normaliseraVanligStartMin(null)` → `480`
- `normaliseraVanligStartMin(-5)` → `0`
- `normaliseraVanligStartMin(2000)` → `1439`
- `normaliseraVanligStartMin(450)` → `450`
- `normaliseraVanligSlutMin(null)` → `990`
- `normaliseraVanligRastMin(null)` → `30`
- `normaliseraVanligRastMin(-10)` → `0`
- `normaliseraVanligRastMin(45)` → `45`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(300)` → `true` (05:00)
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(720)` → `true` (12:00)
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(750)` → `false`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(299)` → `false`
- `arGiltigtStamplaInPaminnelseMinEfterMidnatt(315)` → `false` (inte hel eller halv timme)
- `veckodagBit(DayOfWeek.MONDAY)` → `1`
- `veckodagBit(DayOfWeek.SUNDAY)` → `64`

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*NormaliseringTest"

## Gör inte
- Ändra andra filer.
- Använd `kotlin.test` — endast JUnit 4 (`org.junit`).
```

## Frågor till Claude
- inga
