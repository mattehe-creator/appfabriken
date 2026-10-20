beslut: underkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Ingen implementation: `Anmarkningar.kt` oförändrad mot main; procentregeln saknas.
- `AnmarkningarTest.kt` finns inte (uppgiften kräver ny testfil med `anmarkningSummaProcent`).
- Endast uppgiftsfilen flyttades från `uppgifter/ny/` till `uppgifter/pagar/` — ingen kodcommit.
- Testet `*AnmarkningarTest.anmarkningSummaProcent` kan inte köras; inga nya beroenden eller behörigheter tillagda.
- Ingen FRAGA.md på grenen.

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

# Uppgift: Anmärkning vid extrem avstämning i procent

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- Ingen kod ändrades: grenen innehåller bara flytt av uppgiftsfilen till `uppgifter/pagar/`.
- Skapa **båda** filerna i uppgiften: utöka `Anmarkningar.kt` och **ny** `AnmarkningarTest.kt` under `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/` (inte i repots rot).
- Testklassen ska använda JUnit 4: `import org.junit.Test`, `import org.junit.Assert.*` — inte `kotlin.test` eller JUnit Jupiter.
- Testmetoden ska heta `anmarkningSummaProcent` och **asserta hela anmärkningssträngarna** (eller minst att rätt delsträngar finns) för exemplen nedan, inte bara att listan är icke-tom.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt

## Läs som förebild (ändra inte)
- `Anmarkningar.kt` — befintlig avstämningsanmärkning när `!avstamning.stammer` ska finnas kvar oförändrad.
- `styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/regler/Avstamning.kt` — `summa` är summan av komponenternas `anskaffningsvarde`.
- `styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/regler/ReglerTest.kt` — JUnit 4 och enkel komponenthjälpare.

## Uppgift
Utöka `fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>): List<String>` så att när `utkast.byggnadensVarde` **inte är null** och `utkast.avstamning` finns, **utöver** befintliga anmärkningar (differens m.m.) läggs till:

1. **En** svensk anmärkning när `avstamning.summa` är **strikt större** än 150 procent av byggnadens värde (`summa > byggnadensVarde * 150 / 100`). Formuleringen ska tydligt säga att komponenternas summa **överstiger 150 procent** av byggnadens redovisade värde (nämn gärna beloppen i kr).
2. **En separat** svensk anmärkning när `avstamning.summa` är **strikt mindre** än 50 procent (`summa < byggnadensVarde * 50 / 100`). Formuleringen ska tydligt säga att summan **understiger 50 procent** — annan formulering än punkt 1.

Om `byggnadensVarde` är null: inga procent-anmärkningar.

Gränser: exakt 150 procent (t.ex. summa 15_000_000 mot byggnad 10_000_000) ger **ingen** över-150-anmärkning. Exakt 50 procent ger **ingen** under-50-anmärkning.

## Exempel
- Byggnad `10_000_000`, komponentsumma `16_000_000` → minst en anmärkning om att summan överstiger 150 procent (plus ev. befintlig differensanmärkning).
- Byggnad `10_000_000`, summa `4_000_000` → minst en anmärkning om att summan understiger 50 procent.
- Byggnad `10_000_000`, summa `10_000_000` → inga procent-anmärkningar (kan fortfarande finnas andra anmärkningar).

## Test som ska gå igenom
gradle :styrelserummet:testDebugUnitTest --tests "*AnmarkningarTest.anmarkningSummaProcent"

Bygg minimala `Utkast`-objekt (t.ex. med `Forening`, tom `komponenter`-lista om du bara sätter `avstamning` manuellt, tom `anmarkningar`, enkel `ArsredovisningUtdrag` om konstruktorn kräver det — läs `Utkast.kt`). Anropa `anmarkningarFor(utkast, emptyList())` och jämför mot exemplen ovan med `assertTrue(anmarkningar.any { … })` eller `assertEquals` på förväntade delsträngar.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.

## Frågor till Claude
- inga
