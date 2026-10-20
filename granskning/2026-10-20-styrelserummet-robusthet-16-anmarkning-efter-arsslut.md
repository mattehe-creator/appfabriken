beslut: underkänd
granskare: cursor
datum: 2026-10-20

## Kontrollerat
- Endast `AnmarkningarTest.kt` (tom fil) och flytt av uppgiftsfil till `pagar/` ändrats; `Anmarkningar.kt` och `Utkast.kt` är oförändrade trots att de ska ändras.
- Uppgiftens beteende saknas: ingen parameter `arsslut` i `anmarkningarFor`, ingen regel för anskaffningsår efter räkenskapsårets slut, inget uppdaterat anrop med `nuAr`.
- Testet `anmarkningAnskaffningsarEfterArsslut` finns inte; testfilen är tom och täcker inga exempel.
- Inga nya beroenden, behörigheter eller nätverk.
- Ingen UI-kod tillagd.
- `:styrelserummet:testDebugUnitTest` grönt på grenen (tom testfil kompilerar).

## Rättat av Cursor
- inget

## Vid underkänd: försök 2

```
# Uppgift: Anmärkning anskaffningsår efter räkenskapsåret

uppdrag: uppdrag/2026-10-20-styrelserummet-robusthet-handlingar.md
försök: 2
beror-på: ingen

## Vad som saknades
- `Anmarkningar.kt` och `Utkast.kt` ändrades inte alls.
- `AnmarkningarTest.kt` skapades som tom fil utan testklass eller testmetod.
- Parametern `arsslut`, den nya anmärkningsregeln och anropet `anmarkningarFor(utkast, tabell, nuAr)` saknas.

## Ändra bara dessa filer
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/AnmarkningarTest.kt
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Utkast.kt

## Läs som förebild (ändra inte)
- styrelserummet/src/test/kotlin/se/tmconnect/styrelserummet/underlag/ByggUtkastTest.kt (JUnit 4, `org.junit.Test` och `org.junit.Assert`)
- styrelserummet/src/main/kotlin/se/tmconnect/styrelserummet/underlag/Anmarkningar.kt (befintliga anmärkningssträngar och stil)

## Uppgift
1. Ändra signaturen till:
   `fun anmarkningarFor(utkast: Utkast, tabell: List<NyttjandeperiodRad>, arsslut: Int): List<String>`
   (`arsslut` ska vara sista parametern.)
2. I loopen över `utkast.komponenter`: om `komponent.anskaffningsar > arsslut`, lägg till en **svensk** anmärkning som nämner komponentens namn och att anskaffningsåret ligger efter räkenskapsårets slut (`arsslut`). Befintliga anmärkningar ska finnas kvar oförändrade.
3. I `ByggUtkast.byggUtkast`, raden som anropar `anmarkningarFor`: skicka in `nuAr` som tredje argument (`arsslut`).

## Exempel
- `arsslut = 2025`, komponent med `anskaffningsar = 2026` → minst en anmärkning som nämner komponentnamnet och att anskaffningsåret är efter 2025 (räkenskapsårets slut).
- `arsslut = 2025`, komponent med `anskaffningsar = 2025` → ingen anmärkning **för den här regeln** (andra regler kan fortfarande ge anmärkningar).
- `arsslut = 2025`, komponent med `anskaffningsar = 2024` → ingen anmärkning för den här regeln.

## Test som ska gå igenom
gradle :styrelserummet:test --tests "*AnmarkningarTest.anmarkningAnskaffningsarEfterArsslut"

Skriv testet med JUnit 4 (`org.junit.Test`, `org.junit.Assert`), inte `kotlin.test`. Bygg ett minimalt `Utkast` (t.ex. `Forening`, en `Komponent` med `anskaffningsar = 2026`, tom eller enkel `tabell` via `lasNyttjandeperioderFranResurs()`), anropa `anmarkningarFor(utkast, tabell, arsslut = 2025)` och asserta att en anmärkning finns för 2026-fallet och att `anskaffningsar = 2025` inte triggar den regeln.

## Gör inte
- Ändra andra filer.
- Lägga till beroenden.
- Ändra `Komponent` eller `Forening`.
```

## Frågor till Claude
- inga
