# Uppgift: CSV för årsöversikten

uppdrag: uppdrag/claude/2026-10-22-flextid-arsoversikt.md
försök: 2
beror-på: 2026-10-22-flextid-ar-01-oversikt

## Vad som saknades
Försök 1 committade enbart «Uppgift startad» (flytt till `uppgifter/pagar/`). Filerna `flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsv.kt` och `flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsvTest.kt` skapades inte. Implementera enligt avsnitten nedan och verifiera med test mot exemplen.

Innan du börjar: hämta senaste `main`. Uppgiften använder `ManadsRad` från `ArsOversikt.kt` (samma paket `se.tmconnect.flextid.regler`). Den filen måste finnas på grenen så att projektet kompilerar — vänta med start tills `2026-10-22-flextid-ar-01-oversikt-f2` är mergad till `main`, eller rebasea på `main` efter det.

## Ändra bara dessa filer
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsv.kt (ny)
- flextid/src/test/kotlin/se/tmconnect/flextid/regler/ArsOversiktCsvTest.kt (ny)

## Läs som förebild (ändra inte)
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/ArsOversikt.kt
- flextid/src/main/kotlin/se/tmconnect/flextid/regler/TidFormatering.kt

## Uppgift
fun arsOversiktCsv(
    rader: List<ManadsRad>,
    rubriker: List<String>,   // 4 rubriker: månad, arbetat, utfall, saldo
    manadsnamn: List<String>, // 12 namn, index 0 = januari
    locale: Locale,
): String

- Semikolon som avgränsare, `\n` som radslut, **även efter sista raden** (slutsträngen ska sluta med `\n`).
- Första raden: rubrikerna i ordning, joinade med `;`.
- Sedan en rad per element i `rader` (i samma ordning som listan): `manadsnamn[manad - 1]`, `formateraMinuterSomTid(arbetadMin, locale)`, `formateraSaldoMinuter(utfallMin, locale)`, `formateraSaldoMinuter(saldoVidSlutMin, locale)`.
- Om `rubriker.size != 4` eller `manadsnamn.size != 12`: kasta `IllegalArgumentException` (meddelande spelar ingen roll).

## Exempel
Rubriker `Månad`, `Arbetat`, `Utfall`, `Saldo`, svenska månadsnamn jan–dec, `locale = Locale.forLanguageTag("sv-SE")`.
Rader `ManadsRad(1, 510, 30, 150)` och `ManadsRad(3, 420, -60, 90)` ger exakt:
Månad;Arbetat;Utfall;Saldo
januari;8:30;+0:30;+2:30
mars;7:00;-1:00;+1:30

(sista tecknet är radbrytning efter `+1:30`.)

## Test som ska gå igenom
gradle :flextid:testDebugUnitTest --tests "*ArsOversiktCsvTest"

Skriv minst tre `@Test` med `org.junit.Test` och `org.junit.Assert.assertEquals` (inte `kotlin.test`):
1. Exemplet ovan — jämför hela CSV-strängen (inklusive avslutande `\n`) mot förväntat värde.
2. `rubriker` med 3 element → `IllegalArgumentException`.
3. `manadsnamn` med 11 element → `IllegalArgumentException`.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Använda `kotlin.test`.
