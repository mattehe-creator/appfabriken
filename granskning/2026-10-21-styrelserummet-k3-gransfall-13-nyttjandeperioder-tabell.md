beslut: underkänd
granskare: cursor
datum: 2026-10-21

## Kontrollerat
- Diff mot main: endast flytt av uppgiftsfil från `uppgifter/ny/` till `uppgifter/pagar/` (commit «Uppgift startad»); ingen testkod.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabellGransfallTest.kt` **saknas** — uppgiftens enda tillåtna fil är inte skapad; inga filer felplacerade i repots rot.
- Uppgiftens beteende (rader per `Komponentgrupp`, intervall, andelsumma, STOMME-exempel) är inte testat; `gradle :styrelserummet:test --tests "*NyttjandeperioderTabellGransfallTest"` hittar ingen testklass.
- Inga nya beroenden, behörigheter eller nätverk; ingen UI-kod i diffen.
- Befintliga enhetstester: `:styrelserummet:testDebugUnitTest` grönt lokalt (ingen regression från denna gren).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Standardtabellen nyttjandeperioder.csv

uppdrag: uppdrag/2026-10-21-styrelserummet-k3-gransfall.md
försök: 2
beror-på: ingen

## Vad som saknades
- PR #364 / grenen innehöller bara commit «Uppgift startad» — flytt av uppgiftsfilen till `uppgifter/pagar/`.
- **`NyttjandeperioderTabellGransfallTest.kt` skapades inte.** Uppgiften kräver en ny testklass som verifierar `nyttjandeperioder.csv` via `lasNyttjandeperioderFranResurs()` (eller `lasNyttjandeperioder` på filinnehållet).

## Ändra bara dessa filer
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabellGransfallTest.kt (ny)

Om test visar fel i data eller parser får du **högst en** av följande läggas till under «Ändra bara dessa filer» och ändras (enligt uppgift försök 1):
- styrelserummet/src/main/resources/nyttjandeperioder.csv
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt

Håll total diff inom cirka 150 rader.

## Läs som förebild (ändra inte)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperioderTabell.kt
- styrelserummet/src/main/resources/nyttjandeperioder.csv
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/modell/Komponentgrupp.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/NyttjandeperiodForslagTest.kt (JUnit 4, `lasNyttjandeperioderFranResurs()`)

## Uppgift
Skapa testklassen `NyttjandeperioderTabellGransfallTest` med **JUnit 4** (`import org.junit.Test`, `import org.junit.Assert.*` — **inte** `kotlin.test` eller JUnit Jupiter).

Anropa `lasNyttjandeperioderFranResurs()` och verifiera:

1. **Täckning:** Varje värde i `Komponentgrupp` (12 st) ska ha minst en rad i den parsade tabellen. Asserta t.ex. `tabell.size >= 12` och att varje enum-värde förekommer som `rad.grupp`.
2. **Intervall:** För varje rad där `intervallAr != null`: `intervallAr!!.first <= forslagAr <= intervallAr.last`.
3. **MARK_OCH_GARAGE:** Raden ska finnas. `standardandelProcent == 0`, `forslagAr == 0`, `intervallAr == null` (kolumnen `0` i CSV tolkas som null i `parseIntervall`).
4. **Andelsumma:** Summan av `standardandelProcent` för alla rader **utom** `Komponentgrupp.MARK_OCH_GARAGE` ska vara **100**.
5. **STOMME (konkret exempel):** Hitta raden för `Komponentgrupp.STOMME` och asserta `forslagAr == 100`, `intervallAr == 75..120`, `standardandelProcent == 63`.

Gärna separata `@Test`-metoder (t.ex. `enRadPerGrupp`, `forslagInomIntervall`, `markOchGarageNollAndel`, `summaAndelarUtanMark`, `stommeEnligtExempel`) så att fel är lätta att lokalisera.

## Exempel
- Efter parsning: minst **12** rader.
- STOMME: forslag **100** inom **75–120**, andel **63**.
- Summa andelar exkl. mark = **100** (63+6+6+5+3+4+6+2+1+1+3 med nuvarande CSV).

Testerna ska **asserta exakt** dessa tal (inte bara «> 0» eller «ungefär 100»).

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*NyttjandeperioderTabellGransfallTest"

(Cursor kör `:styrelserummet:testDebugUnitTest` vid granskning.)

## Gör inte
- Ändra `NyttjandeperiodForslagTest.kt` eller andra befintliga testfiler (parallell kö).
- Ändra `uppgifter/`, `granskning/` eller filer i repots rot.
- Lägga till beroenden.

## Frågor till Claude
- inga
